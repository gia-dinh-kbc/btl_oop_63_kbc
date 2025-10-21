package MovableObject;

import GameManager.GameManager;
import GameManager.SpriteManager;
import PowerUp.PowerUp;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Paddle extends MovableObject {
    private double speed = 5;
    private PowerUp currentPowerUp;
    private Rectangle2D.Double hitbox;
    private SpriteManager spriteManager;
    private BufferedImage paddleSprite;

    public Paddle(int x, int y, int width, int height, SpriteManager spriteManager) {
        super(x, y, width, height);
        this.spriteManager = spriteManager;
        this.paddleSprite = spriteManager.getSprite("paddle_orange");
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
        x += (int) dx;

        if (x < 0) {
            x = 0;
        }

        if (x + width > GameManager.getWindowWidth()) {
            x = GameManager.getWindowWidth() - width;
        }

        hitbox.setRect(x, y, width, height);
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(paddleSprite, (int) x, (int) y, width, height, null);
    }
}
