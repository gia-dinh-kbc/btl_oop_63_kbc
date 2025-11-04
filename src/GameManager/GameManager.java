package GameManager;

// Import các class cần thiết
import Brick.Brick;
import MovableObject.Ball;
import MovableObject.Paddle;
import PowerUp.PowerUp;
import PowerUp.ExpandsPaddlePowerUp;
import PowerUp.FastBallPowerUp;
import PowerUp.SlowBallPowerUp;
import PowerUp.SplitBallPowerUp;
import PowerUp.ExtraLifePowerUp;

import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;

/**
 * Lớp GameManager chịu trách nhiệm quản lý toàn bộ logic trò chơi:
 * - Khởi tạo paddle, bóng, power-up
 * - Xử lý va chạm
 * - Quản lý điểm, mạng sống, trạng thái game
 * - Lắng nghe phím điều khiển
 */
public class GameManager implements KeyListener {
    private SpriteManager spriteManager = SpriteManager.getInstance();
    private SoundManager soundManager = new SoundManager();
    private ScoreManager scoreManager = new ScoreManager();
    private LevelManager levelManager = new LevelManager();

    // Khởi tạo các đối tượng chính trong game
    private Paddle paddle = new Paddle(0, 0, 128, 24, spriteManager);
    private Ball ball = new Ball(0, 0, 24, spriteManager);
    private List<Ball> balls = new ArrayList<>();
    public void addBall(Ball ball) {
        balls.add(ball);  // Thêm bóng vào danh sách balls
    }
    public List<Ball> getBalls() {
        return balls;  // Trả về danh sách tất cả các bóng
    }
    private List<Brick> bricks = new ArrayList<>();              // Danh sách các viên gạch
    private List<PowerUp> powerUps = new ArrayList<>();          // Danh sách vật phẩm rơi ra
    private int score = 0;                                       // Điểm người chơi
    private int lives = 3;
    private boolean isStartingGame = false;// Số mạng

    // Biến cờ này không còn cần thiết nếu logic âm thanh được xử lý đúng
    // private boolean isBackgroundPlaying = false;

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    private int gameState = 0;                                   // Trạng thái game (0: bắt đầu, 1: đang chơi, 2: thua, 3: thắng)
    private boolean ballAttached = true;                         // Bóng đang dính vào paddle hay không
    int currentLevel = 1; // Biến theo dõi màn hiện tại

    // Kích thước cửa sổ game
    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 800;

    // Khi khởi tạo GameManager
    public GameManager() {
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("start");
    }

    /**
     * Hàm khởi động lại game.
     * Reset toàn bộ giá trị và tạo lại bản đồ gạch.
     */
    public void startGame() {
        lives = 3;
        score = 0;
        ballAttached = true;

        // Reset level về màn đầu
        levelManager.reset();

        // Đặt vị trí paddle và bóng
        paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
        paddle.setY(700);
        balls.clear();
        ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        ball.setDx(0);
        ball.setDy(0);
        balls.add(ball);

        gameState = 1;

        // === THAY ĐỔI: Bật nhạc nền Ở ĐÂY ===
        // Logic này được chuyển từ updateGame() về đây.
        // Nó sẽ chạy 1 lần duy nhất khi game bắt đầu.
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("background");
    }

    /**
     * Hàm cập nhật trạng thái game (được gọi mỗi frame).
     */
    public void updateGame() {
        if (gameState != 1) return; // Nếu không phải đang chơi thì bỏ qua

        // Nếu bóng còn dính paddle thì di chuyển theo paddle
        if (ballAttached) {
            // Safe to use for-each here since we're not modifying the list
            for (Ball b : balls) {
                b.setX(paddle.getX() + (paddle.getWidth() / 2) - (b.getWidth() / 2));
                b.setY(paddle.getY() - b.getHeight());
            }
        } else {
            // Cập nhật tất cả các bóng trong game
            // Use a copy to avoid ConcurrentModificationException when powerups add balls
            List<Ball> ballsCopy = new ArrayList<>(balls);
            for (Ball b : ballsCopy) {
                b.update(); // Cập nhật vị trí của từng bóng
            }
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
        // Use a flag to track if we need to handle life loss
        boolean needToLoseLife = false;

        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball b = ballIterator.next();
            if (b.getY() > WINDOW_HEIGHT) {
                // Nếu bóng rơi ra khỏi màn hình
                ballIterator.remove(); // Xóa bóng khỏi danh sách

                // Nếu không còn bóng nào -> đánh dấu cần trừ mạng
                if (balls.isEmpty()) {
                    needToLoseLife = true;
                    break; // Exit the loop immediately
                }
            }
        }

        // Handle life loss AFTER we've finished iterating
        if (needToLoseLife) {
            lives--;  // Trừ mạng nếu không còn bóng nào
            soundManager.playSound("lose");
            resetAllPowerUps();  // Reset tất cả PowerUps

            if (lives == 0) {
                gameOver(); // Thua cuộc
            } else {
                // Nếu còn mạng, reset bóng
                resetBalls();
            }
        }

        // Kiểm tra xem đã hoàn thành level chưa
        if (levelManager.isLevelComplete()) {
            nextLevel();
        }

        // === THAY ĐỔI: ĐÃ XÓA ===
        // Khối 'if' kiểm tra nhạc nền đã được xóa khỏi đây
        // vì nó không hiệu quả (polling).
    }

