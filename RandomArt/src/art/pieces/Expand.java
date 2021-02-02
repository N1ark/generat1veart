package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Expand implements Piece{
	/**
	 * Will generate an image, containg some circles, that will gradually dissolve themselves, and maybe also change color.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - amount, the amount of circle on a side of the square. Having more circle can make nice results, but rendering it
	 * at a low resolution will make it look terrible.
	 * <br> - dissolve, a percentage, that should indicate what is the probability of it dissolving at the first layer. To get
	 * the probability of a certain layer, do <code> prob = layer * iniProb </code>>
	 * <br> - willRespawn, if a row where a circle died can still give circles in the next layer.
	 * <br> - randomizeColor, if each circle can have a slightly different color from the rest of the layer. This can be used
	 * to give a nicer, more natural, result.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Lines...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int amount = 10; // The amount of squares on a size
		int innerAmount = 10;
		int size = Math.round((float)squareSize/amount); // The width of the large square
		int innerSize = Math.round((float)size/innerAmount);
		float dissolve = 0.11f;
		float hue = rand.nextFloat(); // The beginning hue
		float incHue = (rand.nextBoolean() ? 1 : -1) * 0.2f; // The value by which the hue will increase
		boolean randomizeColor = true;
		graphics.setStroke(new BasicStroke(Math.max(1f, innerSize)*0.1f));
		System.out.println("The inner square size is " + innerSize);
		System.out.println(squareSize + "->" + size + "->" + innerSize + " and " + ((float)squareSize/amount) + " and " + ((float)squareSize/(amount*innerAmount)));
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();

		for(int i = 0; i < amount; i++) {
			for(int j = 0; j < amount; j++) {
				int dist = Math.abs(i - (amount/2)) + Math.abs(j - (amount/2));
				graphics.setColor(Color.getHSBColor(hue + incHue/amount * dist, 1, 0.8f));
				System.out.println(i + "/" + j + ", distance: " + dist);
				float prob = dissolve * dist;
				for(int ii = 0; ii < innerAmount; ii++) {
					for(int jj = 0; jj < innerAmount; jj++) {
						if(rand.nextFloat() > prob) {
							if(randomizeColor)
								graphics.setColor(Color.getHSBColor(
										hue + incHue/amount * dist + (rand.nextBoolean() ? 1 : -1) * (rand.nextFloat() * 0.04f), 
										1, 0.8f));
							graphics.drawOval(
									squareStart + i * size + ii * innerSize, 
									squareStart + j * size + jj * innerSize, 
									innerSize, innerSize);
						}
					}
				}
			}
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
