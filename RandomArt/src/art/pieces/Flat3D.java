package art.pieces;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;
import art.util.PoissonDisk;
import art.util.PoissonDisk.PVector;

public class Flat3D implements Piece{
	
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
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.drawString(seed + "", 5, 10);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.003), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		List<PVector> points = PoissonDisk.poissonDiskSampling(seed, squareSize/70, squareSize);
		int segments = 180;
		double TWO_PI = Math.PI*2;
		float shapeSize = squareSize/2;
		
		System.out.println(System.currentTimeMillis()-a + "ms to generate points");
		a = System.currentTimeMillis();
		
		graphics.setColor(Color.BLACK);
		
		for(double j = 0; j <= 1; j+= 0.1) {
			float prevX = IMAGE_SIZE/2 + irr(0, j, noise)*shapeSize;
			float prevY = IMAGE_SIZE/2;
			for(double i = TWO_PI/segments; i <= TWO_PI; i += TWO_PI/segments) {
				float irr = irr(i, j, noise);
				
				float x = IMAGE_SIZE/2 + (float) (cos(i)*shapeSize*irr);
				float y = IMAGE_SIZE/2 + (float) (sin(i)*shapeSize*irr);
				
				graphics.drawLine(round(prevX), round(prevY), round(x), round(y));
				
				
				prevX = x;
				prevY = y;
			}
		}
		
		int offset = (int) ((IMAGE_SIZE)/2-shapeSize);
		for(PVector p : points) {

			p.x += offset;
			p.y += offset;

			boolean buggy = true;
			double tan = buggy ? 
					atan2(p.y-IMAGE_SIZE/2-squareSize*rand.nextFloat(), p.x-IMAGE_SIZE/2-squareSize*rand.nextFloat())*(rand.nextFloat()*0.5+0.5) :
						atan2(p.y-IMAGE_SIZE/2, p.x-IMAGE_SIZE/2);

			float irr = irr(tan, 0, noise);
			double dist = dist(IMAGE_SIZE/2, IMAGE_SIZE/2, p.x, p.y);
			dist = Math.sqrt(dist);
			
			if(dist < shapeSize*irr)
				graphics.fillOval(p.x-squareSize/400, p.y-squareSize/400, squareSize/200, squareSize/200);
		}
		
		System.out.println(System.currentTimeMillis()-a + "ms to finish");
		a = System.currentTimeMillis();
		
		
		return image;
	}
	
	static float dist(double x1, double y1, double x2, double y2) {
		return (float) ((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	static float irr(double angle, OpenSimplexNoise noise) {
		return irr(angle, 0, noise);
	}
	
	static float irr(double angle, double z, OpenSimplexNoise noise) {
		double x = cos(angle);
		double y = sin(angle);
		return (float) ((noise.eval(x, y, z)/2+0.5)*0.9+0.1);
	}

}
