package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.PoissonDisk;
import art.util.PoissonDisk.PVector;

public class Dots implements Piece{
	/**
	 * Will return an image with a colored dots, that go in a gradient and that follow a Poisson-Disk sampling. 
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - minDistance will change the minimal distance between each points. Reducing it will increase the amount of points, but
	 * the generation will take a lot more time.
	 * <br> - distMult impacts the gradient, so the lower the value, the less the color changes, while a high value will make a rainbow
	 * effet.
	 * <br> - black, if set to true some black dots with a gradual offset will appear, in a similar fasion to Lines.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Cells...");
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
		
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // Le carre aura une aire egale a la partie blanche
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int minDistance = (int) (IMAGE_SIZE*0.5*0.07); // Distance minimale entre 2 points
		int radius = minDistance/2;
		float hue = rand.nextFloat();
		PVector reference = new PVector(0,0);
		boolean black = true;
		float distMult = 0.00006f;
		int stretchX = (int) ((rand.nextBoolean() ? 1 : -1) * (radius*0.2+rand.nextInt(radius)*0.4));
		int stretchY = (int) ((rand.nextBoolean() ? 1 : -1) * (radius*0.2+rand.nextInt(radius)*0.4));
		System.out.println("The minimal distance between two points is " + minDistance + " pixels");
		
		List<PVector> poisson = PoissonDisk.poissonDiskSampling(seed, minDistance, squareSize);
		Collections.sort(poisson, Comparator.comparing(vec -> 1.0/(vec.x+vec.y)));
		System.out.println("There are " + poisson.size() + " dots");
		System.out.println(System.currentTimeMillis()-a + "ms to do math and generate points");
		a = System.currentTimeMillis();
		
		for(PVector dot : poisson) {
			double dist = dot.dist(reference);
			graphics.setColor(Color.getHSBColor((float) (hue + dist * distMult), 1, 1));
			graphics.fillOval(
					squareStart + dot.x, 
					squareStart + dot.y, 
					radius, radius
			);
			if(black) {
				graphics.setColor(Color.BLACK);
				graphics.fillOval(
						squareStart + dot.x + stretchX, 
						squareStart + dot.y + stretchY, 
						radius, radius
				);
			}
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw dots");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
