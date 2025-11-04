package GameManager;

import Brick.Brick;
import Brick.IndestructableBrick;
import Brick.NormalBrick;
import Brick.StrongBrick;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * LevelManager chịu trách nhiệm quản lý các màn chơi:.
 * - Tải bản đồ gạch từ file
 * - Quản lý level hiện tại
 * - Kiểm tra điều kiện hoàn thành level
 */
public class LevelManager {
    private int currentLevel;
    private final int totalLevels = 3;
    private List<Brick> bricks;

    // Kích thước gạch
    private static final int BRICK_WIDTH = 64;
    private static final int BRICK_HEIGHT = 32;

    // Offset để căn giữa bản đồ
    private static final int LEVEL_OFFSET_X = 0;
    private static final int LEVEL_OFFSET_Y = 100;

    public LevelManager() {
        this.currentLevel = 1;
        this.bricks = new ArrayList<>();
    }

    /**
     * Tải màn chơi theo level từ file.
     * @param level số thứ tự màn chơi
     */
    public void loadLevel(int level) {
        bricks.clear();
        currentLevel = level;

        String filename = "/Resource/DataFiles/BrickLayoutLevel" + level + ".txt";

        try {
            loadLevelFromFile(filename);
            System.out.println("Loaded level " + currentLevel + " with " + bricks.size() + " bricks from file: " + filename);
        } catch (IOException e) {
            System.err.println("Error loading level " + level + " from file: " + filename);
            e.printStackTrace();
            // Fallback to hardcoded level if file not found
            loadFallbackLevel(level);
        }
    }

    /**
     * Đọc file layout và tạo gạch tương ứng.
     * File format:
     * - 0: Air (không có gạch)
     * - 1: Normal Brick
     * - 2: Strong Brick
     * - 3: Indestructible Brick
     */
    private void loadLevelFromFile(String filename) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(filename);

