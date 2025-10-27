package GameManager;

// Import các class cần thiết
import Brick.Brick;
import Brick.NormalBrick;
import Brick.StrongBrick;
import MovableObject.Ball;
import MovableObject.Paddle;
import PowerUp.PowerUp;
import PowerUp.ExpandsPaddlePowerUp;
import PowerUp.FastBallPowerUp;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Lớp GameManager chịu trách nhiệm quản lý toàn bộ logic trò chơi:
 * - Khởi tạo paddle, bóng, gạch, power-up
 * - Xử lý va chạm
 * - Quản lý điểm, mạng sống, trạng thái game
 * - Lắng nghe phím điều khiển
 */
public class GameManager implements KeyListener {
    private SpriteManager spriteManager = new SpriteManager();  // Quản lý hình ảnh
    private SoundManager soundManager = new SoundManager();      // Quản lý âm thanh

    // Khởi tạo các đối tượng chính trong game
    private Paddle paddle = new Paddle(0, 0, 128, 24, spriteManager);
    private Ball ball = new Ball(0, 0, 24, spriteManager);
    private List<Brick> bricks = new ArrayList<>();              // Danh sách các viên gạch
    private List<PowerUp> powerUps = new ArrayList<>();          // Danh sách vật phẩm rơi ra
    private int score = 0;                                       // Điểm người chơi
    private int lives = 3;                                       // Số mạng
    private int gameState = 0;                                   // Trạng thái game (0: bắt đầu, 1: đang chơi, 2: thua, 3: thắng)
    private boolean ballAttached = true;                         // Bóng đang dính vào paddle hay không
    int currentLevel = 1; // Biến theo dõi màn hiện tại

    // Kích thước cửa sổ game
    private static final int WINDOW_WIDTH = 652;
    private static final int WINDOW_HEIGHT = 800;

    // Khi khởi tạo GameManager
    public GameManager() {
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("start"); // Phát nhạc nền khởi đầu
    }

    /**
     * Hàm khởi động lại game.
     * Reset toàn bộ giá trị và tạo lại bản đồ gạch.
     */
    public void startGame() {
        lives = 3;
        score = 0;
        ballAttached = true;
        currentLevel = 1; // Quay lại level đầu

        // Đặt vị trí paddle và bóng
        paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
        paddle.setY(700);
        ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        ball.setDx(0);
        ball.setDy(0);
        bricks.clear();
        loadLevel(currentLevel); // Tải màn chơi
        gameState = 1; // Đưa game về trạng thái đang chơi
    }

