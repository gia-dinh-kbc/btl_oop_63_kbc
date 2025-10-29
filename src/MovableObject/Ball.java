package MovableObject;

import GameManager.SpriteManager;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Ball extends MovableObject {
    private double speed = 3;
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

    public void launch() {
        double angle = Math.toRadians(60);
        dx = speed * Math.cos(angle);
        dy = -speed * Math.sin(angle);
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

    public void increaseSpeed() {
        this.speed = 4;

        double currentSpeed = Math.sqrt(dx * dx + dy * dy);  // Tính tốc độ hiện tại của bóng
        double speedRatio = currentSpeed / speed;  // Tính tỷ lệ giữa tốc độ hiện tại và tốc độ mới

        dx = dx / speedRatio;
        dy = dy / speedRatio;
    }

    public void decreaseSpeed() {
        this.speed = 2;

        double currentSpeed = Math.sqrt(dx * dx + dy * dy);  // Tính tốc độ hiện tại của bóng
        double speedRatio = currentSpeed / speed;  // Tính tỷ lệ giữa tốc độ hiện tại và tốc độ mới

        dx = dx / speedRatio;
        dy = dy / speedRatio;
    }

    public void resetSpeed() {
        this.speed = 3;
        double currentSpeed = Math.sqrt(dx * dx + dy * dy);
        double speedRatio = currentSpeed / speed;

        dx = dx / speedRatio;
        dy = dy / speedRatio;
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
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