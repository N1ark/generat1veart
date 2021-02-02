package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Triangle implements Piece{

	/**
	 * Will generate an image, containg two triangles composed by hunderds of lines, that will meet to create a darker triangle on the
	 * upper right corner.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - lineAmount, it determines the amount of lines on a triangle. Increasing this number will increase the time needed to generate,
	 * so instead of setting it to a very high value (2000 +), increasing the opacity and lowering the opacity can give a quicker result.
	 * <br> - alpha, it will change the opacity of each line, so the higher the amount of lines, the lower this value should be.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Triangle...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.drawString(seed + "", 5, 10);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.001), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();
		
		Color c = Color.getHSBColor(rand.nextFloat(), 1, 0.5f);
		int red = c.getRed();
		int green = c.getGreen();
		int blue = c.getBlue();
		int alpha = 60;
		int lineAmount = 600; // The amount of line on a side of the triangle
		Color color = new Color(red, green, blue, alpha);
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // Le carre aura une aire egale a la partie blanche
		System.out.println(System.currentTimeMillis()-a + "ms to do math");
		a = System.currentTimeMillis();
		
		graphics.setColor(color);
		for(double i = 0; i < squareSize/2.0; i+=squareSize/(2.0*lineAmount)) {
			int offY = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(squareSize/(lineAmount/10));
			int offX = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(squareSize/(lineAmount/40));
			graphics.drawLine(
					(int) (IMAGE_SIZE/2 + i), 
					(int) (IMAGE_SIZE/2+squareSize/2-i), 
					(int) (IMAGE_SIZE/2 + i) + offX, 
					IMAGE_SIZE/2-squareSize/2 + offY
					);
		}
		for(double i = 0; i < squareSize/2.0; i+=squareSize/(2.0*lineAmount)) {
			int offX = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(squareSize/(lineAmount/10));
			int offY = (rand.nextBoolean() ? 1 : -1) * rand.nextInt(squareSize/(lineAmount/40));
			graphics.drawLine(
					(int) (IMAGE_SIZE/2 + squareSize/2 - i), 
					(int) (IMAGE_SIZE/2 - i), 
					IMAGE_SIZE/2-squareSize/2 + offX,
					(int) (IMAGE_SIZE/2 - i) + offY
					);
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