        if (inputStream == null) {
            throw new IOException("File not found: " + filename);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Bỏ qua dòng trống
                if (line.isEmpty()) {
                    continue;
                }

                // Đọc từng ký tự trong dòng
                String[] values = line.split("\\s+"); // Tách theo khoảng trắng

                for (int col = 0; col < values.length; col++) {
                    try {
                        int brickType = Integer.parseInt(values[col]);

                        // Tính vị trí gạch
                        int x = LEVEL_OFFSET_X + col * BRICK_WIDTH;
                        int y = LEVEL_OFFSET_Y + row * BRICK_HEIGHT;

                        // Tạo gạch tương ứng
                        Brick brick = createBrick(brickType, x, y);

                        if (brick != null) {
                            bricks.add(brick);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid brick type at row " + row + ", col " + col + ": " + values[col]);
                    }
                }

                row++;
            }
        }
    }

    /**
     * Tạo gạch theo loại.
     * @param type loại gạch (0-3)
     * @param x vị trí x
     * @param y vị trí y
     * @return Brick object hoặc null nếu type = 0
     */
    private Brick createBrick(int type, int x, int y) {
        SpriteManager spriteManager = SpriteManager.getInstance();
        switch (type) {
            case 0:
                // Air - không tạo gạch
                return null;

            case 1:
                // Normal Brick
                return new NormalBrick(x, y, BRICK_WIDTH - 1, BRICK_HEIGHT - 1, spriteManager);

            case 2:
                // Strong Brick
                return new StrongBrick(x, y, BRICK_WIDTH - 1, BRICK_HEIGHT - 1, spriteManager);

            case 3:
                // Indestructible Brick
                return new IndestructableBrick(x, y, BRICK_WIDTH - 1, BRICK_HEIGHT - 1, spriteManager);

            default:
                System.err.println("Unknown brick type: " + type);
                return null;
        }
    }

    /**
     * Load hardcoded level nếu file không tồn tại (fallback).
     */
    private void loadFallbackLevel(int level) {
        System.out.println("Loading fallback level " + level);

        switch (level) {
            case 1:
                loadFallbackLevel1();
                break;
            case 2:
                loadFallbackLevel2();
                break;
            case 3:
                loadFallbackLevel3();
                break;
            default:
                System.err.println("Invalid level: " + level);
                loadFallbackLevel1();
        }
    }

    private void loadFallbackLevel1() {
        SpriteManager spriteManager = SpriteManager.getInstance();
        for (int i = 0; i < 10; i++) {
            for (int j = 2; j <= 3; j++) {
                Brick brick = new NormalBrick(
                        i * BRICK_WIDTH,
                        j * BRICK_HEIGHT + LEVEL_OFFSET_Y,
                        BRICK_WIDTH - 1,
                        BRICK_HEIGHT - 1,
                        spriteManager
                );
                bricks.add(brick);
            }
        }
    }

    private void loadFallbackLevel2() {
        SpriteManager spriteManager = SpriteManager.getInstance();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                Brick brick;
                if ((i + j) % 2 == 0) {
                    brick = new StrongBrick(
                            i * BRICK_WIDTH,
                            j * BRICK_HEIGHT + LEVEL_OFFSET_Y + 50,
                            BRICK_WIDTH - 1,
                            BRICK_HEIGHT - 1,
                            spriteManager
                    );
                } else {
                    brick = new NormalBrick(
                            i * BRICK_WIDTH,
                            j * BRICK_HEIGHT + LEVEL_OFFSET_Y + 50,
                            BRICK_WIDTH - 1,
                            BRICK_HEIGHT - 1,
                            spriteManager
                    );
                }
                bricks.add(brick);
            }
        }
    }

    private void loadFallbackLevel3() {
        SpriteManager spriteManager = SpriteManager.getInstance();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                Brick brick;
                if ((i + j) % 2 == 0) {
                    if (i == 0) {
                        brick = new IndestructableBrick(
                                i * BRICK_WIDTH,
                                j * BRICK_HEIGHT + LEVEL_OFFSET_Y + 50,
                                BRICK_WIDTH - 1,
                                BRICK_HEIGHT - 1,
                                spriteManager
                        );
                    } else {
                        brick = new StrongBrick(
                                i * BRICK_WIDTH,
                                j * BRICK_HEIGHT + LEVEL_OFFSET_Y + 50,
                                BRICK_WIDTH - 1,
                                BRICK_HEIGHT - 1,
                                spriteManager
                        );
                    }
                } else {
                    brick = new NormalBrick(
                            i * BRICK_WIDTH,
                            j * BRICK_HEIGHT + LEVEL_OFFSET_Y + 50,
                            BRICK_WIDTH - 1,
                            BRICK_HEIGHT - 1,
                            spriteManager
                    );
                }
                bricks.add(brick);
            }
        }
    }

    /**
     * Kiểm tra xem tất cả gạch có thể phá hủy đã bị phá chưa.
     * @return true nếu tất cả gạch có thể phá hủy đã bị phá
     */
    public boolean isLevelComplete() {
        if (bricks.isEmpty()) {
            return true;
        }

        for (Brick brick : bricks) {
            if (!(brick instanceof IndestructableBrick) && !brick.isDestroyed()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Chuyển sang level tiếp theo.
     * @return true nếu còn level tiếp theo, false nếu đã hết game
     */
    public boolean nextLevel() {
        if (currentLevel < totalLevels) {
            currentLevel++;
            loadLevel(currentLevel);
            return true;
        }
        return false;
    }

    /**
     * Reset về level đầu tiên.
     */
    public void reset() {
        currentLevel = 1;
        loadLevel(currentLevel);
    }

    // Getters
    public List<Brick> getBricks() {
        return bricks;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getTotalLevels() {
        return totalLevels;
    }

    public boolean hasMoreLevels() {
        return currentLevel < totalLevels;
    }
}