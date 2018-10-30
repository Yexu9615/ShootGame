package shoot;

import java.awt.image.BufferedImage;
import java.util.Random;

public class Hero extends FlyingObject {
	private int life;
	private int doubleFire;
	private BufferedImage[] images;
	private int index;

	public Hero() {
		image = ShootGame.hero0;
		width = image.getWidth();
		height = image.getHeight();
		x = 150;
		y = 400;
		life = 3;
		doubleFire = 0;
		images = new BufferedImage[] { ShootGame.hero0, ShootGame.hero1 };
		index = 0;
	}

	public Bullet[] shoot() {
		int xStep = this.width / 4;
		int yStep = 20;

		if (doubleFire > 0) {
			Bullet[] bs = new Bullet[2];
			bs[0] = new Bullet(this.x + 1 * xStep, this.y - yStep);
			bs[1] = new Bullet(this.x + 3 * xStep, this.y - yStep);
			doubleFire -= 2;
			return bs;
		} else {
			Bullet[] bs = new Bullet[1];
			bs[0] = new Bullet(this.x + 2 * xStep, this.y - yStep);

			return bs;
		}
	}

	public void step() {
		image = images[index++ / 10 % images.length];

		// image = images[index++/10%2];
		// index++;
		// int a = index/10;
		// int b=a%2;
		// image = images[b];
		/**
		 * 10M index = 1, a=0, b=0 20M index = 2, a=0, b=0 ... 100M index=10, a=1, b=1
		 */
	}

	public void moveTo(int x, int y) {
		this.x = x - this.width / 2;
		this.y = y - this.height / 2;

	}

	public boolean outOfBounds() {
		return false;
	}

	public void addLife() {
		life++;
	}

	public void substractLife() {
		life--;
	}

	public int getLife() {
		return life;
	}

	public void addDoubleFire() {
		doubleFire += 10;
	}
	public void clearDoubleFire() {
		doubleFire=0;
	}

	public boolean hit(FlyingObject obj) {
		int x1 = obj.x - this.width / 2;
		int x2 = obj.x + obj.width + this.width / 2;
		int y1 = obj.y - this.height / 2;
		int y2 = obj.y + obj.height + this.height / 2;
		int x = this.x + this.width / 2;
		int y = this.y + this.height / 2;

		return x > x1 && x < x2 && y > y1 && y < y2;
	}
}
