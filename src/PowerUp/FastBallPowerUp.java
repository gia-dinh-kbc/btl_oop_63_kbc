package PowerUp;

import MovableObject.Ball;
import GameManager.GameManager;
import GameManager.SpriteManager;

public class FastBallPowerUp extends PowerUp {

    public FastBallPowerUp(double x, double y, SpriteManager spriteManager) {
        super(x, y, 32, 32, spriteManager.getSprite("ball_green"));
    }

    @Override
    public void applyEffect(GameManager game) {
        // Lấy danh sách tất cả các bóng trong game
        for (Ball ball : game.getBalls()) {
            ball.increaseSpeed();  // Tăng tốc tất cả các bóng
        }

        // Tạo một thread mới để hủy hiệu lực sau 10 giây
        new Thread(() -> {
            try {
                Thread.sleep(10000);  // Chờ 10 giây
                // Đặt lại tốc độ của tất cả các bóng về giá trị mặc định
                for (Ball ball : game.getBalls()) {
                    ball.resetSpeed();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
