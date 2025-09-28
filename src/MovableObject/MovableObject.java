package MovableObject;

import GameManager.GameObject;

public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;

    public abstract void move();
}
