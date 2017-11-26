package molab.main.java.util.logcat;

import com.android.ddmlib.MultiLineReceiver;

public class LogcatReceiver extends MultiLineReceiver {

	public boolean isCancelled = false;
	public StringBuffer molab = new StringBuffer();

	public LogcatReceiver() {
		super();
		setTrimLine(false);
	}

	@Override
	public void processNewLines(String[] lines) {
		if (isCancelled == false) {
			for (String line : lines) {
				if(line.indexOf("E/") > 0 || line.indexOf("F/") > 0) {
					line = "<p style='color: red;'>" + line + "</p>";
				} else if(line.indexOf("W/") > 0) {
					line = "<p style='color: blue;'>" + line + "</p>";
				} else {
					line = "<p>" + line + "</p>";
				}
				molab.append(line);
			}
		}
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * @return the molab
	 */
	public StringBuffer getMolab() {
		return molab;
	}

	/**
	 * @param molab
	 *            the molab to set
	 */
	public void setMolab(StringBuffer molab) {
		this.molab = molab;
	}

}
