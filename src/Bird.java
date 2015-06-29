import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Bird extends Rectangle {

	private static final long serialVersionUID = 1L;

	/*
	 * NOTES: - sprites must be square
	 */

	// sprite values
	// 0 = -45 wings middle
	// 1 = -45 wings up (nope)
	// 1 = wings down
	// 2= middle middle
	// 3 = middle down
	// 4 = 45 degrees middle
	// 5 = 45 degrees down
	// 6 = face down
	// 7 = face down wings up?

	private Image[] sprites;
	private int birdSize;
	private int currSprite = 2;
	private int jumpHeight = -8;
	// dub
	private double fallSpeed = 0;

	public Bird(int x, int y, BufferedImage[] sprites, int birdSize, int flapSpeed, PotatoBirdGame game) {

		this.birdSize = birdSize;

		this.x = x;
		this.y = y;

		this.sprites = new Image[sprites.length];

		for (int i = 0; i < sprites.length; i++) {
			this.sprites[i] = sprites[i].getScaledInstance(birdSize, birdSize, 0);
		}

		this.width = birdSize;

		this.height = birdSize;
	}

	void draw(Graphics g) {

		g.drawImage(sprites[currSprite], x, y, null);

	}

	void floorKill() {

		y = PotatoBirdGame.game.floorY - birdSize / 2;

	}

	public void restart() {
		fallSpeed = 0;
		this.y = 0;

	}

	void tilt(int tilt) {

		// 0 = UP
		// 2 = MIDDLE
		// 4 = DOWN
		// 6 = vertical

		currSprite = tilt;

	}

	public void jump() {
		if (!(this.y < 0)) {
			fallSpeed = jumpHeight;

		}
	}

	void flap() {

		currSprite ^= 1;
	}

	public void gravityAlreadyDead() {

		this.y += fallSpeed;
		fallSpeed += 0.4;

		if (fallSpeed > 8) {
			tilt(4);

		} else if (fallSpeed < -5) {
			tilt(0);
			flap();

		} else {
			tilt(2);

		}

	}

	public void gravity() {

		if (this.y + fallSpeed >= PotatoBirdGame.game.floorY - birdSize) {

			PotatoBirdGame.game.gameOver();

			floorKill();

			return;

		} else {
			y += fallSpeed;
			fallSpeed += 0.4;

		}

		if (fallSpeed > 8) {
			tilt(4);

		} else if (fallSpeed < -5) {
			tilt(0);
			flap();

		} else {
			tilt(2);

		}

	}

	public boolean isOnFloor() {
		return (this.y < (PotatoBirdGame.game.floorY - this.birdSize / 2));

	}
}