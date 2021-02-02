package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import art.Main;
import art.Piece;
import art.util.OpenSimplexNoise;
import art.util.PoissonDisk;
import art.util.PoissonDisk.PVector;

public class Stains implements Piece {
	/**
	 * Will return an image with plenty of stains
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, examples:
	 * <br> - drawZone, the zone that will be filled with stains
	 * <br> - sections, the amount of triangles used to draw each shape. a higher number will be slower but more precise
	 * <br> - circles, the amount of circles per shapes, which might increase their irregularity and opacity
	 * <br> - separatingDistance, the distance between each shape center
	 * <br> - mainHue, the main hue used for all shapes. a completely independent hue can also be set for each shape
	 * <br> - irregularity1/irregularity2, a shared float through circle iterations of a shape that determine the variation in size of the shape
	 * <br> - noiseVal1/noiseVal2, another float that determines the variation in size of the shape, but this value is broadly independent for each circle of each shape
	 * <br> - colorNoise, the variation in color for each triangle of the shape
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Seal...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
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
		int sections = 360;
		int circles = 20;
		int separatingDistance = IMAGE_SIZE/10;
		ArrayList<PVector> points = PoissonDisk.poissonDiskSampling(seed, separatingDistance/3, drawZone-separatingDistance);
		for(PVector p : points) {
			p.x += (IMAGE_SIZE-drawZone+separatingDistance)/2;
			p.y += (IMAGE_SIZE-drawZone+separatingDistance)/2;
		}
		System.out.println("Found " + points.size() + " shapes to draw");
		
		float mainHue = rand.nextFloat();
		
		for(int shape = 0; shape < points.size(); shape++) {
			PoissonDisk.PVector p = points.get(shape);
			float hue = (mainHue+rand.nextFloat()*0.3f)%1;
			double angleDif = Math.PI*2/sections;
			for(double angle = 0; angle < Math.PI*2-angleDif; angle += angleDif) {
				double cos1 = Math.cos(angle);
				double cos2 = Math.cos(angle+angleDif);
				double sin1 = Math.sin(angle);
				double sin2 = Math.sin(angle+angleDif);
	
				double irregularity1 = (noise.eval(100+cos1, sin1, shape))+1;
				double irregularity2 = (noise.eval(100+cos2, sin2, shape))+1;
							
				
				for(int i = 0; i < circles; i++) {
					double x1 = cos1 * separatingDistance*0.5;
					double x2 = cos2 * separatingDistance*0.5;
					double y1 = sin1 * separatingDistance*0.5;
					double y2 = sin2 * separatingDistance*0.5;
					
					double noiseVal1 = (1+noise.eval(cos1, sin1, i*0.4, shape))/2;
					double noiseVal2 = (1+noise.eval(cos2, sin2, i*0.4, shape))/2;
					
					
					double colorNoise = noise.eval(cos1, sin1*2, i)/20;
					graphics.setColor(hsbaColor((float) (hue+colorNoise), 0.8f, 0.8f, 0.1f+0.7f/circles));
					
					graphics.fillPolygon(new int[] {
							p.x,
							p.x+(int) (x1 * noiseVal1*irregularity1),
							p.x+(int) (x2 * noiseVal2*irregularity2)
					}, new int[] {
							p.y,
							p.y+(int) (y1 * noiseVal1*irregularity1),
							p.y+(int) (y2 * noiseVal2*irregularity2)
					}, 3);
				}
			}
			
			if((shape+1)%5==0) {
				System.out.println("Drew " + (shape+1) + "/" + points.size() + " in " + (System.currentTimeMillis()-a) + "ms");
				a = System.currentTimeMillis();
			}
		}
		
		
		
		System.out.println(System.currentTimeMillis()-a + "ms to draw circle");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	private static Color hsbaColor(float h, float s, float b, float alpha) {
		Color c1 = Color.getHSBColor(h, s, b);
		return new Color(c1.getRed()/255f, c1.getGreen()/255f, c1.getBlue()/255f, alpha);
	}
}
