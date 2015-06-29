import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class PotatoBirdGame extends Canvas implements Runnable {

	// TODO: add longer flash

	private static final long serialVersionUID = 1L;

	public static PotatoBirdGame game;

	// game vars

	Bird bird;

	ArrayList<Pipe> pipes = new ArrayList<Pipe>();

	Random rand = new Random();

	final int WIDTH, HEIGHT;

	int score = 0;

	String best = "0";

	String scoreString;

	String numbersString = "0123456789";

	Rectangle playAgainButton = new Rectangle(255, 573, 305, 230);

	// game tweaking

	int flashDuration = 6;

	boolean running = true;

	boolean gameOver = false;

	boolean beginningScreen = true;

	int jumpHeight = -8;

	int backgroundWidth = 990;

	int pipe_spacer = 500;

	int pipeGenRate = 1500;

	int numberSize = 70;

	int numberSize2 = 150;

	int score_spacing_2 = 100;

	int score_spacing = 35;

	int beginningWorldSpeed = 2;

	int regularWorldSpeed = 5;

	int worldSpeed = beginningWorldSpeed;

	// double buffering

	Graphics dbg;

	Image dbi;

	// background / pictures

	ImageUtil iu = new ImageUtil();

	Image sky, grass, grass_overlap, pipe, pipe_turned, game_screen, get_ready;

	Image[] numbers16, numbers16Smaller;

	int[] backgroundX = new int[2];

	int floorY;

	int flash = 0;

	boolean displayGameScreen;

	public PotatoBirdGame(int WIDTH, int HEIGHT) {

		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;

		this.floorY = HEIGHT - 200;

		importImages();

		this.setSize(WIDTH, HEIGHT);

		this.addMouseListener(new MA());

		this.requestFocus();

		bird = new Bird(30, 10, iu.makeAnimationStrip("birdsprites.png", 16), 100, 200, this);

		game = this;

	}

	public void gameOver() {

		gameOver = true;

		scoreString = String.valueOf(score / 2);

		if (Integer.valueOf(best) < (score / 2)) {

			best = scoreString;

		}
	}

	private void updatePipes() {
		for (int i = 0; i < pipes.size(); i++) {
			if ((pipes.get(i).x + 160) < 0) {

				pipes.remove(i);
				score++;

			}
		}

		// checks to see if bird crashed into pipes

		for (int i = 0; i < pipes.size(); i++) {
			if (bird.intersects(pipes.get(i))) {
				gameOver();

			}
		}
	}

	@Override
	public void paint(Graphics g) {

		g.drawImage(grass, backgroundX[0], floorY, null);
		g.drawImage(grass, backgroundX[1], floorY, null);

		g.drawImage(sky, backgroundX[0], 0, null);
		g.drawImage(sky, backgroundX[1], 0, null);

		for (int i = 0; i < pipes.size(); i++) {

			pipes.get(i).draw(g);

		}

		g.drawImage(grass_overlap, backgroundX[0], floorY + 20, null);
		g.drawImage(grass_overlap, backgroundX[1], floorY + 20, null);

		bird.draw(g);

		if (!gameOver && !beginningScreen) {

			String scoreS = String.valueOf(score / 2);

			for (int i = 0; i < scoreS.length(); i++) {

				g.drawImage(numbers16[numbersString.indexOf(scoreS.charAt(i))], 320 + (i * score_spacing_2), 150, null);

			}
		}

		if (flash != 0) {

			g.setColor(Color.WHITE);

			g.fillRect(0, 0, WIDTH, HEIGHT);

			flash--;

		}

		if (displayGameScreen) {

			// specific coords:
			// - where text goes
			g.drawImage(game_screen, 8, 0, null);

			for (int i = 0; i < scoreString.length(); i++) {
				g.drawImage(numbers16Smaller[numbersString.indexOf(scoreString.charAt(i))], 375 + (i * score_spacing), 190, null);
			}

			for (int i = 0; i < best.length(); i++) {
				g.drawImage(numbers16Smaller[numbersString.indexOf(best.charAt(i))], 375 + (i * score_spacing), 400, null);
			}

		}

		if (beginningScreen) {

			g.drawImage(get_ready, 8, 0, null);

		}

	}

	public void gameRestart() {

		score = 0;

		bird.restart();

		worldSpeed = beginningWorldSpeed;

		pipes.clear();

		gameOver = false;

		displayGameScreen = false;

		beginningScreen = true;

	}

	public void click(Point point) {

		if (playAgainButton.contains(point) && displayGameScreen) {

			gameRestart();

		} else if (beginningScreen) {

			beginningScreen = false;

			worldSpeed = regularWorldSpeed;

			new Thread(new pipeGenThread()).start();

		} else if (!gameOver) {

			bird.jump();

		}
	}

	public class beginningThread implements Runnable {

		@Override
		public void run() {
			while (beginningScreen) {

				bird.flap();

				try {

					Thread.sleep(400);

				} catch (InterruptedException e) {

					e.printStackTrace();

				}

			}
		}
	}

	public void importImages() {

		// loads background images
		try {

			sky = iu.fetchResizedImage("sky.png", backgroundWidth, 700);

			grass = iu.fetchResizedImage("grass.png", backgroundWidth, 250);
			grass_overlap = iu.fetchResizedImage("grass_overlap.png", backgroundWidth, 230);

			backgroundX[0] = 0;
			backgroundX[1] = backgroundWidth;

		} catch (IOException e) {

			e.printStackTrace();

		}

		// loads pipe images
		try {

			pipe = iu.fetchResizedImage("pipe.png", 160, 800);

			pipe_turned = iu.fetchResizedImage("pipe_upside_down.png", 160, 800);

		} catch (IOException e) {

			e.printStackTrace();

		}

		// loads misc game art
		try {

			game_screen = iu.fetchResizedImage("score-best.png", 800, 860);

			get_ready = iu.fetchResizedImage("getready.png", 800, 860);

			BufferedImage numberSheet16 = iu.fetchImage("numbers_16.png");

			numbers16 = new Image[10];

			numbers16Smaller = new Image[10];

			for (int i = 0; i < 10; i++) {

				numbers16Smaller[i] = numberSheet16.getSubimage(i * 16, 0, 16, 16).getScaledInstance(numberSize, numberSize, 0);

			}

			for (int i = 0; i < 10; i++) {

				numbers16[i] = numberSheet16.getSubimage(i * 16, 0, 16, 16).getScaledInstance(numberSize2, numberSize2, 0);

			}

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	@Override
	public void run() {

		while (running) {

			if (gameOver) {

				new Thread(new skiddingThread()).start();

				while (gameOver) {

					repaint();
					moveBackground();

					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (beginningScreen) {

				new Thread(new beginningThread()).start();

				while (beginningScreen) {

					moveBackground();

					repaint();

					try {
						Thread.sleep(17);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
			moveBackground();
			updatePipes();
			repaint();
			bird.gravity();

			try {
				Thread.sleep(19);

			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}
	}

	// note: death by pipe needs gameOver = true;

	public void start() {

		new Thread(this).start();

	}

	private class pipeGenThread implements Runnable {

		@Override
		public void run() {

			while (!gameOver) {

				int len = rand.nextInt(300) + 100;

				pipes.add(new Pipe(WIDTH, (HEIGHT - len) - pipe_spacer, true));

				pipes.add(new Pipe(WIDTH, len, false));

				try {

					Thread.sleep(pipeGenRate);

				} catch (InterruptedException e) {

					e.printStackTrace();

				}
			}
		}
	}

	private class skiddingThread implements Runnable {

		@Override
		public void run() {

			flash = flashDuration;

			while (bird.isOnFloor()) {

				bird.gravityAlreadyDead();

				try {

					Thread.sleep(17);

				} catch (InterruptedException e) {

					e.printStackTrace();

				}

			}

			worldSpeed--;

			try {

				Thread.sleep(50);

			} catch (InterruptedException e) {

				e.printStackTrace();

			}

			bird.tilt(2);

			while (worldSpeed != 0) {

				worldSpeed--;

				try {

					Thread.sleep(400);

				} catch (InterruptedException e) {

					e.printStackTrace();

				}
			}

			displayGameScreen = true;
		}
	}

	public void movePipes() {
		for (int i = 0; i < pipes.size(); i++) {
			pipes.get(i).x -= worldSpeed;

		}
	}

	public void moveBackground() {

		backgroundX[0] -= worldSpeed;
		backgroundX[1] -= worldSpeed;

		if (backgroundX[0] <= -backgroundWidth) {
			backgroundX[0] = backgroundX[1] + backgroundWidth;

		} else if (backgroundX[1] <= -backgroundWidth) {
			backgroundX[1] = backgroundX[0] + backgroundWidth;

		}

		movePipes();

	}

	public static void main(String[] args) {

		PotatoBirdGame p = new PotatoBirdGame(840, 900);
		p.start();

		JFrame f = new JFrame("Le Potato Bird");
		f.add(p);
		// f.pack();
		f.setSize(840, 900);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

		f.requestFocus();

	}

	public class MA extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			click(e.getPoint());

		}
	}

	@Override
	public void update(Graphics g) {

		dbi = this.createImage(WIDTH, HEIGHT);

		dbg = dbi.getGraphics();

		dbg.setColor(getBackground());

		dbg.fillRect(0, 0, WIDTH + 10, HEIGHT + 10);

		paint(dbg);

		g.drawImage(dbi, 0, 0, this);

	}
}
