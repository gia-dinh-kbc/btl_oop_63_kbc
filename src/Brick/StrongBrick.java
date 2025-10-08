package Brick;

import java.awt.*;

public class StrongBrick extends Brick {
    public StrongBrick(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.type = 2;
        this.hitPoints = 2;
    }

    @Override
    public void render(java.awt.Graphics g) {
        if (hitPoints == 2) {
            Color color = new Color(101,55,110);
            g.setColor(color);
        } else if (hitPoints == 1) {
            Color color = new Color(222,123,242);
            g.setColor(color);
        }
        g.fillRect(x, y, width, height);
    }
}
