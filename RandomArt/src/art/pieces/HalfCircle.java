package art.pieces;

import static art.Main.IMAGE_SIZE;
import static java.lang.Math.round;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;

public class HalfCircle implements Piece{
	/**
	 * Will generate an image, containg a set of black rectangles, that will gradually offset themselves, while their normal
	 * position is behind them, in a color that will gradually change.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - amountLines, that will decide on how many lines should appear on the image. This piece is really simple, so increasing
	 * this number won't significantly change the time needed to generate.
	 * <br> - If done carefully, the generator behind stretchX and stretchY can be changed, to change the final offset of the rectangle.
	 * This should be done carefully, because the current settings are, in my opinion, really nice and changing them can make
	 * everything ugly if not perfect.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Lines...");
		long chr = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(new Color(0x111111));
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.WHITE);
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-chr + "ms to generate basic image");
		chr = System.currentTimeMillis();

		int circleRadius = (int) (IMAGE_SIZE/Math.sqrt(2))/2; // The square should have the same area as the white part
		int squareCenter = (IMAGE_SIZE )/2;
		int amountLines = 200; // The amount of lines on the piece
		float hue = rand.nextFloat(); // The beginning hue
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		double segSize = circleRadius/1000;
		double startAngle = rand.nextFloat()*Math.PI*2;
		List<Segment> segments = new ArrayList<>();
		segments.add(new Segment(
				Math.cos(startAngle)*circleRadius, 
				Math.sin(startAngle)*circleRadius, 
				Math.cos(startAngle)*(circleRadius-segSize), 
				Math.sin(startAngle)*(circleRadius-segSize)
		));
		Segment last;
		double varAngle = 0.06f;
		while(dist((last = segments.get(segments.size()-1)).x2, last.y2, 0, 0) < circleRadius*circleRadius){
	//		varAngle = (noise.eval(segments.size()/40f, 0)+1)/2 * 0.3f;
			double angle = Math.atan2(last.y2-last.y1, last.x2-last.x1);
			double var = rand.nextFloat()*varAngle-varAngle/2;
			angle += var;
			segments.add(new Segment(last.x2, last.y2, last.x2+Math.cos(angle)*segSize, last.y2+Math.sin(angle)*segSize));
		}
		
		
		graphics.setColor(Color.WHITE);
		graphics.setStroke(new BasicStroke(circleRadius/200, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));

		for(int i = 0; i < amountLines; i++) {
			double angle = Math.PI*2/amountLines*i;
			
			double startX = Math.cos(angle)*circleRadius;
			double startY = Math.sin(angle)*circleRadius;
			
			Segment best = null;
			double distBest = Double.MAX_VALUE;
			for(Segment s : segments) {
				double dist = dist(startX, startY, s.x1, s.y1);
				if(dist < distBest) {
					distBest = dist;
					best = s;
				}
			}
			
			graphics.drawLine((int) startX+squareCenter, (int) round(startY)+squareCenter, (int) round(best.x1)+squareCenter, (int) round(best.y1)+squareCenter);
		}

		graphics.setStroke(new BasicStroke(circleRadius/50, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for(int i = 0; i < segments.size(); i++) {
			Segment seg = segments.get(i);
			draw(graphics, seg, squareCenter);
		}
		
		
		
		return image;
	}
	
	static void draw(Graphics2D g, Segment s, int offset) {
		g.drawLine((int) round(s.x1)+offset, (int) round(s.y1)+offset, (int) round(s.x2)+offset, (int) round(s.y2)+offset);
	}
	
	static double dist(double x, double y, double x2, double y2) {
		return (x-x2)*(x-x2) + (y-y2)*(y-y2);
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
