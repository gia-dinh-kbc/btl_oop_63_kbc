package Brick;

public class NormalBrick extends Brick {

    public NormalBrick(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.type = 1;
        this.hitPoints=1;
    }

    @Override
    public void takeHit() {

    }
}
