package art.pieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Gradient implements Piece{

	/**
	 * Will generate an image, containg a bunch of comet-like shapes, that will move around the screen until they meet
	 * the corner of an invisible square, going in a gradient.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - amountLines, will increase the time needed to generate the image, but can be used to create a continous
	 * change in colours
	 * <br> - lineWidth, will increase the time needed to generate the image, but can be used to create pretty stuff.
	 * <br> - lineSize, the max size of a line, which is reached rarely.
	 * <br> - hueChange, wether each comet has a random color, or if all of them have the same one.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Gradient...");
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
		int amountLines = 5000; // The amount of lines on the piece
		int lineWidth = squareSize/100; // The width of the line, knowing that the space between each line is equal to the width of each line
		int lineSize = (int) (squareSize * 0.8); // The size of a line
		boolean sameDir = rand.nextBoolean();
		int dir = (rand.nextBoolean() ? 1 : -1);
		float hue = rand.nextFloat(); // The beginning hue
		boolean hueChange = false;
		float incHue = (rand.nextBoolean() ? 1 : -1) * (0.15f + rand.nextFloat()*0.15f); // The value by which the hue will increase
		System.out.println("Width is " + lineWidth);
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();

		for(int i = 0; i < amountLines; i++) {
			if(hueChange)
				hue = rand.nextFloat();
			dir = (rand.nextBoolean() ? 1 : -1);
			int x = (int) (squareStart + rand.nextFloat() * squareSize);
			int y = (int) (squareStart + rand.nextFloat() * squareSize);
			double mult = (0.8 + 0.2 *  (amountLines-i) / amountLines);
			System.out.println("Mult is " + mult);
			int width = (int) (lineWidth * mult);
			System.out.println("Size: " + lineSize + " Width: " + width + " Starting at " + x + "/" + y);
			
			graphics.setColor(Color.getHSBColor(hue, 1, 1));
			int j;
			for(j = 0; j < lineSize; j++) {
				
				if(j > lineWidth/2)
					graphics.setColor(Color.getHSBColor(hue + incHue * ((j-lineWidth/2f)/lineSize), 1, 1));
				
				int px = x + j * dir;
				int py = y + j * (sameDir? -dir : dir);
				
				if(squareStart+squareSize < px || px < squareStart || squareStart+squareSize < py || py < squareStart)
					break;
				
				graphics.fillOval(px - width/2 , py - width/2 , width, width);
			}
			System.out.println("Did oval " + i + " with " + j + " circles");
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
