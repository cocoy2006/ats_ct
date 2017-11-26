package molab.main.java.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import molab.main.java.entity.Application;
import molab.main.java.util.init.Adb;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceState;

public class Molab {

	private static final Logger log = Logger.getLogger(Molab.class.getName());
	public static final String LAST_TIME = "opoL8fPcOoiJWZf594oE5Q=="; // 2016.04.29, useless but for atsgz
	public static final String EXPIRE_TIME = "/lpB4F8INDSWu2sX6Jjm/g=="; // 2017.11.01
	public static final String CFG_SELF_SERVER = "self_server";
	public static final String CFG_SELF_PORT = "self_port";
	public static final String CFG_WEB_SERVER = "web_server";
	public static final String CFG_WEB_PORT = "web_port";
	public static final String CFG_WEB_NAME = "web_name";
	public static final String CFG_ROM_INFO = "rom_info";

	private static Molab molab = null;
	private static Properties props = null;

	public static Molab getInstance() {
		if (molab == null) {
			synchronized (Molab.class) {
				molab = new Molab();
				props = PropertiesUtil.loadProperties(Path.cfg());
			}
		}
		return molab;
	}

	public String getProperty(String key) {
		if(props.containsKey(key)) {
			return props.getProperty(key);
		}
		return null;
	}
	
	public static boolean isAppExist(Application app) {
		if(app.getAliasname() != null) {
			String apk = Path.apk(app.getAliasname());
			File file = new File(apk);
			return file.exists();
		}
		return false;
	}
	
	public static boolean isSystemRunning(IDevice iDevice) {
		String command = "cmd /C adb -s " + iDevice.getSerialNumber() + 
				" shell " + Adb.PM_LIST_PACKAGES;
		try {
			Process proc = Runtime.getRuntime().exec(command); 
			ArrayList<String> errorOutput = new ArrayList<String>();
            ArrayList<String> stdOutput = new ArrayList<String>();
            int status = grabProcessOutput(proc, errorOutput, stdOutput, true);
			if (status != 0) {
	            return false;
	        }
		} catch (IOException | InterruptedException e) {}
		return true;
	}
	
	public static float averageCpu(List<Float> cpus) {
		if (cpus.isEmpty())
			return 0;
		float sum = 0;
		for (float cpu : cpus) {
			sum += cpu;
		}
		return setAccuracy(sum / cpus.size(), 2);
	}

	public static Float setAccuracy(Float f, int scale) {
		int index = (int) Math.pow(10, scale);
		return (float) (Math.round(f * index)) / index;
	}
	
	public static long averageMemory(List<Long> memorys) {
		if (memorys.isEmpty())
			return 0;
		long sum = 0;
		for (long memory : memorys) {
			sum += memory;
		}
		return (long) Math.floor(sum / memorys.size());
	}
	
	public static void lock(IDevice iDevice) throws IOException {
		if(iDevice != null) {
			iDevice.setState(DeviceState.BUSY);
			log.info("Device state swift to BUSY.");
			// uninstall -3 apps
			Adb.clear(iDevice);
		}
	}
	
	public static void release(IDevice iDevice) throws IOException {
		if(iDevice != null) {
			iDevice.setState(DeviceState.ONLINE);
			log.info("Device state swift to ONLINE.");
		}
	}
	
	public static String host() {
		return String.format(
				"http://%s:%s/%s/", 
				getInstance().getProperty(CFG_WEB_SERVER), 
				getInstance().getProperty(CFG_WEB_PORT), 
				getInstance().getProperty(CFG_WEB_NAME));
	}
	
	/**
     * Get the stderr/stdout outputs of a process and return when the process is done.
     * Both <b>must</b> be read or the process will block on windows.
     * @param process The process to get the output from
     * @param errorOutput The array to store the stderr output. cannot be null.
     * @param stdOutput The array to store the stdout output. cannot be null.
     * @param waitForReaders if true, this will wait for the reader threads.
     * @return the process return code.
     * @throws InterruptedException
     */
    public static int grabProcessOutput(final Process process, final ArrayList<String> errorOutput,
            final ArrayList<String> stdOutput, boolean waitForReaders)
            throws InterruptedException {
        assert errorOutput != null;
        assert stdOutput != null;
        // read the lines as they come. if null is returned, it's
        // because the process finished
        Thread t1 = new Thread("") { //$NON-NLS-1$
            @Override
            public void run() {
                // create a buffer to read the stderr output
                InputStreamReader is = new InputStreamReader(process.getErrorStream());
                BufferedReader errReader = new BufferedReader(is);

                try {
                    while (true) {
                        String line = errReader.readLine();
                        if (line != null) {
                            errorOutput.add(line);
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    // do nothing.
                }
            }
        };

        Thread t2 = new Thread("") { //$NON-NLS-1$
            @Override
            public void run() {
                InputStreamReader is = new InputStreamReader(process.getInputStream());
                BufferedReader outReader = new BufferedReader(is);

                try {
                    while (true) {
                        String line = outReader.readLine();
                        if (line != null) {
                            stdOutput.add(line);
                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    // do nothing.
                }
            }
        };

        t1.start();
        t2.start();

        // it looks like on windows process#waitFor() can return
        // before the thread have filled the arrays, so we wait for both threads and the
        // process itself.
        if (waitForReaders) {
            try {
                t1.join();
            } catch (InterruptedException e) {
            }
            try {
                t2.join();
            } catch (InterruptedException e) {
            }
        }

        // get the return code from the process
        return process.waitFor();
    }

}
