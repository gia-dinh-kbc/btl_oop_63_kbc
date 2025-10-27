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
        Ball ball = game.getBall();
        ball.increaseSpeed();  // Tăng tốc bóng

        // Tạo một thread mới để hủy hiệu lực sau 10 giây
        new Thread(() -> {
            try {
                Thread.sleep(10000);  // Chờ 10 giây
                ball.resetSpeed();    // Đặt lại tốc độ bóng về giá trị mặc định
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
