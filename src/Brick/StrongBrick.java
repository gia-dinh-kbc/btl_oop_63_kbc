package Brick;

public class StrongBrick extends Brick {
    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.type = 2;
        this.hitPoints = 2;
    }

    @Override
    public void takeHit() {

    }
}