    /**
     * Tạo bản đồ gạch cho từng level.
     */
    private void loadLevel(int level) {
        bricks.clear();
        int width = 64;
        int height = 32;

        if (level == 1) {
            for (int i = 0; i < 10; i++) {
                for (int j = 1; j <= 3; j++) {
                    if (j == 1) {
                        Brick brick = new StrongBrick(i * width, j * height + 100, width - 1, height - 1, spriteManager);
                        bricks.add(brick);
                    } else {
                        Brick brick = new NormalBrick(i * width, j * height + 100, width - 1, height - 1, spriteManager);
                        bricks.add(brick);
                    }
                }
            }
        } else if (level == 2) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i + j) % 2 == 0) {
                        Brick brick = new StrongBrick(i * width, j * height + 150, width - 1, height - 1, spriteManager);
                        bricks.add(brick);
                    } else {
                        Brick brick = new NormalBrick(i * width, j * height + 150, width - 1, height - 1, spriteManager);
                        bricks.add(brick);
                    }
                }
            }
        }
    }

    /**
     * Hàm cập nhật trạng thái game (được gọi mỗi frame).
     */
    public void updateGame() {
        if (gameState != 1) return; // Nếu không phải đang chơi thì bỏ qua

        // Nếu bóng còn dính paddle → di chuyển theo paddle
        if (ballAttached) {
            double ballX = paddle.getX() + (paddle.getWidth() / 2) - (ball.getWidth() / 2);
            double ballY = paddle.getY() - ball.getHeight();
            ball.setX(ballX);
            ball.setY(ballY);
        } else {
            ball.update(); // Cập nhật vị trí bóng
        }

        paddle.update();

        // Kiểm tra va chạm
        if (!ballAttached) {
            checkCollisions();
        }

        // --- Cập nhật trạng thái Power-Up ---
        Iterator<PowerUp> pIterator = powerUps.iterator();
        while (pIterator.hasNext()) {
            PowerUp p = pIterator.next();
            p.update();

            // Nếu paddle bắt được Power-Up
            if (paddle.getHitbox().intersects(p.getHitbox().getBounds2D())) {
                p.applyEffect(this);
                soundManager.playSound("powerup");
                pIterator.remove();
            }
            // Nếu rơi quá đáy màn hình
            else if (p.getY() > WINDOW_HEIGHT) {
                pIterator.remove();
            }
        }

        // Nếu bóng rơi ra khỏi màn hình
        if (ball.getY() > WINDOW_HEIGHT && !ballAttached) {
            lives--;
            soundManager.playSound("lose");
            resetAllPowerUps();
            if (lives == 0) {
                gameOver(); // Thua cuộc
            } else {
                ballAttached = true; // Reset bóng về paddle
                ball.setDx(0);
                ball.setDy(0);
                ball.resetSpeed();
            }
        }

        // Nếu phá hết gạch → chuyển màn or win
        if (bricks.isEmpty()) {
            nextLevel();
        }

        // Phát nhạc nền nếu chưa chạy
        if (gameState == 1 && !soundManager.isPlaying("background")) {
            soundManager.stopAllSounds();
            soundManager.playLoopingSound("background");
        }
    }

    /**
     * Khi qua màn → sang level mới hoặc thắng chung cuộc.
     */
    private void nextLevel() {
        if (currentLevel == 1 && score != 0) {
            currentLevel = 2;
            soundManager.playSound("win");
            ballAttached = true;
            ball.setDx(0);
            ball.setDy(0);
            ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
            paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
            paddle.setY(700);
            loadLevel(currentLevel);
        } else {
            youWin();
        }
    }
    /** Khi thua cuộc */
    public void gameOver() {
        gameState = 2;
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("gameOver");
    }

    /** Khi chiến thắng */
    public void youWin() {
        gameState = 3;
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("win");
    }

    /**
     * Xử lý va chạm giữa bóng, tường, gạch và paddle.
     */
    public void checkCollisions() {
        // --- Va chạm với tường ---
        if (ball.getX() <= 0) {
            ball.setX(0);
            ball.reverseX();
            soundManager.playSound("bounce");
        } else if (ball.getX() + ball.getWidth() >= WINDOW_WIDTH) {
            ball.setX(WINDOW_WIDTH - ball.getWidth());
            ball.reverseX();
            soundManager.playSound("bounce");
        }

        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.reverseY();
            soundManager.playSound("bounce");
        }

        // --- Va chạm với gạch ---
        Iterator<Brick> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            Brick brick = iterator.next();

            if (ball.getHitbox().intersects(brick.getHitbox().getBounds2D())) {
                // Tính toán vùng chồng lấn để xác định hướng phản xạ
                double ballX = ball.getX();
                double ballY = ball.getY();
                int ballWidth = ball.getWidth();
                int ballHeight = ball.getHeight();

                double brickX = brick.getX();
                double brickY = brick.getY();
                int brickWidth = brick.getWidth();
                int brickHeight = brick.getHeight();

                double overlapLeft = (ballX + ballWidth) - brickX;
                double overlapRight = (brickX + brickWidth) - ballX;
                double overlapTop = (ballY + ballHeight) - brickY;
                double overlapBottom = (brickY + brickHeight) - ballY;

                double minOverlap = Math.min(
                        Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop, overlapBottom)
                );

                double currentSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());

                brick.takeHit(); // Giảm máu gạch
                soundManager.playSound("hit");

                // Xác định hướng bật lại
                if (minOverlap == overlapTop && ball.getDy() > 0) {
                    ball.setY(brickY - ballHeight);
                    ball.reverseY();
                } else if (minOverlap == overlapBottom && ball.getDy() < 0) {
                    ball.setY(brickY + brickHeight);
                    ball.reverseY();
                } else if (minOverlap == overlapLeft && ball.getDx() > 0) {
                    ball.setX(brickX - ballWidth);
                    ball.reverseX();
                } else if (minOverlap == overlapRight && ball.getDx() < 0) {
                    ball.setX(brickX + brickWidth);
                    ball.reverseX();
                } else {
                    ball.reverseY();
                    ball.reverseX();
                }

                // Giữ nguyên tốc độ sau khi đổi hướng
                double newSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
                if (newSpeed > 0) {
                    double speedRatio = currentSpeed / newSpeed;
                    ball.setDx(ball.getDx() * speedRatio);
                    ball.setDy(ball.getDy() * speedRatio);
                }

                // Nếu gạch bị phá → tăng điểm và xóa khỏi danh sách
                if (brick.isDestroyed()) {
                    score += 100;
                    iterator.remove();
                    soundManager.playSound("break");

                    // Tạo ngẫu nhiên Power-Up với xác suất tùy chỉnh
                    double dropChance = Math.random();
                    if (dropChance < 0.8) {
                        PowerUp newPowerUp;
                        if (Math.random() < 0.5) {
                            newPowerUp = new ExpandsPaddlePowerUp(
                                    brick.getX() + brick.getWidth() / 2.0,
                                    brick.getY() + brick.getHeight() / 2.0,
                                    spriteManager
                            );
                        } else {
                            newPowerUp = new FastBallPowerUp(
                                    brick.getX() + brick.getWidth() / 2.0,
                                    brick.getY() + brick.getHeight() / 2.0,
                                    spriteManager
                            );
                        }
                        powerUps.add(newPowerUp);
                    }
                }

                break; // Tránh va chạm nhiều gạch cùng lúc
            }
        }

        // --- Va chạm với paddle ---
        if (ball.getX() + ball.getWidth() >= paddle.getX()
                && ball.getX() <= paddle.getX() + paddle.getWidth()
                && ball.getY() + ball.getHeight() >= paddle.getY()
                && ball.getY() <= paddle.getY() + paddle.getHeight()
                && ball.getDy() > 0) {

            ball.setY(paddle.getY() - ball.getHeight());

            // Tính toán góc bật tùy theo vị trí bóng chạm paddle
            double ballCenterX = ball.getX() + (double) ball.getWidth() / 2;
            double paddleCenterX = paddle.getX() + (double) paddle.getWidth() / 2;
            double relativeHitX = (double)(ballCenterX - paddleCenterX) / (paddle.getWidth() / 2.0);
            relativeHitX = Math.max(-1.0, Math.min(1.0, relativeHitX));

            double bounceAngle = relativeHitX * Math.toRadians(60);
            double currentSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());

            ball.setDx(currentSpeed * Math.sin(bounceAngle));
            ball.setDy(-currentSpeed * Math.cos(bounceAngle));
            soundManager.playSound("bounce");
        }
    }

    public void resetAllPowerUps() {
        // Reset paddle kích thước gốc
        paddle.resetSize();

        // Reset tốc độ bóng
        ball.resetSpeed();

        // Xóa toàn bộ vật phẩm đang rơi
        powerUps.clear();
    }

    // Các getter hỗ trợ cho renderer hoặc UI
    public List<Brick> getBricks() { return bricks; }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public Ball getBall() { return ball; }
    public Paddle getPaddle() { return paddle; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getGameState() { return gameState; }
    public SoundManager getSoundManager() { return soundManager; }

    public static int getWindowWidth() { return WINDOW_WIDTH; }
    public static int getWindowHeight() { return WINDOW_HEIGHT; }

    /** Phóng bóng ra khỏi paddle */
    private void launchBall() {
        ballAttached = false;
        ball.launch();
        soundManager.playSound("bounce");
    }

    // --- Xử lý bàn phím ---
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            paddle.moveLeft();
        } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            paddle.moveRight();
        } else if (key == KeyEvent.VK_SPACE) {
            if (gameState == 0 || gameState == 2 || gameState == 3) {
                startGame(); // Bắt đầu lại game
            } else if (gameState == 1 && ballAttached) {
                launchBall(); // Phóng bóng
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A ||
                key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            paddle.stop(); // Dừng paddle khi nhả phím
        }
    }

    /** Hàm main khởi chạy game */
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true"); // Bật tăng tốc đồ họa
        javax.swing.SwingUtilities.invokeLater(
                () -> {
                    JFrame frame = new JFrame("Arkanoid");
                    frame.setIgnoreRepaint(true);

                    GameManager gameManager = new GameManager();
                    Renderer renderer = new Renderer(gameManager);

                    // Cấu hình cửa sổ
                    frame.add(renderer);
                    frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setResizable(false);
                    frame.setLocationRelativeTo(null);

                    renderer.addKeyListener(gameManager);
                    renderer.setFocusable(true);

                    frame.setVisible(true);
                    renderer.requestFocusInWindow();
                    renderer.requestFocus();

                    // Khi tắt chương trình → giải phóng tài nguyên âm thanh
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        gameManager.getSoundManager().cleanup();
                    }));

                    // --- Game Loop ---
                    new Thread(
                            () -> {
                                final double TARGET_FPS = 30.0;
                                final long OPTIMAL_TIME = (long) (1000000000 / TARGET_FPS);
                                long lastLoopTime = System.nanoTime();

                                while (true) {
                                    long now = System.nanoTime();
                                    long updateLength = now - lastLoopTime;
                                    lastLoopTime = now;

                                    gameManager.updateGame(); // Cập nhật logic
                                    renderer.repaint();        // Vẽ lại khung hình

                                    // Dừng một chút để giữ FPS ổn định
                                    long sleepTime = (OPTIMAL_TIME - updateLength) / 1000000;
                                    if (sleepTime > 0) {
                                        try {
                                            Thread.sleep(sleepTime);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            })
                            .start();
                });
    }
}
