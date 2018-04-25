import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.Random;

public class Pen extends CanvasComponent{

	
	private static final boolean DEBUG = false;
	Msg log = new Msg("pen",DEBUG);
	public Pen(int w, int h) {
		super(w, h);
	}
	long prevX=0,prevY=0;
	boolean freshPrevs=false,freshRelease=false;
	int thickness=1;
	Stroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color color=Color.black;
	private boolean penIsSelected=true;
    
	@Override
	public Graphics2D mouseDown(Graphics2D g, long x, long y) {
		freshPrevs=true;
		Graphics2D gg= paint(g,x,y);
		prevX=x;
		prevY=y;
		log.d("mouse down");
		return super.mouseDown(gg,x,y);
	}
	@Override
	public Graphics2D mouseUp(Graphics2D g, long x, long y) {
		freshRelease=true;
		Graphics2D gg= paint(g,x,y);
		prevX=x;
		prevY=y;
		freshPrevs=false;
		log.d("mouse up");
		
		return super.mouseUp(gg, x, y);
	}
	@Override
	public Graphics2D mouseMove(Graphics2D g, long x, long y) {
		Graphics2D gg= paint(g,x,y);
		prevX=x;
		prevY=y;
		return gg;
	}
	
	private int colorJitter(int c){
		Random r= new Random();
		int n=c+(-30+r.nextInt(30));
		if(n<0||n>=255)
			return c;
		return n;
	}
	
	
	private Color rainbow[]={Color.RED,Color.PINK,Color.GREEN,Color.CYAN,Color.BLUE,Color.YELLOW};
	int rainbowCounter=0;
	private Color getColor(){
		
		
		if(DEBUG){
	
		rainbowCounter=(rainbowCounter+1)%rainbow.length;
		
		return rainbow[rainbowCounter];
		
		}else{
			//Color c=new Color(
				//	colorJitter(color.getRed()),
					//colorJitter(color.getGreen()),colorJitter(color.getBlue()));
			
		return color;
		}
		
	}
	
	public Graphics2D paint(Graphics2D g, long x, long y) {	
//		log.d("mouse move");
		//g.drawString("sample ("+x+","+y+")", x, y);
			
			if(!penIsSelected)
				return g;
			
			if(mouseIsDown){
				g.setColor(getColor());
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
		//	log.e("RELESAEEE");
		//	g.drawLine((int)x, (int)y, (int)prevX, (int)prevY);
			
		}
		return g;
	}

	@Override
	public Graphics2D toolEvent(Graphics2D g, int mode) {
		
	
			penIsSelected=(mode==1);
	
		return g;
	}
	
	@Override
	public void thicknessEvent(int thickness) {
		log.d("THICKNESS "+thickness);
		stroke = new BasicStroke(getScaledLineW(thickness), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		//stroke = new WobbleStroke(10, getScaledLineW(thickness)/3,getScaledLineW(thickness));
	    
	}

	@Override
	public void colorEvent(Color color) {
		// TODO Auto-generated method stub
		this.color=color;
	}
	
}
