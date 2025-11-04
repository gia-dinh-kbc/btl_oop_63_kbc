package GameManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private BufferedImage spriteSheet;
    private final Map<String, BufferedImage> sprites = new HashMap<>();

    private static final int BRICK_WIDTH = 64;
    private static final int BRICK_HEIGHT = 32;
    private static final int BALL_SIZE = 24;
    private static final int PADDLE_WIDTH = 128;
    private static final int PADDLE_HEIGHT = 24;

    /**
     * SpriteManager constructor.
     */
    public SpriteManager() {
        loadSpriteSheet();
        extractSprites();
    }

    private void loadSpriteSheet() {
        try {
            spriteSheet = ImageIO.read(getClass().getResourceAsStream("/Resource/ObjectAssets/BasicArkanoidPack.png"));
            System.out.println("Sprite sheet loaded successfully!");
        } catch (IOException e) {
            System.err.println("Error loading sprite sheet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extract image in spritesheet.
     */
    private void extractSprites() {
        if (spriteSheet == null) {
            System.err.println("Sprite sheet not loaded!");
            return;
        }

        try {
            sprites.put("brick_orange", getSprite(0, 0, BRICK_WIDTH, BRICK_HEIGHT));
            sprites.put("brick_red", getSprite(72, 0, BRICK_WIDTH, BRICK_HEIGHT));
            sprites.put("brick_green", getSprite(144, 0, BRICK_WIDTH, BRICK_HEIGHT));
            sprites.put("brick_blue", getSprite(216, 0, BRICK_WIDTH, BRICK_HEIGHT));

            sprites.put("brick_pink", getSprite(0, 40, BRICK_WIDTH, BRICK_HEIGHT));
            sprites.put("brick_cyan", getSprite(72, 40, BRICK_WIDTH, BRICK_HEIGHT));
            sprites.put("brick_yellow", getSprite(144, 40, BRICK_WIDTH, BRICK_HEIGHT));
            sprites.put("brick_purple", getSprite(216, 40, BRICK_WIDTH, BRICK_HEIGHT));

            sprites.put("ball_red", getSprite(0, 80, BALL_SIZE, BALL_SIZE));
            sprites.put("ball_green", getSprite(32, 80, BALL_SIZE, BALL_SIZE));
            sprites.put("ball_blue", getSprite(64, 80, BALL_SIZE, BALL_SIZE));
            sprites.put("ball_brown", getSprite(96, 80, BALL_SIZE, BALL_SIZE));
            sprites.put("heart", getSprite(122, 82, BALL_SIZE, BALL_SIZE));

            sprites.put("paddle_red", getSprite(152, 80, PADDLE_WIDTH, PADDLE_HEIGHT));
            sprites.put("paddle_orange", getSprite(0, 112, PADDLE_WIDTH + 32, PADDLE_HEIGHT));

            System.out.println("Sprites extracted successfully!");
        } catch (Exception e) {
            System.err.println("Error extracting sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * getSprite from spritesheet
     * @param col column coordinate
     * @param row row coordinate
     * @param width sprite width
     * @param height sprite height
     * @return sub image
     */
    private BufferedImage getSprite(int col, int row, int width, int height) {

        return spriteSheet.getSubimage(col, row, width, height);
    }

    public BufferedImage getSprite(String name) {
        return sprites.get(name);
    }

    public int getBrickWidth() {
        return BRICK_WIDTH;
    }

    public int getBrickHeight() {
        return BRICK_HEIGHT;
    }

    public int getBallSize() {
        return BALL_SIZE;
    }

    public int getPaddleWidth() {
        return PADDLE_WIDTH;
    }

    public int getPaddleHeight() {
        return PADDLE_HEIGHT;
    }
}
