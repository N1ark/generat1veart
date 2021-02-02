package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.PoissonDisk.PVector;

public class Worm implements Piece {
	/**
	 * Will generate an image, with a random bezier curve in the middle, that will reach between two empty circle, and filled with
	 * smaller circles, and a bunch of rays, representing a segment of the curve.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - minDistance, the minimum distance between the start and end of the curve, increasing the value will require more
	 * calculations to find a good spot, and might even lock the program, if no empty spot is found!
	 * <br> - circleSize, the size of the circles for the curve and for the smaller dots.
	 * <br> - moreData, if set to true there will be a gray rectangle around the curve, and a red line over the usual striped line.
	 * <br> - the size of dots, will change the amount of smaller circles.
	 * <br> - barLength, will change the size of an individual line.
	 * <br> - barSize, will change inc (see below).
	 * <br> - inc, increasing it will put more space between each line.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Worm...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.drawString(seed + "", 5, 10);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.008), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		// Math, determiner les points pour la courbe
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2));
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int minDistance = squareSize/2;
		int circleSize = (int) (IMAGE_SIZE * 0.5 * 0.04);
		boolean moreData = false;
		PVector[] dots = new PVector[8];
		PVector[] points = new PVector[2]; // Must be 4
		PVector[] control = new PVector[] {
				 new PVector(squareStart + rand.nextFloat() * squareSize, squareStart + rand.nextFloat() * squareSize),
				 new PVector(squareStart + rand.nextFloat() * squareSize, squareStart + rand.nextFloat() * squareSize)
		};
		boolean ok = false;
		for(int i = 0; i < points.length; i++)
			do {
				ok = true;
				System.out.println("Making point " + i);
				points[i] = new PVector(squareStart + rand.nextFloat() * squareSize, squareStart + rand.nextFloat() * squareSize);
				for(int j = 0; j < i; j++)
					if(points[j].dist(points[i]) < minDistance)
						ok = false;
			} while (!ok); // Will simply try to get points that are farther that minDistance
		
		CubicCurve2D curve = new CubicCurve2D.Double();
		curve.setCurve(points[0].x, points[0].y, control[0].x, control[0].y, control[1].x, control[1].y, points[1].x, points[1].y);
		Rectangle bounds = curve.getBounds();
		
		// Math, determiner les points aleatoires
		int minDotDistance = Math.max(bounds.width, bounds.height) / dots.length;
		System.out.println("Minimum dot distance is " + minDotDistance);
		for(int i = 0; i < dots.length; i++)
			do {
				ok = true;
				System.out.println("Making dot " + i);
				dots[i] = new PVector(
						bounds.x + rand.nextFloat() * bounds.width, 
						bounds.y + rand.nextFloat() * bounds.height);
				for(int j = 0; j < i; j++)
					if(dots[j].dist(dots[i]) < minDotDistance)
						ok = false;
			} while(!ok);

		System.out.println(System.currentTimeMillis()-a + "ms to do math");
		a = System.currentTimeMillis();

		// Dessiner la courbe, et les cercles
		
		if(moreData) {
			graphics.setColor(Color.DARK_GRAY);
			graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
		
		graphics.setColor(Color.WHITE);
		graphics.draw(curve);
		graphics.drawOval(points[0].x-circleSize/2, points[0].y-circleSize/2, circleSize, circleSize);
		graphics.drawOval(points[1].x-circleSize/2, points[1].y-circleSize/2, circleSize, circleSize);
		for(int i = 0; i < dots.length; i++)
			graphics.fillOval(
					(int) (bounds.x + rand.nextFloat() * bounds.width), 
					(int) (bounds.y + rand.nextFloat() * bounds.height), 
					circleSize/2, circleSize/2
			);
		
		// Dessiner les traits
		
		PathIterator pi = curve.getPathIterator(null);
		pi.next();
		double[] coords = new double[6];
		pi.currentSegment(coords);
		double x1, y1, x2, y2;
		if(rand.nextBoolean()) { // Determiner quelle droite est dessinnée
			x1 = coords[0];
			y1 = coords[1];
			x2 = coords[2];
			y2 = coords[3];
		} else {
			x1 = coords[2];
			y1 = coords[3];
			x2 = coords[4];
			y2 = coords[5];
		}
		
		// Calculer l'equation de la droite
		double m = (y2-y1) / (x2-x1);
		double p = y1 - m * x1;
		System.out.println("M: " + m + " / P :" + p);
		double barSize = (int) (IMAGE_SIZE * 0.5 * 0.01);
		double barLength = (int) (IMAGE_SIZE * 0.5 * 0.05);
		// Calculer les degres etc.
		
		System.out.println("Compare " + Math.abs(x1/x2) + " and " + Math.abs(y1/y2));
		
		if(Math.abs(x1/x2) < Math.abs(y1/y2)) { // Verticalish line
			double angle = Math.atan(Math.abs(y1 - y2) / Math.abs(x1 - x2)) + Math.PI/2.0;
			System.out.println("Angle Vertical " + angle + " (or " + Math.toDegrees(angle) + " in degrees)");
			double inc = Math.abs(Math.cos(angle) * barSize * 3.0);
			double sin = (int) (Math.sin(angle) * barLength);
			double cos = (int) (Math.cos(angle) * barLength);
			double start =  Math.min(y1, y2);
			double end = Math.max(y1, y2);
			System.out.println("Start at Y " + start + " until " + end + " and increase by " + inc);
			System.out.println("Will need " + ((start - end) / inc) + " stripes");
			if(inc > 1)
				for(double y = start; y < end; y += inc) {
					int x = (int) ((y-p)/m); 
					graphics.drawLine((int) (x-sin), (int) (y-cos), (int) (x+sin), (int) (y+cos));
				}
		}
		
		else { // Horizontalish line
			double angle = Math.atan(Math.abs(x1 - x2) / Math.abs(y1 - y2)) + Math.PI/2.0;
			System.out.println("Angle Horizontal " + angle + " (or " + Math.toDegrees(angle) + " in degrees)");
			double inc = Math.abs(Math.cos(angle) * barSize * 3.0);
			double sin = Math.sin(angle) * barLength;
			double cos = Math.cos(angle) * barLength;
			double start = Math.min(x1, x2);
			double end = Math.max(x1, x2);
			System.out.println("Start at X " + start + " until " + end + " and increase by " + inc);
			System.out.println("Will need " + ((start-end) / inc) + " stripes");
			if(inc > 1)
				for(double x = start; x < end; x += inc) {
					double y = m*x+p;
					graphics.drawLine((int) (x-sin), (int) (y-cos), (int) (x+sin), (int) (y+cos));
				}
		}
		if(moreData) {
			graphics.setColor(Color.RED);
			graphics.drawLine((int)x1, (int)y1, (int)x2, (int) y2);
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw points");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}

}
