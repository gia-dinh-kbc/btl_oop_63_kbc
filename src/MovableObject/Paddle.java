package MovableObject;

import PowerUp.PowerUp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Paddle extends MovableObject {
    private int speed = 5;
    private PowerUp currentPowerUp;
    private Rectangle2D.Double hitbox;

    public Paddle(int x, int y, int width, int height) {
        super(x, y, width, height);
        hitbox = new Rectangle2D.Double(x, y, width, height);
    }

    public Shape getHitbox() {
        return hitbox;
    }

    public void moveLeft() {
        dx = -speed;
    }

    public void moveRight() {
        dx = speed;
    }

    public void stop() {
        dx = 0;
    }

    public void applyPowerUp() {

    }

    @Override
    public void move() {
        x += dx;

        if (x < 0) {
            x = 0;
        }

        if (x + width > 500) {
            x = 500 - width;
        }

        hitbox.setRect(x, y, width, height);
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}
