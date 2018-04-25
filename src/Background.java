import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Background extends CanvasComponent {
	private BufferedImage backgroundSlide = null;
	private BufferedImage backdrop = null;
	private boolean DRAW_CHESS_BOARD = false;

	private int currentSlide = 0;
	private Msg log = new Msg("Background");

	public Background(int w, int h) {
		super(w, h);
		// log.ENABLE_DEBUG=true;
		// tryLoadingBackdrop();
	}

	private void tryLoadingBackdrop() {

		// Load that image..
		try {
			backdrop = ImageIO.read(new File("backdrop.jpg"));
			backdropHasChanged = true;
			log.d("Found backdrop image!");
		} catch (IOException e) {
			backdrop = null;
		}

	}

	private double prev = 0;

	@Override
	public BufferedImage slideChange(BufferedImage img2, int slide) {

		currentSlide = slide;

		// Load that image..
		// Draw background
		try {
			backgroundSlide = ImageIO.read(new File(getPdfName()));
			backdropHasChanged = true;
		} catch (IOException e) {
			backgroundSlide = null;
		}
		return img2;
	}

	// Figures out the pdf name, note the name might change from 1, 01, 001.. !
	private String getPdfName() {
		return TokenFeeder.currentPath + "pdf/p-" + getPaddedNumber(1 + currentSlide) + ".jpg";
	}

	private boolean hasTestedZeros = false;
	private int numLen = 1;
	private Color backgroundColor = Color.BLACK;
	private boolean backdropHasChanged;

	String getZeroString(int zeros) {
		String z = "";
		for (int i = 0; i < zeros; i++) {
			z += "0";
		}
		return z;
	}

	private String getPaddedNumber(int i) {
		// This is lazy, but just test different number (1, 01, 001...) to see
		// what format
		// is used.
		if (!hasTestedZeros) {
			hasTestedZeros = true;

			for (int j = 1; j < 6; j++) {
				numLen = j;
				try {

					ImageIO.read(new File(TokenFeeder.currentPath + "pdf/p-" + getZeroString(j - 1) + "1.jpg"));
					break;
				} catch (IOException e) {

				}
			}
		}

		// Pad number with zeros to the present format
		log.d("zeros: " + numLen);
		String num = i + "";
		num = getZeroString(numLen - num.length()) + num;
		log.d(num);
		return num;
	}

	public Graphics2D drawBackground(Graphics2D gBack) {

		if (backdropHasChanged) {
			backdropHasChanged = false;

			Composite tmp = gBack.getComposite();
			Composite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f);
			gBack.setComposite(composite);

			// gBack.fillRect(0, 0, 2000, 2000);

			gBack.setComposite(tmp);

			gBack.setColor(backgroundColor);
			gBack.fillRect(0, 0, W, H);

			if (backdrop != null) {
				gBack.drawImage(backdrop, 0, 0, W, H, null);
			}

			if (backgroundSlide != null) {

				int bh = H;

				int bw = (int) (bh * ((float) backgroundSlide.getWidth() / (float) backgroundSlide.getHeight()));

				gBack.drawImage(backgroundSlide, (W - bw) / 2, 0, bw, bh, null);

			}

		}

		if (DRAW_CHESS_BOARD) {

			if (prev < Math.PI * 2) {
				prev += .1;

			} else {
				prev = 0;
			}

			int D = (int) (((Math.sin((prev)) + 1.5f) * 10.0));
			int n = W / D + 4;
			Color c0 = Color.gray;
			Color c1 = Color.DARK_GRAY;
			Composite tmp = gBack.getComposite();
			Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			gBack.setComposite(composite);
			// Row
			for (int i = 0; i < n; i++) {

				// Columns
				for (int j = 0; j < n; j++) {
					gBack.setColor(c1);
					if (j % 2 == 0) {
						gBack.setColor(c0);
						if (i % 2 == 0) {
							gBack.setColor(c1);
						}

					} else if ((i) % 2 == 0) {
						gBack.setColor(c0);
					}

					gBack.fillRect(j * (D), i * (D), D, D);
				}
			}

		}

		return gBack;

	}

	@Override
	public void backgroundColorEvent(Color color) {
		this.backgroundColor = color;
		backdropHasChanged = true;

	}

}
