import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

public class ClearCanvas extends CanvasComponent{

	public ClearCanvas(int w, int h) {
		super(w, h);
	}
	
	@Override
	public Graphics2D clearEvent(Graphics2D g) {
		/*Clear the canvas main layer*/		
		Composite tmp = g.getComposite();
		Composite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f);
		g.setComposite(composite);
		g.fillRect(0, 0, W+10, H+10);
		g.setComposite(tmp);
		return super.clearEvent(g);
	}

}
