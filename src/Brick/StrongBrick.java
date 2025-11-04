package Brick;

import GameManager.SpriteManager;

public class StrongBrick extends Brick {

    public StrongBrick(int x, int y, int width, int height, SpriteManager spriteManager) {
        super(x, y, width, height, spriteManager);
        this.hitPoints = 2;
        updateSprite();
    }

    @Override
    public void takeHit() {
        super.takeHit();
        updateSprite();
    }

    private void updateSprite() {
        if (hitPoints == 2) {
            this.brickSprite = spriteManager.getSprite("brick_blue");
        } else if (hitPoints == 1) {
            this.brickSprite = spriteManager.getSprite("brick_pink");
        }
    }
}