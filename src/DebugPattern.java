import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import com.objectplanet.image.PngEncoder;

public class DebugPattern {
	private BufferedImage mainLayer;
	int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
	int w = 1920, h = 1080;
	Graphics2D gMain;
	private ByteArrayOutputStream baos;
	PngEncoder pngEncoder = new PngEncoder();
	DebugViewer viwer;
	
	
	
	boolean ENABLE_GUI=false;
	
	public DebugPattern() throws InterruptedException {
		
		if(ENABLE_GUI)
			viwer=new DebugViewer(w, h);
		mainLayer = new BufferedImage(w, h, IMAGE_TYPE);
		gMain = mainLayer.createGraphics();
		gMain.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		boolean go=true;
		while(go){
			draw(gMain);
			

			if(ENABLE_GUI)
				viwer.draw(mainLayer);
			/*
			if(counter==30*4){
				System.err.println("SLEEPING");
				Thread.sleep(10*60*1000);
			}
			
			if(counter==30*8){

				System.err.println("SLEEPING AGAIN");
				Thread.sleep(2*60*1000);
			}		
			
			if(counter == 30*12)
				System.exit(0);
			*/
			/*Pipe out*/
			
			try {
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				
					baos= new ByteArrayOutputStream();
			
				
						pngEncoder.setCompression(PngEncoder.BEST_SPEED);
						//	pngEncoder.setColorType(PngEncoder.COLOR_TRUECOLOR);
						pngEncoder.encode(mainLayer, baos);
					baos.flush();
					byte[] bytes = baos.toByteArray();
					baos.close();
			
					for (int i = 0; i < 30*60; i++) {
						System.out.write(bytes);	
						System.err.println("FASTMODE FRAME #"+i);
					}
					System.exit(0);
					
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}
/*
 * 
 * 
 *
 * 
 * 
 * 
 * 
 * 
java -jar debug_img.jar | ffmpeg  -framerate 30 -i - -c:v libx264 -profile:v high -crf 20 -pix_fmt yuv420p -y -stats movieDebug.mp4 2> ffmpeg.txt


 ffmpeg  -framerate 30 -i - -c:v libx264 -profile:v high -crf 20 -pix_fmt yuv420p -y -stats movieDebug.mp4 2> ffmpeg.txt
-i - = input pipe?
-c encoder 

-crf 18 = The range of the quantizer scale is 0-51: where 0 is lossless, 23 is default, and 51 is worst possible. A lower value is a higher quality and a subjectively sane range is 18-28



//Tests
 * 
 * 

default 37 sec ish

30 Sec lossless
java -jar debug_img.jar | ffmpeg  -framerate 30 -i - -c:v libx264 -preset ultrafast -crf 0  -y -stats movieDebug.mp4


///47 sec
java -jar debug_img.jar | ffmpeg  -framerate 30 -i - -c:v libx264 -preset slow -crf 22  -y -stats movieDebug.mp4


 * */
	int counter=0;
	int flip=50,inc=1;
	Font font = new Font("Arial", 0, 60);
	
	private void draw(Graphics2D gMain2) {
		gMain2.setColor(Color.DARK_GRAY);
	//	flip+=inc;
		if(flip>10)
			inc=-1;
		if(flip<=1)
			inc=1;
		
		gMain2.fillRect(0, 0, w, h);
		int d=1000,dx= (w)/d;
		
		Random r= new Random();
		Color c ;
		
		for (int i = 0; i < d; i++) {
			if(i%flip==0){
				c=new Color(r.nextInt(255),r.nextInt(255),r.nextInt(255));
			gMain2.setColor(c);	
			gMain2.fillRect(dx*i*2, 0,dx, h);
			gMain2.fillRect(0,dx*i*2,w, dx);
			
			}
		
		}
	//	gMain2.setColor(Color.ORANGE);
	
		gMain2.setFont(font);
		gMain2.drawString("The Lazy Fox jumped over the:  "+counter++, 200, 200);
		
		
		
		
	}
	public static void main(String[] args) {
		try {
			new DebugPattern();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	}
