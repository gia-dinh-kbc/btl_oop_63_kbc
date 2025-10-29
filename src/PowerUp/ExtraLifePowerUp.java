package PowerUp;

import GameManager.GameManager;
import GameManager.SpriteManager;

public class ExtraLifePowerUp extends PowerUp {

    public ExtraLifePowerUp(double x, double y, SpriteManager spriteManager) {
        // Đặt sprite cho PowerUp Extra Life, ví dụ: icon hình trái tim hoặc biểu tượng tăng mạng
        super(x, y, 32, 32, spriteManager.getSprite("ball_red"));
    }

    @Override
    public void applyEffect(GameManager game) {
        // Tăng mạng nếu mạng hiện tại ít hơn 3
        if (game.getLives() < 3) {
            game.setLives(game.getLives() + 1);
            game.getSoundManager().playSound("extraLife"); // Phát âm thanh khi nhận PowerUp
        }
    }
}
