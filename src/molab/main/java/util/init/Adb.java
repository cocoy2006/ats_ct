package molab.main.java.util.init;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import molab.main.java.entity.Application;
import molab.main.java.entity.CtRunner;
import molab.main.java.util.KeyCode;
import molab.main.java.util.Molab;
import molab.main.java.util.Status.CommandType;

import org.apache.commons.lang.StringUtils;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.android.monkeyrunner.adb.AdbBackend;

public class Adb {

	private static final Logger log = Logger.getLogger(Adb.class.getName());
	public static final String PM_LIST_PACKAGES = "pm list packages -3";
	private static final String AM_START = "am start -W -n %s/%s"; // @param1 package name, param2 start activity name
	private static final String LIGHT = "input tap 1 1";
	private static final String LOGCAT_CLEAR = "logcat -c";
	private static final String TMP = "/data/local/tmp/";
	private static final String MONKEY_TEMP = TMP + "monkey.log";
	private static final String MONKEY = "monkey -p %s --throttle 300 --kill-process-after-error -v -v -v 300 > " + MONKEY_TEMP; // @param package name
	private static final String LOGCAT_TEMP = TMP + "logcat.log";
	private static final String LOGCAT = "logcat -d -v brief > " + LOGCAT_TEMP;
	private static final String ANR = "/data/anr/traces.txt";
	private static final String PNG_TEMP = TMP + "tmp.png";
	private static final String SCREENCAP = "/system/bin/screencap -p " + PNG_TEMP;
	private static final String KEYEVENT = "input keyevent %d";
	private static final String SWIPE = "input swipe %d %d %d %d";
	private static final String UNINSTALL = "pm uninstall %s"; // @param package name
	private static final String IP = "ip -f inet addr show wlan0";
	private static final String GET_PROP = "getprop %s";
	
	private static Adb adb = null;
	private static AdbBackend backend = null;
	private static Set<String> whiteList = new HashSet<String>() {{
		add("com.android.adbkeyboard");
	}};

	private Adb() {
		backend = new AdbBackend();
	}

	public static Adb getInstance() {
		if (adb == null) {
			synchronized (Adb.class) {
				adb = new Adb();
			}
		}
		return adb;
	}
	
	public AdbBackend getBackend() {
		return backend;
	}
	
	public static String getprop(IDevice iDevice, String prop) {
		try {
			if(prop == null) {
				prop = IDevice.PROP_BUILD_DISPLAY;
			}
			String result = executeSync(iDevice, CommandType.SHELL, String.format(GET_PROP, prop));
			if(!StringUtils.isBlank(result)) {
				return result.trim();
			}
		} catch (IOException e) {
			log.severe(String.format("Property %s cannot be found.", prop));
		}
		return null;
	}
	
//	public static void pressHome(IDevice iDevice) throws IOException {
//		executeSync(iDevice, CommandType.SHELL, String.format(KEYEVENT, KeyCode.KEYCODE_HOME));
//	}
	
	public static void pressBack(IDevice iDevice) throws IOException {
		executeAsync(iDevice, CommandType.SHELL, String.format(KEYEVENT, KeyCode.KEYCODE_BACK));
	}
	
	public static void swipe(IDevice iDevice, int count) {
		try {
			RawImage rImage = iDevice.getScreenshot();
			int width = rImage.width;
			int height = rImage.height;
			// left swipe
			swipe(iDevice, count, width - 50, height / 2, 50, height / 2);
			// up swipe
//			swipe(iDevice, count, width / 2, height - 50, width / 2, 50);
		} catch (TimeoutException | AdbCommandRejectedException
				| IOException e) {}
	}
	
	private static void swipe(IDevice iDevice, int count, int x1, int y1, int x2, int y2) throws IOException {
		while(count-- > 0) {
			swipe(iDevice, x1, y1, x2, y2);
		}
	}
	
	public static void swipe(IDevice iDevice, int x1, int y1, int x2, int y2) throws IOException {
		executeAsync(iDevice, CommandType.SHELL, String.format(SWIPE, x1, y1, x2, y2));
	}
	
	public static void clear(IDevice iDevice) throws IOException {
		clearPackages(iDevice);
//		clearTraces(iDevice);
	}
	
	private static void clearPackages(IDevice iDevice) throws IOException {
		String result = executeAsync(iDevice, CommandType.SHELL, PM_LIST_PACKAGES);
		if(!StringUtils.isBlank(result)) {
			String[] pList = result.split(System.getProperty("line.separator"));
			for(String p : pList) {
				String name = p.substring(8);
				if(!whiteList.contains(name)) {
					uninstall(null, iDevice, name);
				}
			}
		}
	}
	
	public static boolean install(CtRunner runner, IDevice iDevice, String apk) throws IOException {
		boolean flag = false;
		long start = System.currentTimeMillis();
		String result = executeSync(iDevice, CommandType.INSTALL, apk);
		long time = System.currentTimeMillis() - start;
		if(result == null) {
			flag = true;
			result = "INSTALL_SUCCESS";
		}
		if(runner != null) {
			runner.setInstallResult(result);
			runner.setInstallTime(time);
		}
		log.info(String.format("安装结束: %s, 耗时 %d 毫秒", result, time));
		return flag;
	}
	
