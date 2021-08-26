package art;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import javax.imageio.ImageIO;

import art.pieces.SexyNoise;

public class Main {

	public static final int IMAGE_SIZE = 1024;//4096*4;
	
	public static void main(String[] args) throws IOException {
		long seed = new Random().nextLong();
		Calendar date = Calendar.getInstance();
		String fileName = date.get(Calendar.YEAR) + "." +
				date.get(Calendar.MONTH) + "." +
				date.get(Calendar.DAY_OF_MONTH) + " " + 
				date.get(Calendar.HOUR_OF_DAY) + "." + 
				date.get(Calendar.MINUTE) + "." + 
				date.get(Calendar.SECOND) +
				".png";
		
		
		// put here the piece to generate
		Piece piece = new SexyNoise();
		
		BufferedImage result = piece.generate(seed);
		
		System.out.println(new File("generated", fileName).getAbsolutePath());
		ImageIO.write(result, "png", new File("generated", fileName));
	}
	
	public static <T> boolean contains(T[] array, T search) {
		for(T loop : array)
			if(search.equals(loop))
				return true;
		return false;
	}
}
