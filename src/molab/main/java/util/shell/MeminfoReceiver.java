package molab.main.java.util.shell;

import com.android.ddmlib.MultiLineReceiver;

public class MeminfoReceiver extends MultiLineReceiver {

	private String processName = null;

	public boolean isCancelled = false;
	public long memory = 0;
	
	MeminfoReceiver(String processName) {
		super();
		setTrimLine(false);
		this.processName = processName;
	}
	
	@Override
	public void processNewLines(String[] lines) {
		if (isCancelled == false && processName != null) {
			for (String line : lines) {
				if(line.indexOf("TOTAL") > -1) {
					String[] indexs = line.trim().split("\\ +");
					memory += Long.parseLong(indexs[1]);
				}
			}
		}
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * @return the memory
	 */
	public long getMemory() {
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public void setMemory(long memory) {
		this.memory = memory;
	}

}
