package Brick;

public class BrickFactory {
    public static final int NORM_BRICK = 1;
    public static final int STRONG_BRICK = 2;
    public static Brick createBrick(int type, int x, int y, int width, int height) {
        switch (type) {
            case NORM_BRICK:
                return new NormalBrick(x, y, width, height);
            case STRONG_BRICK:
                return new StrongBrick(x, y, width, height);
            default:
                throw new IllegalArgumentException("Invalid brick type: " + type);
        }
    }
}