	public static boolean load(CtRunner runner, IDevice iDevice, String packageName, String startActivity) throws IOException {
		boolean flag = false;
		long start = System.currentTimeMillis();
		String result = executeSync(iDevice, CommandType.SHELL, 
				String.format(AM_START, packageName, startActivity));
		long time = System.currentTimeMillis() - start;
		if(result != null && result.contains("Complete")) {
			flag = true;
			result = "LOAD_SUCCESS";
		}
		if(runner != null) {
			runner.setLoadResult(result);
			runner.setLoadTime(time);
		}
		log.info(String.format("加载结束: %s, 耗时 %d 毫秒", result, time));
		return flag;
	}
	
	public static boolean isFront(IDevice iDevice, String packageName) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell dumpsys window w|findstr mCurrentFocus|findstr " + packageName;
		try {
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return false;
	        }
			// found
			if(stdOutput.size() > 0) {
				return true;
			}
		} catch (IOException | InterruptedException e) {}
		return false;
	}
	
	public static String currentActivity(IDevice iDevice, String packageName) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell dumpsys window w|findstr mCurrentFocus|findstr " + packageName + "/";
		try {
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return null;
	        }
			// found
			if(stdOutput.size() > 0) {
				String data = stdOutput.get(0).trim();
				return data.substring(data.indexOf("/") + 1, data.length() - 1);
			}
		} catch (IOException | InterruptedException e) {}
		return null;
	}
	
	public static String light(IDevice iDevice) throws IOException {
		return executeAsync(iDevice, CommandType.SHELL, LIGHT);
	}
	
	public static String logcatClear(IDevice iDevice) throws IOException {
		return executeAsync(iDevice, CommandType.SHELL, LOGCAT_CLEAR);
	}
	
	public static void monkey(IDevice iDevice, Application app) {
		log.info("Monkey start.");
		try {
			iDevice.executeShellCommand(String.format(MONKEY, app.getPackagename()), null);
		} catch (TimeoutException | AdbCommandRejectedException
				| ShellCommandUnresponsiveException
				| IOException e) {
			log.severe(e.getMessage());
		}
	}
	
	public static File monkey(IDevice iDevice, String path) throws SyncException, IOException, AdbCommandRejectedException, TimeoutException {
		iDevice.pullFile(MONKEY_TEMP, path);
		return new File(path);
	}
	
	public static File logcat(IDevice iDevice, String path) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException, SyncException {
		iDevice.shell(LOGCAT);
		iDevice.pullFile(LOGCAT_TEMP, path);
		return new File(path);
	}
	
	public static File anr(IDevice iDevice, String path) throws SyncException, IOException, AdbCommandRejectedException, TimeoutException {
		iDevice.pullFile(ANR, path);
		return new File(path);
	}
	
	public static File screencap(IDevice iDevice, String path) throws IOException, SyncException, AdbCommandRejectedException, TimeoutException, ShellCommandUnresponsiveException {
		iDevice.shell(SCREENCAP);
		iDevice.pullFile(PNG_TEMP, path);
		return new File(path);
	}
	
	public static String screencap(IDevice iDevice) throws IOException {
		return executeAsync(iDevice, CommandType.SHELL, SCREENCAP);
	}
	
	public static boolean uninstall(CtRunner runner, IDevice iDevice, String packageName) throws IOException {
		boolean flag = false;
		long start = System.currentTimeMillis();
		String result = executeSync(iDevice, CommandType.SHELL, 
				String.format(UNINSTALL, packageName));
		long time = System.currentTimeMillis() - start;
		if(result != null && result.contains("Success")) {
			flag = true;
			result = "UNINSTALL_SUCCESS";
		}
		if(runner != null) {
			runner.setUninstallResult(result);
			runner.setUninstallTime(time);
		}
		log.info(String.format("卸载结束: %s, 耗时 %d 毫秒", result, time));
		return flag;
	}
	
	/**
	 * @deprecated */
	public static String ip(IDevice iDevice) throws IOException {
		String result = executeAsync(iDevice, CommandType.SHELL, IP);
		log.info(String.format("无线网络情况: %s", result));
		return result;
	}
	
	public static String executeSync(IDevice iDevice, CommandType cmdType, String cmd) throws IOException {
		synchronized(Adb.class) {
			try {
				return execute(iDevice, cmdType, cmd);
			} catch (Exception e) {
				restartAdb(iDevice);
				throw new IOException("Adb crash.");
			}
		}
	}
	
	public static String executeAsync(IDevice iDevice, CommandType cmdType, String cmd) {
		try {
			return execute(iDevice, cmdType, cmd);
		} catch (IOException | InstallException | SyncException | AdbCommandRejectedException | TimeoutException | ShellCommandUnresponsiveException e) {
			log.severe("Error happen, root cause: " + e.getMessage()); 
		}
		return null;
	}
	
	private static String execute(IDevice iDevice, CommandType cmdType, String param) 
			throws InstallException, SyncException, IOException, AdbCommandRejectedException, TimeoutException, ShellCommandUnresponsiveException {
		String result = null;
		switch(cmdType) {
		case INSTALL:
			result = iDevice.installPackage(param, TMP, true);
			break;
		case SHELL:
			result = iDevice.shell(param);
			break;
		default:
			break;
		}
		return result;
	}
	
	private static void restartAdb(IDevice iDevice) {
		log.severe("Adb has down, try to reboot device " + iDevice.getSerialNumber());
		try {
//			iDevice.shell("reboot");
			iDevice.executeShellCommand("reboot", null);
			Thread.sleep(60000);
		} catch (InterruptedException | TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {}
		log.severe("Device has restarted.");
	}

}
