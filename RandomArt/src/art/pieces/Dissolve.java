package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Dissolve implements Piece{
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
		int amount = 40; // The amount of circles on the piece
		int circleSize = squareSize/amount; // The width of the circle
		float dissolve = 0.024f;
		float hue = rand.nextFloat(); // The beginning hue
		float incHue = (rand.nextBoolean() ? 1 : -1) * 0.3f; // The value by which the hue will increase
		boolean willRespawn = true;
		boolean randomizeColor = true;
		graphics.setStroke(new BasicStroke(Math.max(1f, circleSize)*0.1f));
		System.out.println("The circle size is " + circleSize);
		System.out.println("Inc Hue is " + incHue);
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();

		boolean[] isDead = new boolean[amount];
		for(int i = 0; i < amount; i++)
			isDead[i] = false;
		for(int i = 0; i < amount; i++) {
			graphics.setColor(Color.getHSBColor(hue, 1, 0.8f));
			for(int j = 0; j < amount; j++) {
				if(randomizeColor)
					graphics.setColor(Color.getHSBColor(
							hue + (rand.nextBoolean() ? 1 : -1) * (rand.nextFloat() * 0.04f), // Slightly randomize color
							1, 0.8f));
				boolean dissolved = rand.nextFloat() > 1f - i * dissolve;
				if(!willRespawn && dissolved)
					isDead[j] = true;
				if(!dissolved)
					graphics.drawOval(squareStart + j * circleSize, squareStart + i * circleSize, circleSize, circleSize);
			}
			hue += incHue/amount;
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
