package Brick;


import java.awt.*;

public class NormalBrick extends Brick {

    public NormalBrick(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.type = 1;
        this.hitPoints = 1;
    }
    @Override
    public void render(java.awt.Graphics g) {
        Color color = new Color(222,123,242);
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}
