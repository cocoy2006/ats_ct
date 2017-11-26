package molab.main.java.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import molab.main.java.dao.ApplicationDao;
import molab.main.java.dao.CtDispatcherDao;
import molab.main.java.dao.CtRunnerDao;
import molab.main.java.dao.CtScreenshotDao;
import molab.main.java.dao.CtSubRunnerDao;
import molab.main.java.dao.DeviceDao;
import molab.main.java.entity.Application;
import molab.main.java.entity.CtDispatcher;
import molab.main.java.entity.CtRunner;
import molab.main.java.entity.CtScreenshot;
import molab.main.java.entity.CtSubRunner;
import molab.main.java.entity.Device;
import molab.main.java.util.HttpUtil;
import molab.main.java.util.ImageUtil;
import molab.main.java.util.Molab;
import molab.main.java.util.Path;
import molab.main.java.util.Status;
import molab.main.java.util.init.Adb;
import molab.main.java.util.shell.ShellHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;

@Service
public class ReadyTimerService {

	private static final Logger log = Logger.getLogger(ReadyTimerService.class.getName());
	private static final long MONKEY_TIMEOUT = 120000;
	
	@Autowired
	private ApplicationDao ad;
	
	@Autowired
	private DeviceDao devDao;
	
	@Autowired
	private CtDispatcherDao disDao;
	
	@Autowired
	private CtRunnerDao rd;
	
	@Autowired
	private CtSubRunnerDao srd;
	
