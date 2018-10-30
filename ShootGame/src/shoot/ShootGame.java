package shoot;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.color.*;
import java.awt.font.*;

public class ShootGame extends JPanel {
	public static final int WIDTH = 400;
	public static final int HEIGHT = 654;

	public static BufferedImage background;
	public static BufferedImage start;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	public static BufferedImage airplane;

	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	private int state = START;
	private Hero hero = new Hero();
	private FlyingObject[] flyings = {};
	private Bullet[] bullets = {};

	ShootGame() {
	
	}

	static {// initialize static resources->pictures
		try {
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public FlyingObject nextOne() {
		Random rand = new Random();
		int type = rand.nextInt(20);
		if (type < 4) {
			return new Bee();
		} else {
			return new Airplane();
		}
	}

	int flyEnteredIndex = 0;

	public void enterAction() {
		flyEnteredIndex++;
		if (flyEnteredIndex % 10 == 0) {
			FlyingObject one = nextOne();
			flyings = Arrays.copyOf(flyings, flyings.length + 1);
			flyings[flyings.length - 1] = one;
		}
	}

	public void stepAction() {
		hero.step();
		for (int i = 0; i < flyings.length; i++) {
			flyings[i].step();

		}
		for (int i = 0; i < bullets.length; i++) {
			bullets[i].step();
		}
	}

	int shootIndex = 0;

	public void shootAction() {
		// create bullets and add to array bullets
		shootIndex++;// increases with 1 every 10 milliseconds
		if (shootIndex % 30 == 0) {// every 300 mc
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
			// copy from the position 0 of bs to the position of bullets
		}
	}

	public void outOfBoundsAction() {
		int index = 0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (!f.outOfBounds()) {
				flyingLives[index] = f;
				index++;
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);
	}

	public void bangAction() {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			bang(b);
		}
	}

	int score = 0;

	public void bang(Bullet b) {
		int index = -1;// the hitten enemy's index
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (f.shootBy(b)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			FlyingObject one = flyings[index];
			if (one instanceof Enemy) {
				Enemy e = (Enemy) one;
				score += e.getScore();
			}
			if (one instanceof Reward) {
				Reward a = (Reward) one;
				int type = a.getType();
				switch (type) {
				case Reward.DOUBLE_FIRE:
					hero.addDoubleFire();
					break;
				case Reward.LIFE:
					hero.addLife();
					break;
				}
			}
			FlyingObject t = flyings[index];
			flyings[index] = flyings[flyings.length - 1];
			flyings[flyings.length - 1] = t;
			flyings = Arrays.copyOf(flyings, flyings.length - 1);
		}
	}

	public void checkGameOverAction() {
		if (isGameOver()) {
			state = GAME_OVER;
		}
	}

	public boolean isGameOver() {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (hero.hit(f)) {
				hero.substractLife();
				hero.clearDoubleFire();
				// 将被撞敌人与数组最后一个元素交换
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length - 1];
				flyings[flyings.length - 1] = t;
				flyings = Arrays.copyOf(flyings, flyings.length - 1);
			}
		}
		return hero.getLife() <= 0;
	}

	public void action() {
		MouseAdapter l = new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				// hero moves together with mouse
				if (state == RUNNING) {
					int x = e.getX();
					int y = e.getY();
					hero.moveTo(x, y);
				}
			}
			public void mouseClicked(MouseEvent e) {
				switch(state) {
				case START:
					state  = RUNNING;
					break;
				case GAME_OVER:
					score = 0;
					hero= new Hero();
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					state = START;
					break;
				}
			}
			public void mouseExited(MouseEvent e) {
				if(state==RUNNING) {
					state = PAUSE;
				}
			}
			public void mouseEntered(MouseEvent e) {
				if(state==PAUSE) {
					state=RUNNING;
				}
			}

				
		};
		this.addMouseListener(l);
		this.addMouseMotionListener(l);

		Timer timer = new Timer();
		int interval = 10;
		timer.schedule(new TimerTask() {
			public void run() {
				if (state == RUNNING) {

					enterAction();
					stepAction();
					shootAction();
					outOfBoundsAction();
					bangAction();
					checkGameOverAction();
				}
				repaint();
			}
		}, interval, interval);

	}

	// only called by system
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);
		paintHero(g);
		paintFlyingObjects(g);
		paintBullets(g);
		paintScoreAndLife(g);
		paintState(g);
	}

	public void paintHero(Graphics g) {
		g.drawImage(hero.image, hero.x, hero.y, null);
	}

	public void paintFlyingObjects(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			g.drawImage(f.image, f.x, f.y, null);
		}
	}

	public void paintBullets(Graphics g) {
		for (int i = 0; i < bullets.length; i++) {
			FlyingObject b = bullets[i];
			g.drawImage(b.image, b.x, b.y, null);
		}

	}

	public void paintScoreAndLife(Graphics g) {
		g.setColor(new Color(0xFF0000));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
		g.drawString("SCORE: " + score, 10, 25);
		g.drawString("LIFE: " + hero.getLife(), 10, 45);
	}

	public void paintState(Graphics g) {
		switch (state) {
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0, null);
			break;
		}
	
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly");
		ShootGame game = new ShootGame();
		frame.add(game);
		frame.setSize(WIDTH, HEIGHT);
		frame.setAlwaysOnTop(true);// zhongdian
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		game.action();
	}

}