    /**
     * Khi qua màn → sang level mới hoặc thắng chung cuộc.
     */
    public void nextLevel() {
        powerUps.clear();

        // Thử chuyển sang level tiếp theo
        if (levelManager.nextLevel()) {
            resetBalls();

            // Reset paddle position
            paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
            paddle.setY(700);
            paddle.resetSize();
        } else {
            // Đã hoàn thành tất cả level
            youWin();
        }
    }

    public void resetBalls() {
        balls.clear();  // Xóa tất cả các bóng trong game

        // Tạo lại một bóng chính và đặt vào vị trí trên paddle
        Ball newBall = new Ball(0, 0, 24, spriteManager);

        // Đặt bóng tại vị trí trên paddle
        double ballX = paddle.getX() + (paddle.getWidth() / 2.0) - (newBall.getWidth() / 2.0);
        double ballY = paddle.getY() - newBall.getHeight();

        newBall.setX(ballX);
        newBall.setY(ballY);
        newBall.setDx(0);  // Đảm bảo bóng không di chuyển khi bắt đầu lại
        newBall.setDy(0);
        newBall.resetSpeed();  // Đặt lại tốc độ

        balls.add(newBall);  // Thêm bóng vào danh sách
        ballAttached = true;  // Đặt lại trạng thái bóng dính vào paddle
    }


    /** Khi thua cuộc */
    public void gameOver() {
        gameState = 2;

        // Check if it's a high score before adding
        boolean isNewHighScore = scoreManager.isHighScore(score);
        scoreManager.addScore(score);

        if (isNewHighScore && score > 0) {
            int rank = scoreManager.getScoreRank(score);
            System.out.println("NEW HIGH SCORE! Rank #" + rank);
        }

        soundManager.stopAllSounds();
        soundManager.playLoopingSound("gameOver");
    }

    /** Khi chiến thắng */
    public void youWin() {
        gameState = 3;

        boolean isNewHighScore = scoreManager.isHighScore(score);
        scoreManager.addScore(score);

        if (isNewHighScore && score > 0) {
            int rank = scoreManager.getScoreRank(score);
            System.out.println("NEW HIGH SCORE! Rank #" + rank);
        }

        soundManager.stopAllSounds();
        soundManager.playLoopingSound("win");
    }

