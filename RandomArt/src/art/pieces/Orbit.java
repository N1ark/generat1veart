package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import art.Main;
import art.Piece;

public class Orbit implements Piece{
	/**
	 * Will return an image with a large circle in the middle, that is orbited by other circles, that are themselves orbitted.
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - orbits, will decide on how many rings of planets there should be.
	 * <br> - planets, the amount of planets around one other planet. The total amount of planets is equal to Math.pow(planets, orbits),
	 * so increasing orbits will exponentially increase the amount of planets and the time required to do the calculations. Planets will also
	 * increase the calculations.
	 * <br> - size, the size of the initial planet, from which the size of all orbits result
	 * <br> - distance, the distance at which each planet is. This distance will decrease exponentially after each ring.
	 * <br> - decSize, it will determine by how much should the size and distance be reduced through each ring.
	 * <br> - You can also change the sorting algorithm, to either display the planets from larger to smaller, or the opposite.
	 * <br> - Setting all the orbits to start from the same angle by setting dist to 0 and then increasing them by minSplit without
	 * any randomness will output a nice trianle of planets.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Orbit...");
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
		
		
		float hue = rand.nextFloat();
		float hueVar = (rand.nextBoolean() ? 1 : -1) * 0.045f;
		int orbits = 5;
		int planets = 8;
		int size = 400;
		int distance = 1000;
		double decSize = 0.7;
		double minSplit = 2*Math.PI/planets; // = (Math.PI*2)/(planets*2) Faudrait faire ca, on simplifie
		int middle = IMAGE_SIZE/2;
		System.out.println(System.currentTimeMillis()-a + "ms to do math");
		a = System.currentTimeMillis();
		
		graphics.setColor(Color.getHSBColor(hue, 1, 1));
		graphics.fillOval(middle-size/2, middle-size/2, size, size);
		ArrayList<Planet> list = new ArrayList<>();
		ArrayList<Planet> all = new ArrayList<>();
		list.add(new Planet(middle, middle, 0));
		int gen = 0;
		while(!list.isEmpty()) {
			gen++;
			if(gen > orbits)
				break;
			ArrayList<Planet> nextGen = new ArrayList<>();
			for(Planet pl : list) {
				double dist = rand.nextDouble();
				for(int i = 0; i < planets; i++) {
					double angle = dist + minSplit/2 + rand.nextDouble()*minSplit;
					int x = pl.x + (int) (Math.cos(angle) * distance * Math.pow(decSize, gen));
					int y = pl.y + (int) (Math.sin(angle) * distance * Math.pow(decSize, gen));
					nextGen.add(new Planet(x, y, gen));
					dist = angle;
				}
			}
			all.addAll(list);
			list.clear();
			list.addAll(nextGen);
		}
		System.out.println("Size: " + list.size()); // = planets ^ orbits
		Collections.sort(all, Comparator.comparing(pl -> -pl.gen));
		for(Planet pl : all) {
			graphics.setColor(Color.getHSBColor(hue + pl.gen*(hueVar/orbits), 1, 1));
			int radius = (int) (size*Math.pow(decSize, pl.gen));
			graphics.fillOval(pl.x - radius/2, pl.y - radius/2, radius, radius);
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw dots");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	static class Planet {
		int x;
		int y;
		int gen;
		Planet(int x, int y, int gen){
			this.x = x;
			this.y = y;
			this.gen = gen;
		}
	}
}
