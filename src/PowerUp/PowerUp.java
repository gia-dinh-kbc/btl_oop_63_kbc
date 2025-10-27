package PowerUp;

import GameManager.GameObject;
import MovableObject.Paddle;

import java.awt.Color;
import java.awt.Graphics;

import GameManager.GameManager;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Lớp PowerUp đại diện cho các vật phẩm rơi ra từ Brick.
 * - Rơi xuống theo trục Y (gravity)
 * - Khi Paddle bắt được → gọi applyEffect(GameManager)
 */
public abstract class PowerUp extends GameObject {
    protected double fallSpeed = 3; // tốc độ rơi
    protected Rectangle2D.Double hitbox; // vùng va chạm
    protected BufferedImage sprite; // hình hiển thị vật phẩm

    public PowerUp(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height);
        this.sprite = sprite;
        this.hitbox = new Rectangle2D.Double(x, y, width, height);
    }

    public abstract void applyEffect(GameManager game);

    @Override
    public void update() {
        y += fallSpeed;
        hitbox.setRect(x, y, width, height);
    }

    @Override
    public void render(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, (int) x, (int) y, width, height, null);
        } else {
            // Nếu chưa có hình, vẽ tạm bằng màu
            g.setColor(Color.YELLOW);
            g.fillRect((int) x, (int) y, width, height);
        }
    }

    public Rectangle2D.Double getHitbox() {
        return hitbox;
    }

    public double getY() {
        return y;
    }
}
