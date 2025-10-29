package PowerUp;

import MovableObject.Ball;
import GameManager.GameManager;
import GameManager.SpriteManager;

public class SplitBallPowerUp extends PowerUp {

    public SplitBallPowerUp(double x, double y, SpriteManager spriteManager) {
        super(x, y, 32, 32, spriteManager.getSprite("ball_red"));  // Thêm sprite cho vật phẩm này
    }

    @Override
    public void applyEffect(GameManager game) {
        // Lấy bóng chính từ GameManager
        Ball mainBall = game.getBalls().get(0);

        // Tạo 2 bóng mới với tốc độ giống bóng chính
        Ball ball1 = new Ball((int) mainBall.getX(), (int) mainBall.getY(), mainBall.getWidth(), mainBall.getSpriteManager());
        Ball ball2 = new Ball((int) mainBall.getX(), (int) mainBall.getY(), mainBall.getWidth(), mainBall.getSpriteManager());

        // Đặt các giá trị dx và dy cho bóng mới sao cho chúng di chuyển theo 2 hướng khác nhau
        double angle1 = Math.toRadians(45); // Đặt góc cho bóng thứ nhất
        double angle2 = Math.toRadians(135); // Đặt góc cho bóng thứ hai

        // Cập nhật hướng di chuyển của ball1
        ball1.setDx(mainBall.getSpeed() * Math.cos(angle1));
        ball1.setDy(-mainBall.getSpeed() * Math.sin(angle1));

        // Cập nhật hướng di chuyển của ball2
        ball2.setDx(mainBall.getSpeed() * Math.cos(angle2));
        ball2.setDy(-mainBall.getSpeed() * Math.sin(angle2));

        // Thêm các bóng mới vào danh sách bóng
        game.addBall(ball1);
        game.addBall(ball2);

        // Note: Sound is already played in GameManager when powerup is collected
    }
}