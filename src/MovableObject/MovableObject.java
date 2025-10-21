package MovableObject;

import GameManager.GameObject;

public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public MovableObject(int x, int y, int width, int height){
        super(x, y, width, height);
    }
    public abstract void move();
}
