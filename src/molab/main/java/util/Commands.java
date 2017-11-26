package molab.main.java.util;

import java.io.IOException;
import java.util.logging.Logger;

import molab.main.java.util.init.Adb;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.monkeyrunner.adb.AdbMonkeyDevice;

public class Commands {
	
	private static final Logger log = Logger.getLogger(Commands.class.getName());
	
	private Adb adb;
	private AdbMonkeyDevice mDevice;
	private IDevice iDevice;
	
	public Commands(String sn) {
		adb = Adb.getInstance();
		iDevice = adb.getBackend().findAttacedDevice(sn);
		mDevice = adb.getBackend().waitForConnection(10000, sn);
	}
	
	public String shell(String cmd) {
		return mDevice.shell(cmd);
	}

	//CMD = INSTALL LOCALFILEPATH REMOTEPATH
	public String installPackage(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			result = iDevice.installPackage(cmds[1], cmds[2], false);
		} catch (InstallException e) {
			result = "InstallException";
			System.out.println(e.getMessage());
		}
		if(result == null) result = "INSTALL_SUCCESS";
		return result;
	}
	
	//CMD = REINSTALL LOCALFILEPATH REMOTEPATH
	public String reinstallPackage(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			result = iDevice.installPackage(cmds[1], cmds[2], true);
		} catch (InstallException e) {
			result = "InstallException";
			System.out.println(e.getMessage());
		}
		if(result == null) result = "INSTALL_SUCCESS";
		return result;
	}
	
	//CMD = UNINSTALL PACKAGE
	public String uninstallPackage(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			result = iDevice.uninstallPackage(cmds[1]);
		} catch (InstallException e) {
			e.printStackTrace();
			result = "InstallException";
		}
		if(result == null) result = "UNINSTALL_SUCCESS";
		return result;
	}
	
	//CMD = START PACKAGE/PACKAGE+ACTIVITY
	public void startActivity(String cmd) {
		String[] cmds = cmd.split(" ");
		mDevice.shell("am start -W -n " + cmds[1]);
	}

	public String executeCommand(String command) throws IOException {
		log.info("Molab command: " + command);
		String result = null;
		switch(Action.toAction(command.split(" ")[0].toUpperCase())) {
			case INSTALL:
				result = installPackage(command);
				break;
			case REINSTALL:
				result = reinstallPackage(command);
				break;
			case UNINSTALL:
				result = uninstallPackage(command);
				break;
			case START:
				startActivity(command);
				break;
			default:
				break;
		}
		return result;
	}
	
	enum Action {
		INSTALL, REINSTALL, UNINSTALL, START;
		
		public static Action toAction(String action) {
			return valueOf(action);
		}
	}
}
