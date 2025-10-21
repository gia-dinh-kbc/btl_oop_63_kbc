package GameManager;

import Brick.Brick;
import Brick.NormalBrick;
import Brick.StrongBrick;
import MovableObject.Ball;
import MovableObject.Paddle;
import PowerUp.PowerUp;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager implements KeyListener {
    private SpriteManager spriteManager = new SpriteManager();
    private SoundManager soundManager = new SoundManager();

    private Paddle paddle = new Paddle(0, 0, 128, 24, spriteManager);
    private Ball ball = new Ball(0, 0, 24, spriteManager);
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private int score = 0;
    private int lives = 3;
    private int gameState = 0;
    private boolean ballAttached = true;

    private static final int WINDOW_WIDTH = 640;
    private static final int WINDOW_HEIGHT = 960;

    public GameManager() {
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("start");
    }

    public void startGame() {
        gameState = 0;
        lives = 3;
        score = 0;
        ballAttached = true;
        paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
        paddle.setY(800);
        ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        ball.setDx(0);
        ball.setDy(0);
        bricks.clear();

        int width = 64;
        int height = 32;
        for (int i = 0; i < 10; i++) {
            for (int j = 1; j <= 3; j++) {
                if (j == 1) {
                    Brick brick = new StrongBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1, spriteManager);
                    bricks.add(brick);
                } else {
                    Brick brick = new NormalBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1, spriteManager);
                    bricks.add(brick);
                }
            }
        }

        gameState = 1;
    }

    public void updateGame() {
        if (gameState != 1) {
            return;
        }

        if (ballAttached) {
            double ballX = paddle.getX() + (paddle.getWidth() / 2) - (ball.getWidth() / 2);
            double ballY = paddle.getY() - ball.getHeight();
            ball.setX(ballX);
            ball.setY(ballY);
        } else {
            ball.update();
        }

        paddle.update();

        if (!ballAttached) {
            checkCollisions();
        }

        if (ball.getY() > WINDOW_HEIGHT && !ballAttached) {
            lives--;
            soundManager.playSound("lose");

            if (lives == 0) {
                gameOver();
            } else {
                ballAttached = true;
                ball.setDx(0);
                ball.setDy(0);
            }
        }

        if (bricks.isEmpty()) {
            youWin();
        }

        if (gameState == 1 && !soundManager.isPlaying("background")) {
            soundManager.stopAllSounds();
            soundManager.playLoopingSound("background");
        }
    }

    public void gameOver() {
        gameState = 2;
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("gameOver");
    }

    public void youWin() {
        gameState = 3;
        soundManager.stopAllSounds();
        soundManager.playLoopingSound("win");
    }

    public void checkCollisions() {
        // Ball and wall collisions
        if (ball.getX() <= 0) {
            ball.setX(0);  // Reposition inside bounds
            ball.reverseX();
            soundManager.playSound("bounce");
        } else if (ball.getX() + ball.getWidth() >= WINDOW_WIDTH) {
            ball.setX(WINDOW_WIDTH - ball.getWidth());
            ball.reverseX();
            soundManager.playSound("bounce");
        }

        if (ball.getY() <= 0) {
            ball.setY(0);  // Reposition inside bounds
            ball.reverseY();
            soundManager.playSound("bounce");
        }

        // Ball and brick collision
        Iterator<Brick> iterator = bricks.iterator();

        while (iterator.hasNext()) {
            Brick brick = iterator.next();

            if (ball.getHitbox().intersects(brick.getHitbox().getBounds2D())) {
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

                brick.takeHit();
                soundManager.playSound("hit");

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

                // Restore speed after reversing direction
                double newSpeed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
                if (newSpeed > 0) {
                    double speedRatio = currentSpeed / newSpeed;
                    ball.setDx(ball.getDx() * speedRatio);
                    ball.setDy(ball.getDy() * speedRatio);
                }

                if (brick.isDestroyed()) {
                    score += 100;
                    iterator.remove();
                    soundManager.playSound("break");
                }

                break;
            }
        }

        // Ball and paddle collision
        if (ball.getX() + ball.getWidth() >= paddle.getX()
                && ball.getX() <= paddle.getX() + paddle.getWidth()
                && ball.getY() + ball.getHeight() >= paddle.getY()
                && ball.getY() <= paddle.getY() + paddle.getHeight()
                && ball.getDy() > 0) {

            ball.setY(paddle.getY() - ball.getHeight());

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

    public List<Brick> getBricks() {
        return bricks;
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

    public static int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    private void launchBall() {
        ballAttached = false;
        ball.launch();
        soundManager.playSound("bounce");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            paddle.moveLeft();
        } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            paddle.moveRight();
        } else if (key == KeyEvent.VK_SPACE) {
            if (gameState == 0 || gameState == 2 || gameState == 3) {
                startGame();
            } else if (gameState == 1 && ballAttached) {
                launchBall();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A ||
                key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            paddle.stop();
        }
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        javax.swing.SwingUtilities.invokeLater(
            () -> {
                JFrame frame = new JFrame("Arkanoid");
                frame.setIgnoreRepaint(true);

                GameManager gameManager = new GameManager();
                Renderer renderer = new Renderer(gameManager);

                // Create handle inputs
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

                // shutdown hook to clean up audio resources
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    gameManager.getSoundManager().cleanup();
                }));

                // Game loop
                new Thread(
                        () -> {
                            final double TARGET_FPS = 60.0;
                            final long OPTIMAL_TIME = (long) (1000000000 / TARGET_FPS);
                            long lastLoopTime = System.nanoTime();

                            while (true) {
                                long now = System.nanoTime();
                                long updateLength = now - lastLoopTime;
                                lastLoopTime = now;

                                // Update game
                                gameManager.updateGame();

                                // Render
                                renderer.repaint();

                                // Sleep time
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