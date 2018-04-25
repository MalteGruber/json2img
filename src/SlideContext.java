
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

public class SlideContext extends CanvasComponent {
	Msg log = new Msg("Slide saver",false);
	public SlideContext(int w, int h) {
		super(w, h);
		// TODO Auto-generated constructor stub
	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}

	
	ArrayList<BufferedImage> savedMainLayers = new ArrayList<>();
	int currentSlide=0;
	@Override
	public BufferedImage slideChange(BufferedImage img, int slide) {
		// TODO Auto-generated method stub

		// Make sure that there exists a graphics context for the ?new? slide
		// (else this just passes without change)
		for (int i = 0; i < 5; i++) {
			try {
				savedMainLayers.get(slide);
				break;
			} catch (Exception e) {
				// Aright, lets add that slide
				BufferedImage returnImage = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
			
				savedMainLayers.add(returnImage);
				
				log.d("New graphics created for slide "+slide+"!");

			}
			if (i > 3) {
				System.err
						.println("Jump in slide numbers, make sure to have tokens for slides be 1,2,3,4,5 not 1,2,9!!");
			}
		}
		//Alright, now save the current g and return the new one! 
		
		savedMainLayers.set(currentSlide, deepCopy(img));

		log.d("current "+currentSlide+" new slide "+slide);
		
		currentSlide=slide;
		
		return deepCopy(savedMainLayers.get(currentSlide)); //Load the one that we have changed to.
		
		
		
		
	}

}
