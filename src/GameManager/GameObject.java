package GameManager;

public abstract class GameObject {
    private double x;
    private double y;
    private double width;
    private double height;

    public abstract void update();
    public abstract void render();
}
