package art.pieces;

import static art.Main.IMAGE_SIZE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;
import art.util.PoissonDisk;
import art.util.PoissonDisk.PVector;

public class Caves implements Piece {
	/**
	 * Will return an image with some sort of root system in an irregular circle, filled with polygons.
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, examples:
	 * <br> - separatingDistance, the distance between polygon points
	 * <br> - drawCircle, if the outer circle should be drawn
	 * <br> - the irr() methoid to change the way the irregular circle is made
	 * <br> - segmentLength, the length of the inner "roots" the smaller the value, the more irregular they look.
	 * <br> - the probability of splitting the root, to make it look more "rooty" i guess
	 * <br> - drawLines, if the root system should be drawn
	 * <br> - drawCells, if the inner polygons should be drawn
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
		
		Random rand = new Random(seed);
		int drawZone = (int) (IMAGE_SIZE/Math.sqrt(2));
		int separatingDistance = drawZone/120;
		ArrayList<PVector> points = PoissonDisk.poissonDiskSampling(seed, separatingDistance, drawZone-separatingDistance);
		for(PVector p : points) {
			p.x += (IMAGE_SIZE-drawZone+separatingDistance)/2;
			p.y += (IMAGE_SIZE-drawZone+separatingDistance)/2;
		}
		System.out.println("Found " + points.size() + " points to draw");
		
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);

		int circleRadius = drawZone/2;
		PVector center = new PVector(IMAGE_SIZE/2, IMAGE_SIZE/2);
		
		boolean drawCircle = true;
		if(drawCircle)
			for(double angle = 0; angle < PI*2; angle+= 0.03) {
				double irr1 = irr(angle, noise);
				double irr2 = irr(angle+0.03, noise);
				
				graphics.drawLine(
						center.x+(int)(cos(angle)*circleRadius*irr1), 
						center.y+(int)(sin(angle)*circleRadius*irr1), 
						center.x+(int)(cos(angle+0.03)*circleRadius*irr2), 
						center.y+(int) (sin(angle+0.03)*circleRadius*irr2));
			}
		
		
		double segmentLength = (double)circleRadius/200;
		List<Segment> segments = new ArrayList<>();
		for(double angle = rand.nextDouble(), i = angle; i < angle+PI*2-PI/20; i+=PI/12) {
			double startX = Math.cos(i)*circleRadius*irr(i, noise);
			double startY = Math.sin(i)*circleRadius*irr(i, noise);
			double dir = i + PI + rand.nextFloat()*0.1f;
			double endX = startX + Math.cos(dir) * segmentLength;
			double endY = startY + Math.sin(dir) * segmentLength;
			segments.add(new Segment(startX, startY, endX, endY));
		}
		
		for(int i = 0; i < segments.size(); i++) {
			Segment seg = segments.get(i);
			if(center.dist(seg.x2, seg.y2) < segmentLength)
				continue;
			double angle = Math.atan2(seg.y2-seg.y1, seg.x2-seg.x1);
			boolean added = false;
			if(rand.nextFloat() < 0.3) { // new segment
				int tries = 0;
				while(tries++ < 5 && !added) {
					double angle1 = angle + 0.4 + rand.nextDouble()*2;
					double angle2 = angle - 0.4 - rand.nextDouble()*2;
					
					Segment s1 = new Segment(seg.x2, seg.y2, seg.x2+cos(angle1)*segmentLength, seg.y2+sin(angle1)*segmentLength);
					Segment s2 = new Segment(seg.x2, seg.y2, seg.x2+cos(angle2)*segmentLength, seg.y2+sin(angle2)*segmentLength);
					if(canPlace(s1, segments, segmentLength*1.5) && canPlace(s2, segments, segmentLength*1.5)) {
						added = true;
						segments.add(s1);
						segments.add(s2);
					}
				}
			}
			if(!added) {
				int tries = 0;
				while(tries++ < 5 && !added) {
					double randAngle = angle + rand.nextDouble()-0.5;
					double correctAngle = Math.atan2(seg.y2, seg.x2)+PI;
					if(Math.abs(randAngle-correctAngle) > 3)
						randAngle += Math.PI*2;
					int balance = 3;
					double newAngle = (randAngle*balance + correctAngle)/(balance+1);
					
					Segment s = new Segment(seg.x2, seg.y2, seg.x2+cos(newAngle)*segmentLength, seg.y2+sin(newAngle)*segmentLength);
					if(canPlace(s, segments, segmentLength*1.5)) {
						segments.add(s);
						added = true;
					}
				}
			}
			
		}
		
		boolean drawLines = true;
		if(drawLines)
			for(Segment seg : segments)
				graphics.drawLine(center.x+(int) seg.x1, center.y+(int) seg.y1, center.x+(int) seg.x2, center.y+(int) seg.y2); 
		
		
		boolean drawCells = false;
		
		if(drawCells) {
			Iterator<PVector> it = points.iterator();
			while(it.hasNext()) {
				PVector p = it.next();
				double angle = Math.atan2(p.y-center.y, p.x-center.x);
				double irregularity = irr(angle, noise);
				if(p.dist(center) > irregularity*circleRadius) {
					it.remove();
				}
				
			}
			
			for(int i = 0; i < points.size(); i++) {
				PVector p = points.get(i);
				
				PVector[] closest = new PoissonDisk.PVector[6];
				double[] dists = new double[closest.length];
				for(int j = 0; j < dists.length; j++) dists[j] = Double.MAX_VALUE;
				
				// look for n closest dots
				for(int j = 0; j < points.size(); j++) {
					if(i==j) continue;
					PVector q = points.get(j);
					double d = p.dist(q);
					if(d > separatingDistance*2)
						continue;
					
					// check if closer
					for(int k = 0; k < dists.length; k++) {
						if(dists[k] > d) {
							// move everything to the left
							for(int l = dists.length-1; l > k; l--) {
								dists[l] = dists[l-1];
								closest[l] = closest[l-1];
							}
							// replace
							dists[k] = d;
							closest[k] = q;
							break;
						}
					}
				}
				
				drawLoop: for(PVector q : closest) {
					if(q != null) {
						for(Segment s : segments)
							if(intersects(s, p.x-IMAGE_SIZE/2, p.y-IMAGE_SIZE/2, q.x-IMAGE_SIZE/2, q.y-IMAGE_SIZE/2))
								continue drawLoop;
						graphics.drawLine(p.x, p.y, q.x, q.y);
					}
				}
			}
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw circle");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	static double irr(double level, double angle, OpenSimplexNoise noise) {
		return (1+noise.eval(cos(angle)*2, sin(angle)*2, level))/2 /4+0.75f;
	}
	
	static double irr(double angle, OpenSimplexNoise noise) {
		return irr(0, angle, noise);
	}
	
	static double dist(double x, double y, double x2, double y2) {
		return Math.sqrt((x-x2)*(x-x2) + (y-y2)*(y-y2));
	}
	
	static boolean canPlace(Segment s, List<Segment> segments, double limit) {
		if(s.x2 < -IMAGE_SIZE || s.y2 < -IMAGE_SIZE || s.x1 > IMAGE_SIZE || s.y1 > IMAGE_SIZE)
			return false;
		if(dist(0,0,s.x2,s.y2) < limit*4 || dist(0,0,s.x1,s.y1) < dist(0,0,s.x2,s.y2))
			return false;
		for(Segment seg : segments)
			if(
					s.x1 != seg.x2 && 
					s.y1 != seg.y2 && 
					dist(s.x2, s.y2, seg.x2, seg.y2) < limit
			) 
				return false;
		return true;
	}
	
	static boolean intersects(Segment s, int x1, int y1, int x2, int y2) {
		double s1_x, s1_y, s2_x, s2_y;
	    s1_x = s.x2 - s.x1;     s1_y = s.y2 - s.y1;
	    s2_x = x2 - x1;     	s2_y = y2 - y1;

	    double s_, t;
	    s_ = (-s1_y * (s.x1 - x1) + s1_x * (s.y1 - y1)) / (-s2_x * s1_y + s1_x * s2_y);
	    t  = ( s2_x * (s.y1 - y1) - s2_y * (s.x1 - x1)) / (-s2_x * s1_y + s1_x * s2_y);

	    if (s_ >= 0 && s_ <= 1 && t >= 0 && t <= 1)
	        // Collision detected
	        return true;

	    return false; // No collision
	}
	
	static class Segment {
		private double x1, y1, x2, y2;
		
		public Segment(double x1, double y1, double x2, double y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}
