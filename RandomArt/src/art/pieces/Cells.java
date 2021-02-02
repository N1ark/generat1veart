package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import art.Main;
import art.Piece;
import art.util.PoissonDisk;
import art.util.PoissonDisk.PVector;

public class Cells implements Piece{
	/**
	 * Will return an image with a square that has the same area than the image without said square, containing randomly distributed
	 * points using a PoissonDisk sampling. 
	 * <br> Using this method will also output the time it takes to create each step of the image.
	 * <br> <br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - minDistance will change the minimal distance between each points. Reducing it will increase the amount of points, but
	 * the generation will take a lot more time.
	 * <br> - the sice of closer[], that will decide about how many neighbours should be found for a certain point. As far as I know
	 * changing this value won't really change a lot the time required to generate, but obviously the bigger the size, the longer to
	 * make.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Cells...");
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
		
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // Le carre aura une aire egale a la partie blanche
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int minDistance = (int) (IMAGE_SIZE*0.5*0.02); // Distance minimale entre 2 points
		System.out.println("The minimal distance between two points is " + minDistance + " pixels");
		
		List<PVector> poisson = PoissonDisk.poissonDiskSampling(seed, minDistance, squareSize);
		System.out.println("There are " + poisson.size() + " dots");
		System.out.println(System.currentTimeMillis()-a + "ms to do math and generate points");
		a = System.currentTimeMillis();
		
		for(PVector dot : poisson) {
			PVector[] closer = new PVector[1]; // Nombre de voisins avec lesquels se relier 
			for(int i = 0; i < closer.length; i++) {
				double dist = Integer.MAX_VALUE;
				for(PVector check : poisson) {
					if(check != dot && !Main.contains(closer, check)) {
						double dist2 = check.dist(dot);
						if(dist2 < dist) {
							dist = dist2;
							closer[i] = check;
						}
					}
				}
			}
			int startx = squareStart + dot.x;
			int starty = squareStart + dot.y;
			for(PVector neighbour : closer) {
				int endx = squareStart + neighbour.x;
				int endy = squareStart + neighbour.y;
				graphics.drawLine(startx, starty, endx, endy);
			}
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines.");
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
}