    /**
     * Xử lý va chạm giữa bóng, tường, gạch và paddle.
     */
    public void checkCollisions() {
        // Use a copy to avoid ConcurrentModificationException when powerups add balls
        List<Ball> ballsCopy = new ArrayList<>(balls);

        // Kiểm tra va chạm cho tất cả các bóng trong danh sách balls
        for (Ball b : ballsCopy) {

            // --- Va chạm với tường ---
            if (b.getX() <= 0) {
                b.setX(0);
                b.reverseX();
                soundManager.playSound("bounce");
            } else if (b.getX() + b.getWidth() >= WINDOW_WIDTH) {
                b.setX(WINDOW_WIDTH - b.getWidth());
                b.reverseX();
                soundManager.playSound("bounce");
            }

            if (b.getY() <= 0) {
                b.setY(0);
                b.reverseY();
                soundManager.playSound("bounce");
            }

            // --- Va chạm với gạch ---
            Iterator<Brick> iterator = levelManager.getBricks().iterator();
            while (iterator.hasNext()) {
                Brick brick = iterator.next();

                if (b.getHitbox().intersects(brick.getHitbox().getBounds2D())) {
                    // Tính toán vùng chồng lấn để xác định hướng phản xạ
                    double ballX = b.getX();
                    double ballY = b.getY();
                    int ballWidth = b.getWidth();
                    int ballHeight = b.getHeight();

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

                    double currentSpeed = Math.sqrt(b.getDx() * b.getDx() + b.getDy() * b.getDy());

                    brick.takeHit(); // Giảm máu gạch
                    soundManager.playSound("hit");

                    // Xác định hướng bật lại
                    if (minOverlap == overlapTop && b.getDy() > 0) {
                        b.setY(brickY - ballHeight);
                        b.reverseY();
                    } else if (minOverlap == overlapBottom && b.getDy() < 0) {
                        b.setY(brickY + brickHeight);
                        b.reverseY();
                    } else if (minOverlap == overlapLeft && b.getDx() > 0) {
                        b.setX(brickX - ballWidth);
                        b.reverseX();
                    } else if (minOverlap == overlapRight && b.getDx() < 0) {
                        b.setX(brickX + brickWidth);
                        b.reverseX();
                    } else {
                        b.reverseY();
                        b.reverseX();
                    }

                    // Giữ nguyên tốc độ sau khi đổi hướng
                    double newSpeed = Math.sqrt(b.getDx() * b.getDx() + b.getDy() * b.getDy());
                    if (newSpeed > 0) {
                        double speedRatio = currentSpeed / newSpeed;
                        b.setDx(b.getDx() * speedRatio);
                        b.setDy(b.getDy() * speedRatio);
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

                            // Tạo các PowerUp
                            double powerUpType = Math.random();
                            if (powerUpType < 0.25) {
                                // PowerUp mở rộng paddle
                                newPowerUp = new ExpandsPaddlePowerUp(
                                        brick.getX() + brick.getWidth() / 2.0,
                                        brick.getY() + brick.getHeight() / 2.0,
                                        spriteManager
                                );
                            } else if (powerUpType < 0.5) {
                                // PowerUp tăng tốc bóng
                                newPowerUp = new FastBallPowerUp(
                                        brick.getX() + brick.getWidth() / 2.0,
                                        brick.getY() + brick.getHeight() / 2.0,
                                        spriteManager
                                );
                            } else if (powerUpType < 0.75) {
                                // PowerUp làm chậm bóng
                                newPowerUp = new SlowBallPowerUp(
                                        brick.getX() + brick.getWidth() / 2.0,
                                        brick.getY() + brick.getHeight() / 2.0,
                                        spriteManager
                                );
                            } else if (powerUpType < 0.9) {
                                // PowerUp tăng thêm 2 bóng
                                newPowerUp = new SplitBallPowerUp(
                                        brick.getX() + brick.getWidth() / 2.0,
                                        brick.getY() + brick.getHeight() / 2.0,
                                        spriteManager
                                );
                            } else {
                                // PowerUp tăng thêm 1 mạng
                                newPowerUp = new ExtraLifePowerUp(
                                        brick.getX() + brick.getWidth() / 2.0,
                                        brick.getY() + brick.getHeight() / 2.0,
                                        spriteManager
                                );
                            }

                            // Thêm PowerUp mới vào danh sách powerUps
                            powerUps.add(newPowerUp);
                        }
                    }

                    break; // Tránh va chạm nhiều gạch cùng lúc
                }
            }

            // --- Va chạm với paddle ---
            if (b.getX() + b.getWidth() >= paddle.getX()
                    && b.getX() <= paddle.getX() + paddle.getWidth()
                    && b.getY() + b.getHeight() >= paddle.getY()
                    && b.getY() <= paddle.getY() + paddle.getHeight()
                    && b.getDy() > 0) {

                b.setY(paddle.getY() - b.getHeight());

                // Tính toán góc bật tùy theo vị trí bóng chạm paddle
                double ballCenterX = b.getX() + (double) b.getWidth() / 2;
                double paddleCenterX = paddle.getX() + (double) paddle.getWidth() / 2;
                double relativeHitX = (double)(ballCenterX - paddleCenterX) / (paddle.getWidth() / 2.0);
                relativeHitX = Math.max(-1.0, Math.min(1.0, relativeHitX));

                double bounceAngle = relativeHitX * Math.toRadians(60);
                double currentSpeed = Math.sqrt(b.getDx() * b.getDx() + b.getDy() * b.getDy());

                b.setDx(currentSpeed * Math.sin(bounceAngle));
                b.setDy(-currentSpeed * Math.cos(bounceAngle));
                soundManager.playSound("bounce");
            }
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

    // Getters
    public List<Brick> getBricks() {
        return levelManager.getBricks();
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getGameState() {
        return gameState;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }



    public static int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    /** Phóng bóng ra khỏi paddle */
    private void launchBall() {
        Ball mainBall = balls.get(0);
        ballAttached = false;
        mainBall.launch();
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
            // Chỉ chạy khi ở màn hình chờ (gameState 0) VÀ game chưa bắt đầu
            if (gameState == 0 && !isStartingGame) {
                isStartingGame = true; // Đặt cờ: Báo là game "đang bắt đầu"
                soundManager.playSound("press");

                // Tạo một Timer để trì hoãn
                int delay = 1700; // 2000 milliseconds = 2 giây

                // Tạo một Timer, sẽ chạy 1 lần sau 2 giây
                Timer timer = new Timer(delay, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        startGame(); // Gọi hàm startGame() sau 2 giây
                    }
                });

                timer.setRepeats(false); // Đảm bảo nó chỉ chạy một lần
                timer.start(); // Bắt đầu đếm ngược

            } else if (gameState == 2 || gameState == 3) {
                // Khởi động lại game (không cần chờ)
                startGame();
            } else if (gameState == 1 && ballAttached) {
                launchBall();
            } else if (gameState == 4 || gameState == 5) {
                gameState = 0;
            }
        } else if (key == KeyEvent.VK_L) {
            if (gameState == 0 || gameState == 2 || gameState == 3) {
                gameState = 4;
            }
        } else if (key == KeyEvent.VK_M) {
            if (gameState == 0 || gameState == 2 || gameState == 3) {
                gameState = 5;
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
                                final double TARGET_FPS = 60.0;
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