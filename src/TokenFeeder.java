import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.objectplanet.image.PngEncoder;

/**
 * 
 * 
 * 
 * This class parses the json input and calls 
 * every registered CanvasEvent interface with the relevant 
 * methods for the parsed events.
 * 
 * For every tick (ie frame of video) the events are read.
 * A graphics2D object is keept between ticks and is painted with 
 * the changes of each frame. A image of the graphics object is saved
 * between frames.
 * 
 * */

/**
 * JSON Specication
 * 
 * 
 * 
 * 
 * Mouse movement sample: {"x":0,"y":"0","t":14204212}; Coordinates relative to
 * 4K screen
 * 
 * 
 * ------------ Mouse events down 1 up 2 {"mouse":2,"x":23,"y":42,"t":14204212}
 * 
 * 
 * ------------ Screen switch (As in changing slide) {"screen":5,"t":14204212}
 * <- The number of the screen that was switched to
 * 
 * 
 * ------------ pen 1 eraser 2 {"tool":0,"t":14204212}
 * 
 * 
 * 
 * ------------ Color #000 or #aabbcc {"color":"#930","t":14204212}
 * 
 * 
 * 
 * ------------ Thickness (relative to 4k) {"thk":30,"t":14204212}
 * 
 * 
 * ------------
 * 
 * tool 1: Pen 2: Eraser
 * 
 * 
 * {"tool:1,"t"=12345}
 * 
 * 
 */

// FOR PICS
// ffmpeg -framerate 100 -i bild_%05d.png -c:v libx264 -profile:v high -crf 20
// -pix_fmt yuv420p a_output.mp4
// Win: ffmpeg -framerate 30 -i img/bild_%05d.png -c:v libx264 -profile:v high
// -crf 20 -pix_fmt yuv420p output.mp4

// PIPE
// nice java -jar json_img.jar | ffmpeg -framerate 60 -i - -c:v libx264
// -profile:v high -crf 20 -pix_fmt yuv420p a_output.mp4

// Pipe and path
// nice java -jar json_img.jar
// /home/mg/workspace/TutorialiteSpider/testJobs/GQ7UJ/ | nice ffmpeg -framerate
// 60 -i - -c:v libx264 -profile:v high -crf 20 -pix_fmt yuv420p a_output.mp4

public class TokenFeeder implements WdtCallbackListener {

	
	
	/*Only used during debugging*/
	public static String currentPath="./demo_project"+"/"; // Must end with "/"
	
	/* Debugging feature, clears the main frame on each new frame */
	private static  boolean TEMP_FRAME = false;
	private static  boolean DEBUG_SAVE_AS_IMAGE = false;
	private static  boolean DEBUG_GUI = true;
	private static  boolean ENABLE_PIPE = false;
	private static  boolean ENABLE_DEBUG_OWERLAY = true;
	
	
	
	
	
	private WatchDogTimer watchDogTimer = new WatchDogTimer(this);
	private byte[] bytes;
	
	/*Do not change these!*/
	static void setProductionSettings(){
		TEMP_FRAME = false;
		DEBUG_SAVE_AS_IMAGE = false;
		DEBUG_GUI = false;				
		ENABLE_PIPE = true;
		ENABLE_DEBUG_OWERLAY = false;
		Benchmark.beSilent();
	}
	
	
	private Msg log = new Msg("token feeder", false);
	private Graphics2D gMain = null;
	private String json;
	private DebugViewer debugViewer;
	private BufferedImage mainLayer;

	
	private static String pad(int i) {
		String out = "" + i;
		while (out.length() < 5) {
			out = "0" + out;
		}
		return out;
	}

	private ArrayList<CanvasEventListener> listeners = new ArrayList<>();
	private RenderedImage img;
	private ByteArrayOutputStream baos;
	private BufferedImage cursorLayer;
	private Graphics2D gCursor;
	private Graphics2D gBack;
	private BufferedImage backgroundLayer;
	private JsonFeeder jsonReader;
	
	private Cursor cursor;
	private Background background;

	public void addCursor(Cursor cursor) {
		this.cursor = cursor;
		addTokenListener(cursor);
	}

	public void addBackground(Background b) {
		background = b;
		addTokenListener(b);
	}

	public void addTokenListener(CanvasEventListener listener) {
		listeners.add(listener);
	}

	private int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
	private int w, h;
	 boolean gifModeEnabled;
	public TokenFeeder(int w, int h, boolean skipPauses) {
		this.w=w;
		this.h=h;
		this.gifModeEnabled=skipPauses;
		FileHandler.writeToPublicLog("Initializing");
		
		

		if (DEBUG_GUI) {
			debugViewer = new DebugViewer(w, h);
		}

		mainLayer = new BufferedImage(w, h, IMAGE_TYPE);
		gMain = mainLayer.createGraphics();
		gMain.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		cursorLayer = new BufferedImage(w, h, IMAGE_TYPE);
		gCursor = cursorLayer.createGraphics();
		gCursor.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		backgroundLayer = new BufferedImage(w, h, IMAGE_TYPE);
		gBack = backgroundLayer.createGraphics();
		gBack.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	}



