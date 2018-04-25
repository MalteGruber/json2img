import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Cursor extends CanvasComponent {

	private static final boolean FORCE_DEBUG_CURSOR = false;
	private Msg log = new Msg("cursor");
	private BufferedImage cursorImage = null;
	private boolean cursorEnabled=true;
	public Cursor(int w, int h,boolean enabled) {
		super(w, h);
		this.cursorEnabled=enabled;
		try {

			cursorImage = ImageIO.read(new File("cursorCross.png"));

		} catch (IOException e) {

		}
	}

	private long lastX, lastY;

	@Override
	public Graphics2D mouseMove(Graphics2D g, long x, long y) {
		lastX = x;
		lastY = y;
		return super.mouseMove(g, x, y);
	}

	@Override
	public Graphics2D mouseDown(Graphics2D g, long x, long y) {
		lastX = x;
		lastY = y;
		return super.mouseDown(g, x, y);
	}

	public Graphics2D drawCursor(Graphics2D g) {

		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, 2000, 2000);
		g.setComposite(AlphaComposite.SrcOver);
		// Is cursor inside image?
		if ((lastX < 0) || (lastY < 0) || (lastX > W) || (lastY > H) || !cursorEnabled) {
			// Meh, cursor not visible/disabled..
		} else {

			Stroke stroke = new BasicStroke(getScaledLineW(6), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
			int c = getScaledLineW(10);

			if (cursorImage != null) {

				g.setStroke(stroke);

				g.drawImage(cursorImage, (int) lastX - cursorImage.getWidth() / 2,
						(int) lastY - cursorImage.getHeight() / 2, null);
				if (FORCE_DEBUG_CURSOR) {
					g.setColor(Color.BLACK);
					log.d("CURSOR" + lastX + " " + lastY);
					g.setStroke(stroke);

					g.drawLine((int) lastX - c, (int) lastY - c, (int) lastX + c, (int) lastY + c);
					g.drawLine((int) lastX + c, (int) lastY - c, (int) lastX - c, (int) lastY + c);
				}
			} else {
				g.setColor(Color.BLACK);
				log.d("CURSOR" + lastX + " " + lastY);
				g.setStroke(stroke);

				g.drawLine((int) lastX - c, (int) lastY - c, (int) lastX + c, (int) lastY + c);
				g.drawLine((int) lastX + c, (int) lastY - c, (int) lastX - c, (int) lastY + c);
			}

		}
		return g;
	}

	@Override
	public Graphics2D toolEvent(Graphics2D g, int mode) {
		// TODO Auto-generated method stub
		return g;
	}

	@Override
	public void thicknessEvent(int thickness) {
		// TODO Auto-generated method stub

	}

	@Override
	public void colorEvent(Color color) {
		// TODO Auto-generated method stub

	}

}