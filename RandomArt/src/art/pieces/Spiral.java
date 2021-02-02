package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Spiral implements Piece{
	/**
	 * Will return an image with colored dots that will spiral around, and eventually with smaller dots orbiting them.
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - radius, the intial radius for the start of the spiral
	 * <br> - spins, the amount of spins the spiral should make
	 * <br> - increase, by how many pixels should the radius be increased at every turn
	 * <br> - points, the amount of points that should take part in the spiral
	 * <br> - size, the size of an individual dot
	 * <br> - nearPoints, the amount of dots orbiting a circle
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Spiral...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.003), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();
		
		
		float hue = rand.nextFloat();
		int radius = 200;
		int spins = 3;
		int increase = 400;
		int points = 200;
		float hueInc = 0.15f/points;
		int size = 40;
		int nearPoints = 1;
		System.out.println(System.currentTimeMillis()-a + "ms to do math");
		a = System.currentTimeMillis();
		
		for(int i = 0; i < points; i++) {
			double angle = i*(Math.PI*2*spins)/points;
			double distance = radius + i * (increase*spins)/points;
			graphics.setColor(Color.getHSBColor(hue + i * hueInc, 1, 1));
			int x1 = IMAGE_SIZE/2 + (int) (Math.cos(angle) * distance);
			int y1 = IMAGE_SIZE/2 + (int) (Math.sin(angle) * distance);
			graphics.fillOval(x1, y1, size, size);
			for(int j = 0; j < nearPoints; j++) {
				double angle2 = rand.nextDouble()*2*Math.PI;
				int x2 = (int) (x1 + Math.cos(angle2) * size * 1.5);
				int y2 = (int) (y1 + Math.sin(angle2) * size * 1.5);
				graphics.fillOval(x2+size/4, y2+size/4, size/2, size/2);
			}
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw dots");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
