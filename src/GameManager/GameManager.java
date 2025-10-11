package GameManager;

import Brick.BrickFactory;
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
import static Brick.BrickFactory.NORM_BRICK;
import static Brick.BrickFactory.STRONG_BRICK;

import static java.lang.Math.abs;

public class GameManager implements KeyListener {
    private Paddle paddle = new Paddle(200, 450, 80, 15);
    private Ball ball = new Ball(240, 300, 15);
    private List<Brick> bricks = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();
    private int score = 0;
    private int lives = 3;
    private int gameState = 0;

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 600;

    public void startGame() {
        gameState = 1;
        lives = 3;
        score = 0;
        paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);
        paddle.setY(450);
        ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
        bricks.clear();
        int width = WINDOW_WIDTH / 10;
        int height = width / 4;
        for (int i = 0; i < 10; i++) {
            for (int j = 1; j <= 3; j++) {
                if (j == 1) {
                    Brick brick = BrickFactory.createBrick(STRONG_BRICK, i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                    bricks.add(brick);
                } else {
                    Brick brick = BrickFactory.createBrick(NORM_BRICK, i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                    bricks.add(brick);
                }
            }
        }
    }

    public void updateGame() {
        if (gameState != 1) {
            return;
        }

        ball.update();
        paddle.update();

        checkCollisions();

        if (ball.getY() > WINDOW_HEIGHT) {
            lives--;
            if (lives == 0) {
                gameOver();
            } else {
                ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
            }
        }

        if (bricks.isEmpty()) {
            youWin();
        }
    }

    public void gameOver() {
        gameState = 2;
    }

    public void youWin() {
        gameState = 3;
    }

    public void checkCollisions() {
        // Ball and wall collisions
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= WINDOW_WIDTH) {
            ball.reverseX();
        }

        if (ball.getY() <= 0) {
            ball.reverseY();
        }

        // Ball and brick collision
        Iterator<Brick> iterator = bricks.iterator();
        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            if (ball.getHitbox().intersects(brick.getHitbox().getBounds2D())) {
                brick.takeHit();
                ball.reverseY();
                if (brick.isDestroyed()) {
                    score += 100;
                    iterator.remove();
                }
                break;
            }
        }

        // Ball and paddle collision
        if (ball.getY() + ball.getWidth() >= paddle.getY()
                && ball.getY() < paddle.getY() + paddle.getHeight()
                && ball.getX() + ball.getWidth() > paddle.getX()
                && ball.getX() < paddle.getX() + paddle.getWidth()
                && ball.getDy() > 0) {

            ball.setY(paddle.getY() - ball.getWidth());

            int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            int ballCenter = ball.getX();
            int hitOffset = ballCenter - paddleCenter;

            int bounceAngle = 4;

            int dx = hitOffset / bounceAngle;

            if (dx == 0) {
                dx = (Math.random() < 0.5) ? -2 : 2;
            }

            int dy = -abs(ball.getDy());

            int maxSpeed = 3;
            if (dx > maxSpeed) {
                dx = maxSpeed;
            }

            if (dx < -maxSpeed) {
                dx = -maxSpeed;
            }

            ball.setDx(dx);
            ball.setDy(dy);
            System.out.println("Ball dx: " + ball.getDx() + ", dy: " + ball.getDy());
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
    javax.swing.SwingUtilities.invokeLater(
        () -> {
            JFrame frame = new JFrame("Arkanoid");
            frame.setIgnoreRepaint(true);

            GameManager gameManager = new GameManager();
            Renderer renderer = new Renderer(gameManager);
            // Create bricks
            int width = WINDOW_WIDTH / 10;
            int height = width / 4;
            for (int i = 0; i < 10; i++) {
                for (int j = 1; j <= 3; j++) {
                    if(j == 1){
                        Brick brick = new StrongBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                        gameManager.getBricks().add(brick);
                        continue;
                    }

                    Brick brick = new NormalBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                    gameManager.getBricks().add(brick);
                }
            }

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