package art.pieces;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import art.Main;
import art.Piece;

public class FillCells implements Piece{
	/**
	 * Will generate an image, containg a lot of circles that will take as much place as possible, with a decreasing size,
	 * all over an invisible square, and with a random color that is more or less around the same hue.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - size, the begginning size for the first generation of circles.
	 * <br> - maxTries, the amount of successive failures needed before a new generation is started. The higher
	 * this number is, the more filled will the space be, and the longer it will take to generate the piece.
	 * <br> - minSize, the minimal size of a circle. This value will make sure the last circle are barely visible.
	 * <br> - full, if the inside of the circle should be white or not.
	 * <br> - width, if full is true, it will decide on the width of the colored circle we get to see.
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
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int size = squareSize/40;
		int maxTries = 10_000;
		int minSize = IMAGE_SIZE/700;
		boolean full = false;
		int width  = (int) (IMAGE_SIZE*0.5*0.008);
		float hue = rand.nextFloat();
		
		ArrayList<Cell> cells = new ArrayList<>();
		int gen = 0;
		while(size > minSize) {
			int tries = 0;
			int added = 0;
			adding: while(tries < maxTries) {
				gen++;
				Cell cell = new Cell(
						(int) (squareStart + rand.nextFloat() * squareSize), 
						(int) (squareStart + rand.nextFloat() * squareSize), 
						size
				);
				for(Cell loop : cells) {
					if(
							cell.x - cell.size/2 < squareStart ||
							cell.x + cell.size/2 > squareStart + squareSize ||
							cell.y - cell.size/2 < squareStart ||
							cell.y + cell.size/2 > squareStart + squareSize ||
							loop.dist(cell) < (loop.size + cell.size)/2
					) {
						tries ++;
						continue adding;
					}
				}
				cells.add(cell);
				tries = 0;
				added ++;
			}
			System.out.println("Added " + added + " cells, next generation");
			size -= size * 0.02;
		}
		System.out.println("Total " + cells.size() + " cells");
		DecimalFormat df = new DecimalFormat("#,###.00");
		double ratio = gen * 1.0 / cells.size();
		System.out.println("Did " + df.format(gen) + " tries, for average " + df.format(ratio) + " tries per cell");
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();
		
		for(Cell c : cells) {
			graphics.setColor(Color.getHSBColor(hue + (rand.nextBoolean()?1:-1) * rand.nextFloat() * 0.15f, 1, 1));
			graphics.fillOval(c.x-c.size/2, c.y-c.size/2, c.size, c.size);
			if(!full && c.size > width) {
				graphics.setColor(Color.white);
				graphics.fillOval(c.x-c.size/2+width/2, c.y-c.size/2+width/2, c.size-width, c.size-width);
			}
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	
	static class Cell {
		int x, y, size;
		Cell(int x, int y, int size){
			this.x = x;
			this.y = y;
			this.size = size;
		}
		double dist(Cell v) {
			return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2));
		}
	}
}
