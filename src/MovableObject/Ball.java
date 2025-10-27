package MovableObject;

import GameManager.SpriteManager;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Ball extends MovableObject {
    private double speed = 5;
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
        this.x = x;
        this.y = y;
        hitbox.setFrame(x, y, width, height);
        this.dx = 0;
        this.dy = 0;
    }

    private void normalizeSpeed() {
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        if (currentSpeed > 0) {
            dx = (dx / currentSpeed) * speed;
            dy = (dy / currentSpeed) * speed;
        }
    }

    public void launch() {
        double angleRange = Math.toRadians(60);
        double angle = Math.toRadians(-90) + (Math.random() * angleRange - angleRange/2);

        this.dx = speed * Math.cos(angle);
        this.dy = speed * Math.sin(angle);

        normalizeSpeed();
    }

    public void reverseX() {
        dx = -dx;
        normalizeSpeed();
    }

    public void reverseY() {
        dy = -dy;
        normalizeSpeed();
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
        g.drawImage(ballSprite, (int) x, (int) y, width, height, null);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}