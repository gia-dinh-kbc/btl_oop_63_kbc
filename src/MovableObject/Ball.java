package MovableObject;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class Ball extends MovableObject {
    private int speed = 5;

    public Ball(int x, int y, int radius) {
        super(x, y, radius, radius);
        reset(x, y);
    }

    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        Random random = new Random();
        boolean r = random.nextBoolean();
        this.dx = (r) ? speed : -speed;
        this.dy = -speed;
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
