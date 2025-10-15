package Brick;


import GameManager.SpriteManager;

public class NormalBrick extends Brick {

    public NormalBrick(int x, int y, int width, int height, SpriteManager spriteManager) {
        super(x, y, width, height, spriteManager);
        this.type = 1;
        this.hitPoints = 1;
        this.brickSprite = spriteManager.getSprite("brick_pink");
    }
}
