import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Random;

public class Eraser extends CanvasComponent{

	
	private static final boolean DEBUG = false;
	Msg log = new Msg("eraser",false);
	public Eraser(int w, int h) {
		super(w, h);
	}
	long prevX=0,prevY=0;
	boolean freshPrevs=false,freshRelease=false;
	int thickness=1;
	Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color color=Color.black;
	private boolean eraserIsSelected=false;
    
	@Override
	public Graphics2D mouseDown(Graphics2D g, long x, long y) {
	//	log.d("mouse down "+g );
		
		super.mouseDown(g, x, y);
		freshPrevs=true;
		Graphics2D gg= paint(g,x,y);
		prevX=x;
		prevY=y;
		
		return gg;
	}
	@Override
	public Graphics2D mouseUp(Graphics2D g, long x, long y) {
		super.mouseUp(g, x, y);
		freshRelease=true;
		Graphics2D gg= paint(g,x,y);
		prevX=x;
		prevY=y;
		freshPrevs=false;
		log.d("mouse up");
		
		return gg;
	}
	@Override
	public Graphics2D mouseMove(Graphics2D g, long x, long y) {
		super.mouseMove(g, x, y);
		Graphics2D gg= paint(g,x,y);
		prevX=x;
		prevY=y;
		
		return gg;
	}
	private Color getColor(){
		if(DEBUG){
		Random r= new Random();
		return new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
		}else{
		return color;
		}
	}
	
	public Graphics2D paint(Graphics2D g, long x, long y) {	
//		log.d("mouse move");
		//g.drawString("sample ("+x+","+y+")", x, y);
			
			if(!eraserIsSelected)
				return g;
			
			Composite tmp=g.getComposite();
			Composite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f);
			if(!DEBUG){
				g.setComposite(composite);	    
				
			}
			if(mouseIsDown){
			

				
				g.setColor(Color.ORANGE);
				g.setStroke(stroke);
				//log.d("mouse down at "+x+","+y+" prevs "+prevX+","+prevY);
				//There is a point do draw a line to!
				g.drawLine((int)x, (int)y, (int)prevX, (int)prevY);
				//There is no previous line
				if(freshPrevs){
					//Draw the line!
					log.d("FRESH CLICK!");
					g.drawLine((int)x, (int)y, (int)x, (int)y);
					freshPrevs=false;		
				}
				
			
		}else if(freshRelease){
			freshRelease=false;
			
		
			g.drawLine((int)x, (int)y, (int)prevX, (int)prevY);
			
		}
			
			if(!DEBUG){
				g.setComposite(tmp);
			}
			
		return g;
	}

	@Override
	public Graphics2D toolEvent(Graphics2D g, int mode) {
		super.toolEvent(g, mode);
		freshRelease=false;
			eraserIsSelected=(mode==2);
			
		return g;
	}
	
	@Override
	public void thicknessEvent(int thickness) {
	//	log.d("THICKNESS "+thickness);
		stroke = new BasicStroke(getScaledLineW(thickness), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	    
	}

	@Override
	public void colorEvent(Color color) {
		// TODO Auto-generated method stub
		this.color=color;
	}
	
}
