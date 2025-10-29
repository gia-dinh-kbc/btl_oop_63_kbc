package MovableObject;

import GameManager.GameManager;
import GameManager.SpriteManager;
import PowerUp.PowerUp;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Paddle extends MovableObject {
    private double speed = 7;
    private boolean isExpanded = false;
    private long expandEndTime = 0; // thời gian hết hiệu lực
    private PowerUp currentPowerUp;
    private Rectangle2D.Double hitbox;
    private SpriteManager spriteManager;
    private BufferedImage paddleSprite;

    public Paddle(int x, int y, int width, int height, SpriteManager spriteManager) {
        super(x, y, width, height);
        this.spriteManager = spriteManager;
        this.paddleSprite = spriteManager.getSprite("paddle_orange");
        hitbox = new Rectangle2D.Double(x, y, width, height);
    }

    public Shape getHitbox() {
        return hitbox;
    }

    public void moveLeft() {
        dx = -speed;
    }

    public void moveRight() {
        dx = speed;
    }

    public void stop() {
        dx = 0;
    }

    public void applyPowerUp() {

    }

    @Override
    public void move() {
        x += (int) dx;

        if (x < 0) {
            x = 0;
        }

        if (x + width > GameManager.getWindowWidth()) {
            x = GameManager.getWindowWidth() - width;
        }

        hitbox.setRect(x, y, width, height);
    }

    @Override
    public void update() {
        move();
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(paddleSprite, (int) Math.round(x), (int) Math.round(y), width, height, null);
    }

    // Mở rộng paddle (chỉ khi chưa mở rộng)
    public synchronized void expand() {
        long currentTime = System.currentTimeMillis();

        // Nếu paddle chưa mở rộng → mở rộng và khởi tạo bộ đếm
        if (!isExpanded) {
            this.width *= 1.5;
            if (this.width > GameManager.getWindowWidth()) {
                this.width = GameManager.getWindowWidth();
            }
            hitbox.setRect(x, y, width, height);
            isExpanded = true;

            // Đặt thời gian kết thúc sau 10 giây
            expandEndTime = currentTime + 10000;

            // Tạo luồng kiểm tra hết thời gian hiệu lực
            new Thread(() -> {
                try {
                    while (System.currentTimeMillis() < expandEndTime) {
                        Thread.sleep(500); // kiểm tra mỗi 0.5s
                    }
                    resetSize(); // hết thời gian thì thu nhỏ lại
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        else {
            // Nếu paddle đã mở rộng → reset lại thời gian thêm 10 giây
            expandEndTime = currentTime + 10000;
        }
    }


    // Đặt lại kích thước paddle mặc định
    public void resetSize() {
        this.width = 128; // hoặc giá trị mặc định ban đầu của paddle
        hitbox.setRect(x, y, width, height);
        isExpanded = false;
    }

    public boolean isExpanded() {
        return isExpanded;
    }
}
