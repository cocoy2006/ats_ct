package molab.main.java.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.android.ddmlib.RawImage;
import com.android.monkeyrunner.MonkeyImage;
import com.android.monkeyrunner.adb.AdbMonkeyImage;
import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageUtil {

	public static BufferedImage convertSnapshot(BufferedImage image) {
		// Convert the image to ARGB so ImageIO writes it out nicely
		BufferedImage argb = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = argb.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return argb;
	}

	public static void writeToJPGFile(BufferedImage bImage, String file,
			float quality) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(file));
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bImage);
			param.setQuality(quality, false);
			encoder.setJPEGEncodeParam(param);
			encoder.encode(bImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isSame(BufferedImage pre, BufferedImage cur,
			double percent) {
		int width = cur.getWidth();
		int height = cur.getHeight();
		int numDiffPixels = 0;
		// Now, go through pixel-by-pixel and check that the images are the
		// same;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (cur.getRGB(x, y) != pre.getRGB(x, y)) {
					numDiffPixels++;
				}
			}
		}
		double numberPixels = (height * width);
		double diffPercent = numDiffPixels / numberPixels;
		return percent <= 1.0 - diffPercent;
	}
	
	public static BufferedImage zoom(RawImage rImage) {
		if(rImage != null) {
			MonkeyImage mImage = new AdbMonkeyImage(rImage);
			BufferedImage bImage = mImage.createBufferedImage();
			return zoom(bImage);
		}
		return null;
	}
	
	public static BufferedImage zoom(BufferedImage bImage) {
		float screenQ = 0.9f;
		if(bImage.getWidth() <= 320) {
			
		} else if(bImage.getWidth() <= 480) {
			screenQ = 0.7f;
		} else if(bImage.getWidth() <= 700) {
			screenQ = 0.5f;
		} else if(bImage.getWidth() <= 1300){
			screenQ = 0.3f;
		} else {
			screenQ = 0.1f;
		}
		return zoom(bImage, screenQ);
	}
	
	public static BufferedImage zoom(BufferedImage bImage, float screenQ) {
		int width = (int) (bImage.getWidth() * screenQ);
		int height = (int) (bImage.getHeight() * screenQ);
		return zoom(bImage, width, height);
	}
	
	public static BufferedImage zoom(BufferedImage bImage, int width, int height) {
		if(bImage.getWidth() == width && bImage.getHeight() == height) {
			return bImage;
		}
		BufferedImage nImage = new BufferedImage(width, height, bImage.getType());
		Graphics g = nImage.getGraphics();
		g.drawImage(bImage, 0, 0, width, height, null);
		g.dispose();
		return nImage;
	}

}
