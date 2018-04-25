import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.objectplanet.image.PngEncoder;

public class EncodingThread extends Thread{
	PngEncoder pngEncoder = new PngEncoder();
	@Override
	public void run() {

		super.run();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			pngEncoder.encode(localImage, baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			baos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	BufferedImage localImage;
	public void initEncoding(BufferedImage b) {

		// if(returnImage==null)
		localImage = new BufferedImage(b.getWidth(), b.getHeight(), BufferedImage.TYPE_INT_RGB);//OBS TYPE ÄR EJ TRANSP

		// if(gMerge==null){
		Graphics2D g = localImage.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// }

		// gMerge.drawImage(bottom, 0, 0, null);
		g.drawImage(b, 0, 0, null);

		this.start();

	}
}
