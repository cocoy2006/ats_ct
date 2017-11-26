package molab.main.java.util.shell;

import com.android.ddmlib.MultiLineReceiver;

public class CatReceiver extends MultiLineReceiver {
	
	public boolean isCancelled = false;
	public StringBuffer sb;
	
	public CatReceiver() {
		super();
		setTrimLine(false);
	}

	@Override
	public void processNewLines(String[] lines) {
		if (isCancelled == false) {
			sb = new StringBuffer();
			for (String line : lines) {
				sb.append(line);
			}
			isCancelled = true;
		}
	}

	public boolean isCancelled() {
		return isCancelled;
	}
	
	/**
	 * @return the sb
	 */
	public StringBuffer getSb() {
		return sb;
	}

	/**
	 * @param sb the sb to set
	 */
	public void setSb(StringBuffer sb) {
		this.sb = sb;
	}

}
