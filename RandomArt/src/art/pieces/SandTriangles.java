package art.pieces;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;
import art.util.PoissonDisk.PVector;

public class SandTriangles  implements Piece{
	/**
	 * Will generate an image, containg sand that's initially organised in triangles that will be propagated, following the force
	 * applied to it.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - trianglesPerLine, the amount of triangles per line
	 * <br> - sandSize, the size in pixels of a sand. this value should be quite small
	 * <br> - sandAmount, the amount of sand particles to create for each triangle side
	 * <br> - maxForce, the max force that can be applied to the sand when it's at the optimal position
	 * <br> - bestDist, the best distance from the image center, where the force is the largest.
	 * <br> - A bunch of stuff can be changed and customised in the sand loop to change how the force is 
	 * applied to the sand. Some wildly different results can be obtained! 
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Lines...");
		long chr = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(new Color(0x111111));
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.WHITE);
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-chr + "ms to generate basic image");
		chr = System.currentTimeMillis();

		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int trianglesPerLine = 31;
		int triangleSize = squareSize/trianglesPerLine;
		int sandSize = IMAGE_SIZE/1000;
		int sandAmount = 500;
		List<PVector> points = new ArrayList<>();
		
		List<PVector> sand = new ArrayList<>();
		
		for(int i = 0, y = squareStart; y < squareStart+squareSize; y+= triangleSize, i++) {
			for(int x = (i%2 == 0) ? squareStart : squareStart+triangleSize/2; x < squareStart+squareSize; x+= triangleSize) {
				points.add(new PVector(x, y));
				
				if(x+triangleSize/2 < squareStart+squareSize && y+triangleSize < squareStart+squareSize)
					drawSandLine(sand, sandSize, sandAmount, x, y, x+triangleSize/2, y+triangleSize);
				if(x-triangleSize/2 >= squareStart && y+triangleSize < squareStart+squareSize)
					drawSandLine(sand, sandSize, sandAmount, x, y, x-triangleSize/2, y+triangleSize);
				if(x+triangleSize < squareStart+squareSize)
					drawSandLine(sand, sandSize, sandAmount, x, y, x+triangleSize, y);
			}
		}
		
		System.out.println(System.currentTimeMillis()-chr + "ms to generate sand");
		chr = System.currentTimeMillis();
		
		graphics.setColor(new Color(1,1,1,0.03f));
		float maxForce = 500;
		float bestDist = squareSize/3;
		
		float a = -4 * maxForce / (bestDist*bestDist);
		float b = 4 * maxForce / bestDist;
		
		for(PVector p : sand) {
			double var = noise.eval(p.x/10f, p.y/10f)/2+0.5; // some noise variation
			var *= (rand.nextFloat()*0.6f+0.7f); // some random variation
			double dist = p.dist(IMAGE_SIZE/2, IMAGE_SIZE/2);
			double force = Math.max(0,a*dist*dist + b*dist);
			double actualForce = force * (var*0.4+0.6);
			double forceAngle = Math.atan2(p.y-IMAGE_SIZE/2,p.x-IMAGE_SIZE/2) - Math.PI/2;
			
			p.add(
					(int) (Math.cos(forceAngle) * actualForce), 
					(int) (Math.sin(forceAngle) * actualForce)
					);
			
			graphics.fillOval(p.x-sandSize/2, p.y-sandSize/2, sandSize, sandSize);
		}
		
		System.out.println(System.currentTimeMillis()-chr + "ms to move and draw sand");
		chr = System.currentTimeMillis();
		
		return image;
	}
	
	Random rand;
	
	public void drawSandLine(Graphics g, int size, int n, int x1, int y1, int x2, int y2) {
		for(int i = 0; i < n; i++) {
			float v = rand.nextFloat();
			float xp = x1 + v*(x2-x1) + size*(rand.nextFloat()-0.5f);
			float yp = y1 + v*(y2-y1) + size*(rand.nextFloat()-0.5f);
			g.fillOval(round(xp-size/2f), round(yp-size/2f), size, size);
		}
	}
	
	public void drawSandLine(List<PVector> list, int size, int n, int x1, int y1, int x2, int y2) {
		for(int i = 0; i < n; i++) {
			float v = rand.nextFloat();
			float xp = x1 + v*(x2-x1) + size*(rand.nextFloat()-0.5f);
			float yp = y1 + v*(y2-y1) + size*(rand.nextFloat()-0.5f);
			list.add(new PVector(round(xp-size/2f), round(yp-size/2f)));
		}
	}

}
