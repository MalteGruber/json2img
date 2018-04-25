import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.peer.KeyboardFocusManagerPeer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DebugViewer extends JFrame implements KeyListener{
	
	
	int w,h;
	public DebugViewer(int w, int h){
		super("Debug viewer");
		this.w=w;
		this.h=h;
		addKeyListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(w+100, h+100));
		add(new Panel());
		pack();
		setLocationRelativeTo(null); //center on screen
		setVisible(true);
		
	}
	
	

	private class Panel extends JPanel {
		
		public Panel(){
			setPreferredSize(new Dimension(w,h));
			
		}


			
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.white);
			g.fillRect(0, 0, 160, 160);
			g.drawImage(img, 0, 0, null);
		}






		
	}

	//From stack overflow
	public static BufferedImage copyImage(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	
	int HIST_SIZE=128;
	int savePointer=0;
	int viewPointer=0;
	BufferedImage history[] = new BufferedImage[HIST_SIZE];
	
	boolean pause=true;
	boolean flick=false;
	BufferedImage img;
	private boolean steppingEnabled=true;
	public void draw(BufferedImage b) {
		
		
		img=b;
		repaint();
		//Save to history
		history[savePointer]=copyImage(b);
		savePointer=(savePointer+1)%HIST_SIZE;
		viewPointer=savePointer;
		decreaseHistory();
	//	System.out.println(viewPointer);
		
		try {
			while(pause){
			Thread.sleep(10);
				if(flick){
					flick=false;
					System.out.println(viewPointer);
					img=history[viewPointer];
					repaint();
					
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(steppingEnabled)
			pause=true;
		
	}
	

	private void decreaseHistory() {
		viewPointer=(viewPointer-1)%HIST_SIZE;
		if(viewPointer<0)
			viewPointer=HIST_SIZE-1;
	}

	private void increaseHistory() {
		viewPointer=(viewPointer+1)%HIST_SIZE;
	}
	@Override
	public void keyPressed(KeyEvent e) {

		// TODO Auto-generated method stub
		if(e.getKeyChar()=='n'){
			pause=false;
		
		}
		
		if(e.getKeyChar()=='j'){
			decreaseHistory();
			flick=true;
			
		}
		if(e.getKeyChar()=='k'){
			increaseHistory();
			flick=true;
		
		}
		if(e.getKeyChar()=='p'){
			steppingEnabled=!steppingEnabled;
			pause=steppingEnabled;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
