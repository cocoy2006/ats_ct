package molab.main.java.util.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import molab.main.java.entity.Application;
import molab.main.java.entity.CtRunner;
import molab.main.java.entity.CtSubRunner;
import molab.main.java.util.Molab;

import com.android.ddmlib.IDevice;

public class ShellHelper {
	
	private static final Logger log = Logger.getLogger(ShellHelper.class.getName());
	
	public static CtSubRunner performance(CtRunner runner, IDevice iDevice, Application app, 
			int pid, int uid, List<Float> cpus, List<Long> memorys) {
		float cpu = cpuRate(iDevice, pid);
		cpu = cpu >= 0 ? cpu : 0;
		cpu = cpu <=100 ? cpu : 100;
		long memory = pss(iDevice, app.getPackagename());
		memory = memory >= 0 ? memory : 0;
		if(memory != 0 || cpu != 0) {
			CtSubRunner sr = new CtSubRunner();
			sr.setRunnerId(runner.getId());
			// pss
			cpus.add(cpu);
			sr.setCpu(cpu);
			// memory
			memorys.add(memory);
			sr.setMemory(memory);
			// traffic
			traffic(sr, iDevice, uid);
			return sr;
		}
		return null;
	}
	
	public static int pid(IDevice iDevice, String packageName) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell ps|findstr " + packageName;
		try {
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return 0;
	        }
			// found
			if(stdOutput.size() > 0) {
				String[] data = stdOutput.get(0).trim().split("\\ +");
				return Integer.parseInt(data[1]);
			}
		} catch (IOException | InterruptedException e) {}
		return 0;
	}
	
	public static long pss(IDevice iDevice, String packageName)	{
		MeminfoReceiver mr = new MeminfoReceiver(packageName);
		try {
			iDevice.executeShellCommand("dumpsys meminfo " + packageName, mr);
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		mr.done();
		return mr.getMemory();
	}
	
	public static float cpuRate(IDevice iDevice, int pid) {
		try {
			float totalTime1 = cpuTotalTime(iDevice);
			float appTime1 = cpuAppTime(iDevice, pid);
			Thread.sleep(100);
			float totalTime2 = cpuTotalTime(iDevice);
			float appTime2 = cpuAppTime(iDevice, pid);
			return 100 * (appTime2 - appTime1) / (totalTime2 - totalTime1);
		} catch(Exception e) {
			log.severe(e.getMessage());
		}
		return 0;
	}
	
	public static long cpuTotalTime(IDevice iDevice) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell cat /proc/stat";
		try {  
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return 0;
	        }
			// found
			if(stdOutput.size() > 0) {
				String[] data = stdOutput.get(0).trim().split("\\ +");
				return Long.parseLong(data[2])
			            + Long.parseLong(data[3]) + Long.parseLong(data[4])
			            + Long.parseLong(data[6]) + Long.parseLong(data[5])
			            + Long.parseLong(data[7]) + Long.parseLong(data[8]);
			}
		} catch (IOException | InterruptedException e) {}
		return 0;
	}
	
	public static long cpuAppTime(IDevice iDevice, int pid) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell cat /proc/" + pid + "/stat";
		try {  
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return 0;
	        }
			// found
			if(stdOutput.size() > 0) {
				String[] data = stdOutput.get(0).trim().split("\\ +");
				return Long.parseLong(data[13]) + Long.parseLong(data[14])
			            + Long.parseLong(data[15]) + Long.parseLong(data[16]);
			}
		} catch (IOException | InterruptedException e) {}
		return 0;
	}
	
	public static int uid(IDevice iDevice, String packageName) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell dumpsys package " + packageName + "|findstr userId=";
		try {  
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return 0;
	        }
			// found
			if(stdOutput.size() > 0) {
				Pattern p=Pattern.compile("userId=\\d+");
				Matcher m=p.matcher(stdOutput.get(0));
				while(m.find()) {
					return Integer.parseInt(m.group().substring(7));
				}
			}
		} catch (IOException | InterruptedException e) {}
		return 0;
	}
	
	public static void traffic(CtSubRunner sr, IDevice iDevice, int uid) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell cat /proc/net/xt_qtaguid/stats|findstr " + uid;
		try {  
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = Molab.grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return;
	        }
			// found
			if(stdOutput.size() > 0) {
				long up = 0, down = 0;
				for(String output : stdOutput) {
					if(!StringUtils.isBlank(output)) {
						String[] data = output.trim().split("\\ +");
						down += Long.parseLong(data[5]);
						up += Long.parseLong(data[7]);
					}
				}
				sr.setUptraffic(up);
				sr.setDowntraffic(down);
			}
		} catch (IOException | InterruptedException e) {}
	}
	
}
