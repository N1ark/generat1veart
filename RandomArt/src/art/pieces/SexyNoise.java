package art.pieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;

public class SexyNoise implements Piece {
	
	enum Mode { SINUSOID, BLURRY_CIRCLE }
	
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Lines...");
		long chr = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();

		graphics.setColor(new Color(0x111111));
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.WHITE);
		graphics.drawString(seed + "", 5, 10);
		
		System.out.println(System.currentTimeMillis()-chr + "ms to generate basic image");
		chr = System.currentTimeMillis();		
			
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		
		int grading = 1000;
		Color[] colors = new Color[grading];
		Color c1 = new Color(0xf01376);
		Color c2 = new Color(0xf5d75f);
		for(int i = 0; i < grading; i++)
			colors[i] = blend(c1, c2, (float)i/grading);

		Mode mode = Mode.SINUSOID;
		
		float smoothness = 190;
		float cycles = 5;
		
		for(int x = 0; x < IMAGE_SIZE; x++) {
			for(int y = 0; y < IMAGE_SIZE; y++) {

				Color c = null;
				
				switch(mode) {
				case SINUSOID: 
				{
					float value = (float) noise.eval(x/smoothness, y/smoothness)/2+0.5f;
					value = (float) Math.cos(value*Math.PI*2*cycles)/2+0.5f;
					value *= grading-1;
					c = colors[(int) value];
					break;
				}
				
				case BLURRY_CIRCLE: 
				{
					float dist = (float) Math.sqrt((x-IMAGE_SIZE/2)*(x-IMAGE_SIZE/2)+(y-IMAGE_SIZE/2)*(y-IMAGE_SIZE/2));
					float maxDist = IMAGE_SIZE/3f;
					
					float value = (float) noise.eval(x/smoothness, y/smoothness)/2+0.5f;
					
					float color = dist/maxDist*grading;
					color *= 1-value*0.5;
						
					float color2 = Math.max(Math.min(color, grading-1), 0);
					c = colors[(int) color2];
					break;
				}
				}
				
				
 
				

				
				graphics.setColor(c);
				graphics.fillRect(x, y, 1, 1);
			}
		}

		return image;
	}
	
	private static Color blend( Color c1, Color c2, float ratio ) {
	    if ( ratio > 1f ) ratio = 1f;
	    else if ( ratio < 0f ) ratio = 0f;
	    float iRatio = 1.0f - ratio;

	    int a = (int)((c1.getAlpha() * iRatio) + (c2.getAlpha() * ratio));
	    int r = (int)((c1.getRed()   * iRatio) + (c2.getRed()   * ratio));
	    int g = (int)((c1.getGreen() * iRatio) + (c2.getGreen() * ratio));
	    int b = (int)((c1.getBlue()  * iRatio) + (c2.getBlue()  * ratio));

	    return new Color(r, g, b, a);
	}

}
