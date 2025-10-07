package MovableObject;

import GameManager.GameObject;

public abstract class MovableObject extends GameObject {
    protected int dx;
    protected int dy;

    public MovableObject(int x, int y, int width, int height){
        super(x, y, width, height);
    }
    public abstract void move();
}