	@Autowired
	private CtScreenshotDao ssd;
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void run() throws IOException {
		List<CtRunner> runnerList = rd.findByState(Status.RunnerStatus.READY.getInt(), 
				Molab.getInstance().getProperty(Molab.CFG_SELF_SERVER), 
				Integer.parseInt(Molab.getInstance().getProperty(Molab.CFG_SELF_PORT)));
		if(runnerList != null && runnerList.size() > 0) {
			Adb adb = Adb.getInstance();
			ThreadGroup tGroup = new ThreadGroup("cts");
			for(CtRunner runner : runnerList) {
				Device device = devDao.findByRunnerId(runner.getId());
				IDevice iDevice = adb.getBackend().findAttacedDevice(device.getSn());
				if(iDevice == null || !iDevice.isOnline() || !Molab.isSystemRunning(iDevice)) {
					log.severe(String.format("Device %s is not ready.", device.getSn()));
					continue;
				} else {
					Molab.lock(iDevice);
					CtDispatcher dispatcher = disDao.findById(runner.getDispatcherId());
					runner.setState(Status.RunnerStatus.RUNNING.getInt());
					rd.update(runner);
					Thread t = new Thread(tGroup, new Running(this, runner, dispatcher, iDevice, ad.findByRunnerId(runner.getId())), 
							"CTS Thread " + runner.getId());
					t.start();
				}
			}
			while(tGroup.activeCount() > 0) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					log.severe(e.getMessage());
				}
			}
			for(CtRunner runner : runnerList) {
				rd.update(runner);
			}
			clear();
		}
	}
	
	class Running extends Thread {
		
		private ReadyTimerService service;
		private CtRunner runner;
		private CtDispatcher dispatcher;
		private IDevice iDevice;
		private Application app;
		private int uid = 0, pid = 0;
		private List<Float> cpus = null;
		private List<Long> memorys = null;
		private BufferedImage preImage = null;
		private List<CtScreenshot> ssList = null;
		private List<CtSubRunner> srList = null;
		
		public Running(ReadyTimerService service, CtRunner runner, CtDispatcher dispatcher, IDevice iDevice, Application app) {
			this.service = service;
			this.runner = runner;
			this.dispatcher = dispatcher;
			this.iDevice = iDevice;
			this.app = app;
		}

		@Override
		public void run() {
			try {
				prepare();
				cts(); // Compatibility Test Suite
				runner.setAverageCpu(Molab.averageCpu(cpus));
				runner.setAverageMemory(Molab.averageMemory(memorys));
				if(srList.size() > 0) {
					CtSubRunner sr = srList.get(srList.size() - 1);
					runner.setUptraffic(sr.getUptraffic());
					runner.setDowntraffic(sr.getDowntraffic());
				}
				runner.setState(Status.RunnerStatus.END.getInt());
				// update dispatcher if necessary
				CtDispatcher dispatcher = service.disDao.findById(runner.getDispatcherId());
				// find dispatcher
				if(dispatcher != null && 
						dispatcher.getState() == Status.DispatcherStatus.START.getInt()) {
					dispatcher.setState(Status.DispatcherStatus.REPORTING.getInt());
					service.disDao.update(dispatcher);
					log.info(String.format("Dispatcher with id %d swift to REPORTING.", dispatcher.getId()));
				}
				// save sub runner
				for(CtSubRunner sr : srList) {
					service.srd.save(sr);
				}
				// save screenshot
				for(CtScreenshot ss : ssList) {
					service.ssd.save(ss);
				}
			} catch(Exception e) {
				// TODO warning is needed
				runner.setState(Status.RunnerStatus.READY.getInt());
				log.severe(e.getMessage());
			} finally {
				try {
					Molab.release(iDevice);
				} catch (IOException e) {}
			}
		}
		
		private void prepare() {
			cpus = new ArrayList<Float>();
			memorys = new ArrayList<Long>();
			ssList = new ArrayList<CtScreenshot>();
			srList = new ArrayList<CtSubRunner>();
		}
		
		private void cts() throws IOException {
			boolean installSuccess = Adb.install(runner, iDevice, Path.apk(app.getAliasname()));
			
			// launch
			boolean loadSuccess = false;
			if(installSuccess) {
				
				Adb.light(iDevice);
				Adb.pressBack(iDevice);
				
				loadSuccess = Adb.load(runner, iDevice, app.getPackagename(), app.getStartactivity());
			}
			
			if(loadSuccess) {
				pid = ShellHelper.pid(iDevice, app.getPackagename());
				uid = ShellHelper.uid(iDevice, app.getPackagename());
				hold();
				// in order to skip homepage
				Adb.swipe(iDevice, 5);
				// clear logcat cache
				Adb.logcatClear(iDevice);
				
				// monkey test
				Thread monkeyThread = new Thread(new Runnable() {

					@Override
					public void run() {
						Adb.monkey(iDevice, app);
					}
					
				}, "Monkey Thread " + runner.getId());
				monkeyThread.start();
				// sleep a while in order to get more stable data
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
				// performance & screencap start
				Timer srTimer = new Timer("Performance Thread " + runner.getId());
				try {
					srTimer.scheduleAtFixedRate(new TimerTask() {

						@Override
						public void run() {
							performance();
							screencap();
						}
						
					}, 0, 2000);
					
					Thread.sleep(MONKEY_TIMEOUT); // interrupt after timeout
					
					monkeyThread.interrupt();
					monkeyThread.join(2000);
					log.info("Monkey and Performance done.");
				} catch(Exception e) {
					log.severe(e.getMessage());
				} finally {
					srTimer.cancel();
				}
			}
			// uninstall
			if(installSuccess) {
				Adb.uninstall(runner, iDevice, app.getPackagename());
			}
			// logcat anyway
			logcat(iDevice, runner, app);
		}
		
		private void hold() {
			if(dispatcher != null && dispatcher.getHoldTime() > 0) {
				try {
					Thread.sleep(dispatcher.getHoldTime() * 1000);
				} catch (InterruptedException e) {}
			}
		}
		
		private void performance() {
			CtSubRunner sr = ShellHelper.performance(runner, iDevice, app, pid, uid, cpus, memorys);
			if(sr != null) {
				srList.add(sr);
			}
		}
		
		private int ssCount = 1;
		private void screencap() {
			try {
				String activityName = Adb.currentActivity(iDevice, app.getPackagename());
				if(activityName != null) {
//				if(Adb.isFront(iDevice, app.getPackagename())) {
					String name = runner.getId() + "." + ssCount + ".png";
					String tmp = Path.temp(name);
					File png = Adb.screencap(iDevice, tmp);
					BufferedImage bImage = ImageIO.read(png);
					if(bImage != null) {
						bImage = ImageUtil.zoom(bImage);
						ByteArrayOutputStream baso = new ByteArrayOutputStream();
						ImageIO.write(bImage, "png", baso);
						// eliminate duplicate data
						if(preImage == null || !ImageUtil.isSame(preImage, bImage, 0.99)) {
							preImage = bImage;
							// post to file server
							String host = Molab.host() + "ctApi/ss";
							HttpUtil.post(host, png);
							// write into database
							CtScreenshot ss = new CtScreenshot();
							ss.setRunnerId(runner.getId());
							ss.setImage(name);
							ss.setActivityName(activityName);
							ss.setOprTime(System.currentTimeMillis());
							ssList.add(ss);
							ssCount++;
						}
					}
				}
			} catch(Exception e) {
				log.severe(e.getMessage());
			}
		}
		
		
	}
	
	private void logcat(IDevice iDevice, CtRunner runner, Application app) {
		String host = Molab.host() + "ctApi/log";
		try {
			// monkey
			String monkeyFileName = runner.getId() + ".monkey.log";
			String tmp = Path.temp(monkeyFileName);
			File monkey = Adb.monkey(iDevice, tmp);
			if(monkey != null && monkey.exists()) {
				HttpUtil.post(host, monkey);
			}
			log.info("Monkey.log success.");
		} catch(IOException | SyncException | TimeoutException | AdbCommandRejectedException e) {
			log.severe("Monkey.log failure, root cause: " + e.getMessage());
		}
		try {
			// logcat
			String logcatFileName = runner.getId() + ".logcat.log";
			String tmp = Path.temp(logcatFileName);
			File logcat = Adb.logcat(iDevice, tmp);
			if(logcat != null && logcat.exists()) {
				HttpUtil.post(host, logcat);
			}
			log.info("Logcat.log success.");
		} catch(IOException | SyncException | TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException e) {
			log.severe("Logcat.log failure, root cause: " + e.getMessage());
		}
		try {
			// anr
			String anrFileName = runner.getId() + ".anr.log";
			String tmp = Path.temp(anrFileName);
			File anr = Adb.anr(iDevice, tmp);
			if(anr != null && anr.exists()) {
				HttpUtil.post(host, anr);
			}
			log.info("Anr.log success.");
		} catch(IOException | SyncException | TimeoutException | AdbCommandRejectedException e) {
			log.severe("Anr.log failure, root cause: " + e.getMessage());
		}
	}

	private void clear() {
		try {
			File temp = new File(Path.temp());
			if(temp.exists() && temp.isDirectory()) {
				File[] files = temp.listFiles();
				for(File file : files) {
					file.delete();
				}
			}
		} catch(Exception e) {}
	}
	
}