	/*
	 * BufferedImage tmp=copyImage(bufferedImage); Graphics2D g2Tmp =
	 * tmp.createGraphics();
	 * g2Tmp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	 * RenderingHints.VALUE_ANTIALIAS_ON); drawCursor(g2Tmp,data,time);
	 * 
	 * ImageIO.write(tmp, "png", new File(path));
	 */

	private BufferedImage returnImage;
	private Graphics2D gMerge;

	public BufferedImage mergeLayers(BufferedImage bottom, BufferedImage main, BufferedImage top) {

		if(returnImage==null)
			returnImage = new BufferedImage(main.getWidth(), main.getHeight(), IMAGE_TYPE);

		// if(gMerge==null){
		gMerge = returnImage.createGraphics();

		gMerge.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// }

		 gMerge.drawImage(bottom, 0, 0, null);
		gMerge.drawImage(main, 0, 0, null);

		if (TEMP_FRAME) {
			Composite tmp = gMain.getComposite();
			Composite composite = AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f);
			gMain.setComposite(composite);

			gMain.fillRect(0, 0, 2000, 2000);
			gMain.setComposite(tmp);
		}

		 gMerge.drawImage(top, 0, 0, null);
		 gMerge.dispose();

		return returnImage;
	}


	private int oldPercent;
	private int pipeCounter=0;
	private int parsedCounter;

	private void renderVideo() {
		
		jsonReader= new JsonFeeder(currentPath);
		EncodingThread encodingThread = new EncodingThread();
		JSONObject event = null;

		// These are null per default, if a token exists they will get the token
		// value.
		// Assume that we are the producer of the json, we do not care about
		// type etc.
		Object x = null;
		Object y = null;
		Object time = null;
		Object mode = null;
		Object mouseEvent = null;
		Object thickness = null;
		Object color = null;
		Object backgroundColor = null;
		Object screen = null;
		Object tool = null;
		Object clear = null;
		Object stop = null;
		PngEncoder pngEncoder = new PngEncoder();
		long frameEndTime = 0;
		long dt = 1000;
		int parsedCount = 0;

		int frame = 0;
		BufferedImage b = null;

		double elapsedTime = 0, frameTime = (1 / 24f) * 1000;
		if(gifModeEnabled){
			frameTime = (1 / 5f) * 1000;
		}
		boolean render = true;
		long currentFrameEnd = 0;
		long timeOfFirstSample = 0;
		long lastParsedTimestamp = 0;
		int lastIndexParsed = 0;
		int frameNumber = 0;
		boolean startTokenParsed = false;

		/*
		 * Loop all timestamps { renderFrame, saveFrame }
		 */
		boolean anyChangeInFrame = false;
		String jsonForThisFrame = "";
		boolean revertFrame=false;
		boolean renderIsGo=true;
		while (renderIsGo) {
			Benchmark.start(3, "StartStop");

			// To avoid loss of persision do not use the full timestamp as "zero
			// time" (it is a large number=bad precision)
			// Instead use a double time stamp to count from zero, then when
			// evaluating all frames for that timestamp,
			// Convert it to a long utc by adding the start time from the first
			// frame!..
			elapsedTime += frameTime;
			currentFrameEnd = timeOfFirstSample + (long) elapsedTime;

			anyChangeInFrame = false;

			jsonForThisFrame = "";
			long startTime0 = System.nanoTime();
			Benchmark.start(0, "Drawing");
			// Draw all changes found for this frame
			while (lastParsedTimestamp < currentFrameEnd) {
				// -----------------COULD BE FUNCTION, CALLED RENDER
				// FRAME----------------------
				// Chose this approach as to not overwhelm the GC, or litter the
				// class with fields.. sorry :(
				if(!revertFrame)
					event=jsonReader.pop();
				else
					revertFrame=false;
				
				//event = (JSONObject) array.get(lastIndexParsed++);
				
				stop=event.get("stop");
				if(stop!=null){
					renderIsGo=false;
					break;
				}
				
				
				x = event.get("x");
				y = event.get("y");
				mode = event.get("mode");
				mouseEvent = event.get("mouse");
				thickness = event.get("thk");
				color = event.get("color");
				screen = event.get("screen");
				tool = event.get("tool");
				time = event.get("t");
				backgroundColor = event.get("background");
				clear = event.get("clear");
				lastParsedTimestamp = (long) time;
				anyChangeInFrame = true;

				

				// First do some validation on the timestamp..
				if (time == null) {
					log.e("No timestamp found for " + event.toJSONString());
					continue;
				}
				long timestamp = (long) time;

				// Bugfix: Make sure that we do not execute the timestamps
				// beyond the frame..
				if (timestamp > currentFrameEnd) {
					// Force next timestamp
					lastIndexParsed--;
					revertFrame=true;
					continue;
				}

				
				// Save these events for debug printing
				jsonForThisFrame += event.toJSONString() + "\n";

				// Log message for the user
				int percent = (int) (100*(double) parsedCounter++ / (double) jsonReader.size());
				if (percent % 1 == 0 && percent != oldPercent) {
					FileHandler.writeToPublicLog("Progress " + percent + "%");
					oldPercent = percent;
				}

				log.d("-----PARSING " + event.toJSONString() + "------");

				// Check that timestamp is valid
				if (timestamp < frameEndTime - dt) {
					log.e("NON CAUSAL TIMESTAMP");
				}
				final long startTime = System.currentTimeMillis();

				// Scale coordinates to 4k
				if ((x != null) && (y != null)) {
					watchDogTimer.gentlyKickWatchDog();
					double fromX = 3840, fromY = 2160;
					x = (long) (((long) x) * ((double) w / fromX));
					y = (long) (((long) y) * ((double) h / fromY));
				}

				// Now generate events for the tokens that did not get parsed to
				// null

				// If only x and y
				if ((mouseEvent == null) && (x != null) && (y != null)) {
					parsedCount++;
					// log.d((long)x+","+(long)y);
					for (CanvasEventListener c : listeners)
						gMain = c.mouseMove(gMain, (long) x, (long) y);

				} else if (mode != null) {
					parsedCount++;
					// log.d((long)mode);

				} else if ((mouseEvent != null) && (x != null) && (y != null)) {
					parsedCount++;
					for (CanvasEventListener c : listeners) {
						// Mouse down
						if ((long) mouseEvent == 1)
							gMain = c.mouseDown(gMain, (long) x, (long) y);
						// Mouse up
						if ((long) mouseEvent == 2)
							gMain = c.mouseUp(gMain, (long) x, (long) y);

					}
				} else if (thickness != null) {
					for (CanvasEventListener c : listeners)
						c.thicknessEvent((int) (long) thickness);
					;
					parsedCount++;
				} else if (color != null) {
					String colorHex = (String) color;

					Color colorJava = parseColorString(colorHex);
					for (CanvasEventListener c : listeners)
						c.colorEvent(colorJava);
					;

					parsedCount++;
				} else if (backgroundColor != null) {
					String colorHex = (String) backgroundColor;

					Color colorJava = parseColorString(colorHex);
					
					  for(CanvasEventListener c:listeners)
						  c.backgroundColorEvent(colorJava);
					 
					//gMain.setColor(colorJava);
					// FIX
					//gMain.fillRect(0, 0, w + 10, h + 10);

					parsedCount++;
				} else if (screen != null) {
					parsedCount++;
					for (CanvasEventListener c : listeners)
						mainLayer = c.slideChange(mainLayer, (int) (long) (screen));
					gMain = mainLayer.createGraphics();
					gMain.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				} else if (tool != null) {
					// log.d("Tool parsed "+tool);
					// log.d("BEFORE:"+gMain+"xx"+listeners.size());
					for (CanvasEventListener c : listeners)
						gMain = c.toolEvent(gMain, (int) (long) tool);
					// log.d("AFTER:"+gMain);
					parsedCount++;
				} else if (clear != null) {
					// log.d("Tool parsed "+tool);
					// log.d("BEFORE:"+gMain+"xx"+listeners.size());
					 for(CanvasEventListener c:listeners)
						 gMain=c.clearEvent(gMain);
					 
					// log.d("AFTER:"+gMain);
					// TODO!
					parsedCount++;
				} else if (event.get("start") != null) {
					startTokenParsed = true;

				}

				// _---------------------END OF RENDER FRAME-------------------
			}
			Benchmark.stop(0);

			// Save frame as image!

			// ---------------SAVE IMAGE-----------------
			if (startTokenParsed) {

				if (true) {// chhange
					Benchmark.start(1, "Merge");
					gCursor = cursor.drawCursor(gCursor);
					 gBack=background.drawBackground(gBack);
					// MERGE HERE!!!
					// gBack.fillRect(0, 0, w, h);

					//b = mainLayer;
					 if(anyChangeInFrame){
					 b = mergeLayers(backgroundLayer, mainLayer, cursorLayer);
					}
					 Benchmark.stop(1);

					// Draw overlay
					if (ENABLE_DEBUG_OWERLAY) {
						Graphics2D g = (Graphics2D) b.getGraphics();

						int offet = 100;
						int stride = 20;

						g.setColor(Color.GREEN);
						g.drawString("Time=" + elapsedTime + "START_TOKEN_PARSED=" + startTokenParsed, 100,
								offet += stride);

						g.setColor(Color.RED);
						g.drawString("Frame=" + frame, 100, offet += stride);

						g.setColor(Color.YELLOW);
						g.drawString("FrameTime=" + frameTime, 100, offet += stride);

						g.setColor(Color.CYAN);
						g.drawString("anyChange=" + anyChangeInFrame, 100, offet += stride);

						g.setColor(Color.BLUE);
						String json[] = jsonForThisFrame.split("\n");
						// offet=;
						for (String j : json) {
							g.setColor(Color.BLACK);
							g.drawString(j, 100, offet += stride);
						}

					}

					///////////////////////////////////

					// Save frame as image!

					if (DEBUG_SAVE_AS_IMAGE) {
						try {
							ImageIO.write(b, "png", new File(currentPath + "img/out" + pad(frame) + ".png"));

						} catch (IOException e) {
							log.e("Cant save image!");
						}
					}
					if (DEBUG_GUI) {
						debugViewer.draw(b);
					}

					if (ENABLE_PIPE) {
						Benchmark.start(2, "Export");

						try {
							///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
							
								
						
								Benchmark.start(4, "ENCODER OBJECT");
								if(anyChangeInFrame){
									baos= new ByteArrayOutputStream();
									pngEncoder.setCompression(PngEncoder.BEST_SPEED);
									//	pngEncoder.setColorType(PngEncoder.COLOR_TRUECOLOR);
									pngEncoder.encode(b, baos);
									Benchmark.stop(4);
									baos.flush();

									bytes = baos.toByteArray();
									baos.close();
									
									
								}
								//System.err.println("PIPE #"+(pipeCounter++)+" of "+jsonReader.size()+" ("+(int)(((double)pipeCounter/(double)jsonReader.size())*100));
								System.out.write(bytes);
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					Benchmark.stop(2);
					Benchmark.stop(3);
					Benchmark.report();
					frame++;
				//	gMain = cursor.restoreMain(gMain);

				}
			}

		}
		System.err.println("Render finished!");
		FileHandler.writeToPublicLog("Almost done...");
		System.exit(0);
	
	}

	private Color parseColorString(String colorHex) {
		String red = null, green = null, blue = null;
		// remove the #
		colorHex = colorHex.substring(1);

		if (colorHex.length() == 3) {
			red = colorHex.substring(0, 1) + colorHex.substring(0, 1);
			green = colorHex.substring(1, 2) + colorHex.substring(1, 2);
			blue = colorHex.substring(2, 3) + colorHex.substring(2, 3);
		} else {
			red = colorHex.substring(0, 2);
			green = colorHex.substring(2, 4);
			blue = colorHex.substring(4, 6);
		}
		return new Color(Integer.parseInt(red, 16), Integer.parseInt(green, 16), Integer.parseInt(blue, 16));
	}

	public static void main(String[] args) throws IOException {
		
		int w=1920,h=1080;
		boolean enableCursor=true;
		
		boolean ignorePauses=false;
		if (args.length >0) {
			setProductionSettings();
			TokenFeeder.currentPath = args[0];// EWS!!
			System.err.println("Path:" + TokenFeeder.currentPath);
			if(args.length==3){
				/*Resolution in arguments*/
				w=Integer.parseInt(args[1]);
				h=Integer.parseInt(args[2]);
				enableCursor=false;
				ignorePauses=true;
			}
		} else {
			System.err.println("!!!WARNING:::DEBUG.MODE.ENABLED::::WARNING!!!");
		}

		TokenFeeder t = new TokenFeeder(w,h,ignorePauses);
		Pen pen = new Pen(w, h);
		Cursor cursor = new Cursor(w, h,enableCursor);
		Background background = new Background(w, h);
		Eraser eraser = new Eraser(w, h);
		SlideContext slideContext = new SlideContext(w, h);
		ClearCanvas clearCanvas=new ClearCanvas(w, h);
		t.addTokenListener(pen);
		t.addTokenListener(eraser);
		t.addTokenListener(slideContext);
		t.addTokenListener(clearCanvas);
		t.addCursor(cursor);
		t.addBackground(background);
		
		t.renderVideo();

	}

	private int getW() {
		return w;
	}

	private int getH() {
		return h;
	}

	@Override
	public void timeout() {
		FileHandler.writeToPublicLog("Canceled due to inactivity.");
		System.err.println("Exititng due to timeout...");
		System.exit(0);
	}
}
