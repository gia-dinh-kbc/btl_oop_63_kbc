package MovableObject;

import GameManager.SpriteManager;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Ball extends MovableObject {
    private int speed = 2;
    private Ellipse2D.Double hitbox;
    private SpriteManager spriteManager;
    private BufferedImage ballSprite;

    public Ball(int x, int y, int radius, SpriteManager spriteManager) {
        super(x, y, radius, radius);
        this.spriteManager = spriteManager;
        this.ballSprite = spriteManager.getSprite("ball_red");
        hitbox = new Ellipse2D.Double(x, y, width, height);
        reset(x, y);
    }

    public Shape getHitbox() {
        return hitbox;
    }

    public void reset(int x, int y) {
        int[] angles = {-3, -2, -1, 1, 2, 3};
        this.x = x;
        this.y = y;
        Random random = new Random();
        this.dx = angles[new Random().nextInt(angles.length)];
        this.dy = -speed;
        hitbox.setFrame(x, y, width, height);
    }

    public void reverseX() {
        dx = -dx;
    }

    public void reverseY() {
        dy = -dy;
    }

    @Override
    public void move() {
        x += dx;
        y += dy;
        hitbox.setFrame(x, y, width, height);
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(ballSprite, x, y, width, height, null);
    }
}
