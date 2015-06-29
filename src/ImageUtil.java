import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

	public Image fetchResizedImage(String fileName, int width, int length) throws IOException {
		// length = x
		// width = y
		return fetchImage(fileName).getScaledInstance(width, length, 0);

	}

	public BufferedImage fetchImage(String fileName) throws IOException {
		return ImageIO.read(this.getClass().getResourceAsStream(fileName));

	}

	public BufferedImage[] makeAnimationStrip(String path, int size) {

		try {

			BufferedImage spriteSheet = fetchImage(path);

			BufferedImage[] finishedStrip = new BufferedImage[spriteSheet.getHeight() / size];

			for (int i = 0; i < spriteSheet.getHeight() / size; i++) {

				finishedStrip[i] = spriteSheet.getSubimage(0, i * size, size, size);

			}

			return finishedStrip;

		} catch (IOException e) {

			System.err.println("ERROR: Could not get image " + path);
			e.printStackTrace();
			return null;

		}
	}

}
