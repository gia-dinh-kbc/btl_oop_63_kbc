package Brick;

import GameManager.GameObject;
import GameManager.SpriteManager;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Brick extends GameObject {
    protected int hitPoints;
    protected int type;
    protected Rectangle2D.Double hitbox;
    protected SpriteManager spriteManager;
    protected BufferedImage brickSprite;

    public Brick(int x, int y, int width, int height, SpriteManager spriteManager) {
        super(x, y, width, height);
        this.spriteManager = spriteManager;
        hitbox = new Rectangle2D.Double(x, y, width, height);
    }

    public void takeHit() {
        hitPoints--;
    }

    public boolean isDestroyed() {
        if (hitPoints <= 0) {
            return true;
        }
        return false;
    }

    public Shape getHitbox() {
        return hitbox;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(brickSprite, x, y, width, height, null);
    }
}
