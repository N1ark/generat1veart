package art.pieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Lines implements Piece{
	/**
	 * Will generate an image, containg a set of black rectangles, that will gradually offset themselves, while their normal
	 * position is behind them, in a color that will gradually change.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - amountLines, that will decide on how many lines should appear on the image. This piece is really simple, so increasing
	 * this number won't significantly change the time needed to generate.
	 * <br> - If done carefully, the generator behind stretchX and stretchY can be changed, to change the final offset of the rectangle.
	 * This should be done carefully, because the current settings are, in my opinion, really nice and changing them can make
	 * everything ugly if not perfect.
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
		int amountLines = 50; // The amount of lines on the piece
		int lineWidth = squareSize/(amountLines*2-1); // The width of the line, knowing that the space between each line is equal to the width of each line
		int stretchX = (rand.nextBoolean() ? 1 : -1) * ((IMAGE_SIZE-squareSize)/7 + rand.nextInt((IMAGE_SIZE-squareSize)/8)); // L'ecart final en x
		int stretchY = (rand.nextBoolean() ? 1 : -1) * ((lineWidth / 2) + rand.nextInt(lineWidth/2)); // L'ecart final en y
		float hue = rand.nextFloat(); // The beginning hue
		float incHue = rand.nextFloat() * 0.3f; // The value by which the hue will increase
		System.out.println("The stretch is " + stretchX + "/" + stretchY);
		System.out.println("The line width is " + lineWidth);
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();

		for(int i = 0; i < amountLines; i++) {
			graphics.setColor(Color.getHSBColor(hue += incHue / amountLines, 1, 1));
			graphics.fillRect(squareStart, squareStart + (i * lineWidth * 2), squareSize, lineWidth);
			graphics.setColor(Color.BLACK);
			graphics.fillRect(
					(int) (squareStart + i * (float) stretchX / amountLines), 
					(int) (squareStart + (i * lineWidth * 2) + i * (float) stretchY / amountLines), 
					squareSize, 
					lineWidth
			);
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	
	
}
