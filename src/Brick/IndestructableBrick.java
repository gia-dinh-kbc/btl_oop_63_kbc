package Brick;

import GameManager.SpriteManager;

public class IndestructableBrick extends Brick {
    public IndestructableBrick(int x, int y, int width, int height, SpriteManager spriteManager) {
        super(x, y, width, height, spriteManager);
        hitPoints = -1;
        this.brickSprite = spriteManager.getSprite("brick_purple");
    }
}