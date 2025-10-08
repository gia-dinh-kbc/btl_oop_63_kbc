package Brick;

import GameManager.GameObject;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Color;

public class Brick extends GameObject {
    protected int hitPoints;
    protected int type;
    protected Shape hitbox;

    public Brick(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.hitbox = new java.awt.geom.Rectangle2D.Double(x, y, width, height);
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
        Color color = new Color(51,153,255);
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
