package art.pieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Ikea implements Piece {

	@Override
	public BufferedImage generate(long seed) {
		Color[] swedishColors = new Color[] {
				new Color(0x5140b9),
				new Color(0x1a6b93),
				new Color(0x8cb7f0),
				new Color(0x0f9590),
				new Color(0x7b2099)
		};
		
		Color[] partyColorsFull = new Color[] {
				new Color(0xf8efd4),
				new Color(0xedc988),
				new Color(0xd7385e),
				new Color(0x132743)
		};
		
		Color[] partyColorsWRed = new Color[] {
				new Color(0xf8efd4),
				new Color(0xedc988),
				new Color(0xd7385e)
		};
		
		Color[] partyColorsWBlue = new Color[] {
				new Color(0xf8efd4),
				new Color(0xedc988),
				new Color(0x132743)
		};
		
		Color[] colors = partyColorsWRed;
		
		
		System.out.println("Generating Ikea...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int amountTriangles = 1000; // The amount of triangles on the piece
		int averageSize = 800; // The average distance between points of the triangle
		int variation = 10; // The variation in the distance
		float maxAngleVar = (float) (2*Math.PI*5/360);
		
		for(int i = 0; i < amountTriangles; i++) {
			graphics.setColor(colors[rand.nextInt(colors.length)]);
			int[] xPoints = new int[3];
			int[] yPoints = new int[3];
			xPoints[0] = rand.nextInt(IMAGE_SIZE);
			yPoints[0] = rand.nextInt(IMAGE_SIZE);
			
			float dist1 = averageSize + rand.nextFloat()*variation*nextSign(rand);
			float angle1 = (float) (rand.nextFloat()*2*Math.PI);
			xPoints[1] = (int) (xPoints[0] + Math.cos(angle1)*dist1);
			yPoints[1] = (int) (yPoints[0] + Math.sin(angle1)*dist1);
			
			float dist2 = averageSize + rand.nextFloat()*variation*nextSign(rand);
			float angle2 = angle1 + (float) (rand.nextFloat()*maxAngleVar*nextSign(rand) +2*Math.PI*60/360*nextSign(rand));
			xPoints[2] = (int) (xPoints[0] + Math.cos(angle2)*dist2);
			yPoints[2] = (int) (yPoints[0] + Math.sin(angle2)*dist2);
			
			graphics.fillPolygon(xPoints, yPoints, 3);
		}
		
		
		System.out.println(System.currentTimeMillis()-a + "ms to draw triangles");
		graphics.setColor(Color.BLACK);
		graphics.drawString(seed + "", 5, 10);
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	
	static int nextSign(Random rand) {
		return rand.nextBoolean() ? 1 : -1;
	}
}
