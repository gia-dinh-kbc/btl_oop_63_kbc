package PowerUp;

import MovableObject.Paddle;
import GameManager.GameManager;
import GameManager.SpriteManager;

public class ExpandsPaddlePowerUp extends PowerUp {
    public ExpandsPaddlePowerUp(double x, double y, SpriteManager spriteManager) {
        super(x, y, 32, 32, spriteManager.getSprite("ball_blue"));
    }

    @Override
    public void applyEffect(GameManager game) {
        game.getPaddle().expand();
    }
}
