package art.pieces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import art.Main;
import art.Piece;

public class Circle implements Piece {

	/**
	 * Will generate an image, containg a set of black lines that come out of a circle, and that will break themselves and slightly
	 * deviate, while a colored line is behind and with a slighter deviation.
	 * <br> Using this method will also output the time needed to generate it, and some additional message about the values.
	 * <br><br>
	 * Some parts can be modified to obtain really nice graphics, exemples:
	 * <br> - amountLines, that will decide on how many lines should appear on the image. Increasing this number won't really affect
	 * the time needed to generate.
	 * <br> - lineLength, which will decide on the radius of the outer circle. To obtain the total radius just sum smallRadius and lineLength.
	 * <br> - breaks, will indicate how many breaks does a single line have. Increasing this number will result in some really jagged lines,
	 * while keeping it low will minimize the breaking effect.
	 * <br> - smallRadius, will indicate the radius of the inner white circle.
	 * <br> - hueVariation, will decide by how much should the hue be shifted from the base color.
	 * <br> - adjustVariation, will change how the colored line should follow the black one. The closer to 1 it is, the more this line is going
	 * to be similar to the black one. The closer it is to 0, the straigther it will be. Setting it to something higer than 1 will increase the
	 * jaggy effect even more, and setting it to -1 will make a mirror of the black line.
	 * @param seed The seed that will generate the pattern.
	 * @return The BufferedImage, that can then be displayed or saved. 
	 */
	@Override
	public BufferedImage generate(long seed) {
		System.out.println("Generating Circle...");
		long a = System.currentTimeMillis();
		int IMAGE_SIZE = Main.IMAGE_SIZE;
		Random rand = new Random(seed);
		BufferedImage image = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);
		graphics.setColor(Color.BLACK);
		graphics.drawString(seed + "", 5, 10);
		graphics.setStroke(new BasicStroke((float) (IMAGE_SIZE*0.5*0.003), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		System.out.println(System.currentTimeMillis()-a + "ms to generate basic image");
		a = System.currentTimeMillis();

		int amountLines = 35; // The amount of lines on the piece
		int lineLength = 1000; // The length of a line
		int breaks = 10; // The amount of breaks in a line
		int smallRadius = 400; // The radius of the inner circle
		float hue = rand.nextFloat(); // The basic hue, that will vary
		float hueVariation = 0.2f; // The max difference between two hues
		double adjustVariation = 0.6; // By how much should the colored line be adjusted
		double maxVariation = (Math.PI * 2) / (amountLines * 2.5);
		System.out.println(System.currentTimeMillis() - a + "ms to do math");
		a = System.currentTimeMillis();

		for(double i = 0; i < Math.PI * 2 - Math.PI / amountLines; i+= (Math.PI * 2) / amountLines) {
			int[] xBlack = new int[breaks];
			int[] yBlack = new int[breaks];
			int[] xColor = new int[breaks];
			int[] yColor = new int[breaks];
			xBlack[0] = IMAGE_SIZE/2 + (int) (smallRadius * Math.cos(i));
			yBlack[0] = IMAGE_SIZE/2 + (int) (smallRadius * Math.sin(i));
			xColor[0] = xBlack[0];
			yColor[0] = yBlack[0];
			for(int j = 1; j < breaks; j++) {
				int length = j * lineLength/breaks;
				double bLine = i;
				double cLine = i;
				double var = (rand.nextBoolean() ? 1 : -1) * (rand.nextDouble() * maxVariation);
				bLine += var;
				cLine += var * adjustVariation;
				xBlack[j] = IMAGE_SIZE/2 + (int) ((smallRadius + length) * Math.cos(bLine));
				yBlack[j] = IMAGE_SIZE/2 + (int) ((smallRadius + length) * Math.sin(bLine));
				xColor[j] = IMAGE_SIZE/2 + (int) ((smallRadius + length) * Math.cos(cLine));
				yColor[j] = IMAGE_SIZE/2 + (int) ((smallRadius + length) * Math.sin(cLine));
			}
			graphics.setColor(Color.getHSBColor(hue + rand.nextFloat()*hueVariation, 1, 1));
			graphics.drawPolyline(xColor, yColor, breaks);
			graphics.setColor(Color.BLACK);
			graphics.drawPolyline(xBlack, yBlack, breaks);
		}
		System.out.println(System.currentTimeMillis()-a + "ms to draw lines");
		
		System.out.println("Done.");
		System.out.println();
		
		return image;
	}
	

}
