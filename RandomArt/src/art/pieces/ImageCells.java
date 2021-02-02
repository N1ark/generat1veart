package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import art.Main;
import art.Piece;

public class ImageCells implements Piece {
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
	 * @throws IOException if the mask file isn't found.
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Cells...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		BufferedImage mask = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		mask.getGraphics().setColor(Color.WHITE);
		mask.getGraphics().fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.003), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		graphics.drawString(seed + "", 5, 10);
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();
		
		int squareSize = (int) (IMAGE_SIZE/Math.sqrt(2)); // The square should have the same area as the white part
		int squareStart = (IMAGE_SIZE-squareSize)/2;
		int size = squareSize/110;
		int maxTries = 5_000;
		boolean full = false;
		int width  = (int) (IMAGE_SIZE*0.5*0.003);
		float hue = rand.nextFloat();
		try {
			System.out.println(Main.class.getClassLoader().getResourceAsStream("data/mask.png") == null);
			mask.getGraphics().drawImage(
					ImageIO.read(Main.class.getClassLoader().getResourceAsStream("data/mask.png")), 
					squareStart, squareStart, squareSize, squareSize, new JPanel());
		} catch (IOException e) {
			e.printStackTrace();
			return image;
		}
		
		ArrayList<Cell> cells = new ArrayList<>();
		int gen = 0;
		int tries = 0;
		adding: while(tries < maxTries) {
			gen++;
			Cell cell = new Cell(
					(int) (squareStart + rand.nextFloat() * squareSize), 
					(int) (squareStart + rand.nextFloat() * squareSize), 
					size
					);
			int[] rgb = getARGB(mask, cell.x, cell.y);
			if(
					cell.x - cell.size/2 < squareStart ||
					cell.x + cell.size/2 > squareStart + squareSize ||
					cell.y - cell.size/2 < squareStart ||
					cell.y + cell.size/2 > squareStart + squareSize ||
					( // Is White-ish
						rgb[1] > 240 &&
						rgb[2] > 240 &&
						rgb[3] > 240
					)
			) {
				tries ++;
				continue adding;
			}
			for(Cell loop : cells) {
				if(
						
						loop.dist(cell) < (loop.size + cell.size)/2
						) {
					tries ++;
					continue adding;
				}
			}
			cells.add(cell);
			tries = 0;
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
	
	static int[] getARGB(BufferedImage i, int x, int y) {
		int d = i.getRGB(x, y);
		int a = (d & 0xff000000) >> 24;
		int r = (d & 0x00ff0000) >> 16;
		int g = (d & 0x0000ff00) >> 8;
		int b =  d & 0x000000ff;
		return new int[] {a, r, g, b};
	}
}
