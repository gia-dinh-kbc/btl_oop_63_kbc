package MovableObject;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class Ball extends MovableObject {
    private final int speed = 5;
    private Shape hitbox;
    public Ball(int x, int y, int radius) {
        super(x, y, radius, radius);
        reset(x, y);
        hitbox = new java.awt.geom.Ellipse2D.Double(x, y, width, height);
    }
    public Shape getHitbox() {
        return hitbox;
    }
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        Random random = new Random();
        boolean r = random.nextBoolean();
        this.dx = (r) ? speed : -speed;
        this.dy = -speed;
        hitbox = new java.awt.geom.Ellipse2D.Double(x, y, width, height);
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
        hitbox = new java.awt.geom.Ellipse2D.Double(x, y, width, height);
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }
}
