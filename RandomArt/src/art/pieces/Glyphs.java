package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Glyphs implements Piece {
	/**
	 * Will return an image with tons of small glyphs.
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - amountSquares, the amount of squares on the width (iirc).
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Connect...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(new Color(0x404040));
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(new Color(0xD0D0D0));
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int amountSquares = 20;
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int lineWidth = squareSize/20; // The width of the line
		int glyphSize = (int) (squareSize/(amountSquares*1.5));
		int glyphDist = glyphSize + (squareSize - amountSquares * glyphSize)/(amountSquares-1);
		graphics.setStroke(new BasicStroke(lineWidth/2f));
		System.out.println("The line width is " + lineWidth);
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();
		
		for(int i = 0; i < amountSquares*amountSquares; i++) {
			glyph(
					graphics, rand, 
					squareStart + (i%amountSquares) * glyphDist,
					squareStart + (i/amountSquares) * glyphDist,
					glyphSize
			);
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	private static void glyph(Graphics2D graphics, Random rand, int x, int y, int glyphSize) {
		int distDots = glyphSize/2;
		int lineWidth = distDots/10; // The width of the line
		int[] connected = new int[rand.nextInt(3)+4];
		for(int i = 0; i < connected.length; i++)
			connected[i] = -1;
		for(int i = 0; i < connected.length; i++) {
			int num;
			do {
				num = rand.nextInt(9);			
			} while (contains(num, connected));
			connected[i] = num;
		}
		graphics.setStroke(new BasicStroke(lineWidth/2f));
		long a = System.currentTimeMillis();

		for(int i = 0; i < 9; i++) {
			graphics.fillOval(
					x + distDots * (i%3) - lineWidth/2, 
					y + distDots * (i/3) - lineWidth/2, 
					lineWidth, lineWidth);
		}
		int last = connected[0];
		for(int i = 1; i < connected.length; i++) {
			int lx = x + distDots * (last%3);
			int ly = y + distDots * (last/3);
			int nx = x + distDots * (connected[i]%3);
			int ny = y + distDots * (connected[i]/3);
			if(rand.nextInt(3) != 0) {
				graphics.drawLine(lx, ly, nx, ny);
			} else {
				int p1, p2;
				do {
					p1 = rand.nextInt(9);
				} while (p1 == last || p1 == connected[i]);
				do {
					p2 = rand.nextInt(9);
				} while (p2 == last || p2 == connected[i] || p2 == p1);
				System.out.println("Curve: " + last + "->" + connected[i] + " (" + p1 + "/" + p2 + ")");
				int p1x = x + distDots * (p1%3);
				int p1y = y + distDots * (p1/3);
				int p2x = x + distDots * (p2%3);
				int p2y = y + distDots * (p2/3);
				CubicCurve2D curve = new CubicCurve2D.Double();
				curve.setCurve(lx, ly, p1x, p1y, p1x, p1y, nx, ny);
				graphics.draw(curve);
			}
			
			last = connected[i];
		}
		
		System.out.println("Drew glyph in " + (System.currentTimeMillis()-a) + "ms");
	}
	
	private static boolean contains(int searched, int[] values) {
		for(int i : values)
			if(i == searched)
				return true;
		return false;
	}
}
