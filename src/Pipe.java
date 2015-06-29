import java.awt.Graphics;
import java.awt.Rectangle;

public class Pipe extends Rectangle {

	private static final long serialVersionUID = 1L;

	boolean floorPipe;

	int pipeY;

	public Pipe(int x, int height, boolean floorPipe) {

		this.x = x;
		this.height = height;
		this.floorPipe = floorPipe;

		if (floorPipe) {

			pipeY = PotatoBirdGame.game.HEIGHT - height - 230;
			y = PotatoBirdGame.game.HEIGHT - height - 230;

		} else {
			pipeY = -(800 - height);
			y = 0;
		}

		this.width = 160;

	}

	void draw(Graphics g) {

		if (floorPipe) {

			g.drawImage(PotatoBirdGame.game.pipe, x, pipeY, null);
		} else {

			g.drawImage(PotatoBirdGame.game.pipe_turned, x, pipeY, null);

		}
	}
}