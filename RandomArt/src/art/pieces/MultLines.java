package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;

public class MultLines implements Piece{

	/**
	 * Will generate an image, containg 
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - 
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Lines...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.drawString(seed + "", 5, 10);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.01), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		int lineAmount = 50;
		int lineWidth = squareSize*2/lineAmount;
		int lineSize = squareSize/2;
		
		int startX = (IMAGE_SIZE-squareSize)/2;
		
		Color[] colors = {
				new Color(0xe3aac5),
				new Color(0xe1aae3),
				new Color(0xc8aae3),
				new Color(0xaab0e3),
				new Color(0xaac9e3),
				new Color(0xaae3c5),
				new Color(0xbae3aa),
				new Color(0xd6e3aa),
				new Color(0xe3d6aa)
		};
		
		for(int i = 0; i < lineAmount; i++) {
			int width = (int) (lineSize * (rand.nextFloat()*0.1f+0.9f));
			int x = (int) ((squareSize-width)*rand.nextFloat());
			int y = (int) ((squareSize-lineWidth)*rand.nextFloat());
			
			fillRect(image, colors[(y*colors.length)/(squareSize-lineWidth)], startX+x, startX+y, width, lineWidth);
		}
		
		
		return image;
	}
	
	private static void fillRect(BufferedImage img, Color c, int x, int y, int width, int height) {
		for(int xp = x; xp < x+width; xp++) {
			for(int yp = y; yp < y+height; yp++) {
				img.setRGB(xp, yp, multColors(img.getRGB(xp, yp), c));
			}
		}
	}
	
	private static int multColors(int c1, Color c2) {
		float[] rgb1 = {
				((c1 & 0xFF0000) >> 16)/255f,
				((c1 & 0x00FF00) >>  8)/255f,
				((c1 & 0x0000FF) >>  0)/255f
		};
		float[] rgb2 = c2.getRGBColorComponents(null);
				
		int c = 0;
		for(int i = 0; i < 3; i++) {
			c <<= 8;
			c += (rgb1[i] * rgb2[i]) * 256;
		}
		
		return c;
	}
	
}
