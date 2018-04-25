import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


/* General philosopy
 * 
 * 
 * Every canvas function such as draw, erase, etc is implemented with a class that extends
 * Canvas Component. This means that it also implements CanvasEventListener.
 * CanvasEventListener feeds information to the implementing classes and they record the for them relevant information.
 * 
 * The front end can then emit these events, and then feed the resulting
 * graphics object to a painter class that keeps track of generating images.
 * as it parses whatever input data it uses (JSON :) )
 * 
 * 
 * 
 * 
 * **/
public abstract class CanvasComponent implements CanvasEventListener{

	protected int H,W;
	
	public CanvasComponent(int w,int h) {
		W=w;H=h;
	}
	CanvasMode mode=new CanvasMode();
	boolean mouseIsDown;
 @Override
public Graphics2D mouseDown(Graphics2D g, long x, long y) {
	 mouseIsDown=true;
	return g;
}
 @Override
	public Graphics2D mouseUp(Graphics2D g, long x, long y) {
	 mouseIsDown=false;
		return g;
	}
  
 @Override
	public Graphics2D mouseMove(Graphics2D g, long x, long y) {
		return g;
	}
 
 @Override
public Graphics2D toolEvent(Graphics2D g, int mode) {
	return g;
}
@Override
public void colorEvent(Color color) {
	
}


@Override
public Graphics2D clearEvent(Graphics2D g) {
	return g;
	
}
@Override
public void backgroundColorEvent(Color color) {

}

@Override
public void thicknessEvent(int thickness) {
}
//Some scaling stuff that is common for all canvas components
	/*Scales to current screen from 4K (3840x2160)*/
	public int getScaledLineW(int w){
		return (int) (H*(w/2160f));
	}
	
	@Override
	public BufferedImage slideChange(BufferedImage img, int slide) {
		// TODO Auto-generated method stub
		return img;
	}
}	














//Used by JSON parser 
class CanvasMode{
	public static final int PEN_MODE = 0;
	int cursorThickness;
	int erasorThickness;
	int penMode=0;
	Color color;
	
}
/*
 * When adding new event:
 * 1.Add listener to CanvasEventListener as a method
 * 1.1 MAKE SURE IT IS NOT RETURNING NULL!
 * 2.Implement in CanvasComponent, then you can override it were needed!
 * 3.Instantiate new CanvasComponent in token feeder
 * 4.Register it as a listener
 * 
 * */

interface CanvasEventListener{
	Graphics2D mouseDown(Graphics2D g, long x,long y); 
	Graphics2D mouseUp(Graphics2D g,long x, long y);
	Graphics2D mouseMove(Graphics2D g,long x,long y);
	Graphics2D toolEvent(Graphics2D g,int mode);
	Graphics2D clearEvent(Graphics2D g);
	void thicknessEvent(int thickness);
	void colorEvent(Color color);
	void backgroundColorEvent(Color color);
	BufferedImage slideChange(BufferedImage img,int slide);
	
	//Graphics2D frameComplete(Graphics2D g,long mode);
}