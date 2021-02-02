package art;

import java.awt.image.BufferedImage;

public interface Piece {
	BufferedImage generate(long seed);
}
