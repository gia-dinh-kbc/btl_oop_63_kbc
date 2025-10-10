package MovableObject;

import GameManager.GameObject;

public abstract class MovableObject extends GameObject {
    protected int dx;
    protected int dy;

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public MovableObject(int x, int y, int width, int height){
        super(x, y, width, height);
    }
    public abstract void move();
}
