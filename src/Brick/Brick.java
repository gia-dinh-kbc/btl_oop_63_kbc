package Brick;

import GameManager.GameObject;

import java.awt.Color;
import java.awt.Graphics;

public class Brick extends GameObject {
    protected int hitPoints;
    protected int type;

    public Brick(int x, int y, int width, int height) {
        super(x, y, width, height);
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

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }
}
