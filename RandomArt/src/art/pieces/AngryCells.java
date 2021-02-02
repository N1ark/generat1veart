package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import art.Main;
import art.Piece;
import art.util.PoissonDisk;
import art.util.PoissonDisk.PVector;

public class AngryCells implements Piece {
	
	/**
	 * Will return an image with chaotic star shapes that ressemble the Cells piece.
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, examples:
	 * <br> - drawZone, the zone that will be filled with cells
	 * <br> - separatingDistance, the distance between each dot
	 * <br> - circleSize, the size of the potential circle
	 * <br> - startSearch, the starting index to start looking for neighbours. this will affect how chaotic it looks
	 * <br> - checkSize, to avoid drawing points close to the edge
	 * <br> - checkCircle, to avoid drawing points close to the potential circle
	 * <br> - circle, to draw a circle at the start of the poisson disk generation
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Caves...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.003), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();
		
		int drawZone = (int) (IMAGE_SIZE/Math.sqrt(2));
		int separatingDistance = drawZone/70;
		ArrayList<PVector> points = PoissonDisk.poissonDiskSampling(seed, separatingDistance, drawZone-separatingDistance);
		for(PVector p : points) {
			p.x += (IMAGE_SIZE-drawZone+separatingDistance)/2;
			p.y += (IMAGE_SIZE-drawZone+separatingDistance)/2;
		}
		System.out.println("Found " + points.size() + " points to draw");
	//	Collections.shuffle(points, rand);
		
		int circleSize = IMAGE_SIZE/9;

		
		graphics.setColor(new Color(0, 0, 0, 0.8f));
		for(int shape = 0; shape < points.size(); shape++) {
			PoissonDisk.PVector p = points.get(shape);
			PoissonDisk.PVector[] closest = new PoissonDisk.PVector[3];
			double[] dists = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
			
			int startSearch = shape < points.size()/5 ? points.size(): points.size()*3/4;// < points.size() * 29 / 30 ? points.size()*29/30 : shape+1;
			
			for(int shape2 = startSearch; shape2 < points.size(); shape2++) {
				if(shape == shape2)
					continue;
				PoissonDisk.PVector q = points.get(shape2);
				double d = p.dist(q);
				for(int i = 0; i < dists.length; i++) {
					if(dists[i] > d) {
						for(int j = dists.length-1; j > i; j--) {
							dists[j] = dists[j-1];
							closest[j] = closest[j-1];
						}
						dists[i] = d;
						closest[i] = q;
						break;
					}
				}
			}
			
			boolean checkSides = false;
			boolean checkDistCircle = true;
			for(PVector q : closest) {
				if(
						q != null && 
						(!checkSides || 
							p.x > (IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							q.x > (IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							p.y > (IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							q.y > (IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							p.x < IMAGE_SIZE-(IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							q.x < IMAGE_SIZE-(IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							p.y < IMAGE_SIZE-(IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f &&
							q.y < IMAGE_SIZE-(IMAGE_SIZE-drawZone+separatingDistance)/2*1.1f
						) &&
						(!checkDistCircle ||
							p.dist(points.get(0)) >	circleSize*4 &&
							q.dist(points.get(0)) > circleSize*4
						)
				)
					graphics.drawLine(p.x, p.y, q.x, q.y);
			}
		}
		boolean circle = true;

		if(circle) {
			graphics.drawOval(points.get(0).x-circleSize/2, points.get(0).y-circleSize/2, circleSize, circleSize);
			for(double angle = 0; angle < Math.PI*2-0.1; angle +=0.1) {
				
				PVector p = new PVector(points.get(0).x+Math.cos(angle)*circleSize/2, points.get(0).y+Math.sin(angle)*circleSize/2);
				
				double[] dists = {Double.MAX_VALUE};
				PoissonDisk.PVector[] closest = new PoissonDisk.PVector[dists.length];
				
				int startSearch = points.size()*3/4;
				
				for(int shape2 = startSearch; shape2 < points.size(); shape2++) {
					PoissonDisk.PVector q = points.get(shape2);
					double d = p.dist(q);
					for(int i = 0; i < dists.length; i++) {
						if(dists[i] > d) {
							for(int j = dists.length-1; j > i; j--) {
								dists[j] = dists[j-1];
								closest[j] = closest[j-1];
							}
							dists[i] = d;
							closest[i] = q;
							break;
						}
					}
				}
				
				for(PVector q : closest) {
					if(q != null)
						graphics.drawLine(p.x, p.y, q.x, q.y);
				}
			}
		}
		
		
		System.out.println(System.currentTimeMillis()-a + "ms to draw circle");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}

}
