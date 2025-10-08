package GameManager;

import Brick.*;
import MovableObject.Ball;
import MovableObject.Paddle;
import PowerUp.PowerUp;

import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

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
    }

    public void updateGame() {
        if (gameState != 1) {
            return;
        }

        ball.update();
        paddle.update();

        checkCollisions();
    }

    public void checkCollisions() {
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= WINDOW_WIDTH) {
            ball.reverseX();
        }
        if (ball.getY() <= 0) {
            ball.reverseY();
        }

        // Paddle and brick collision
        if (ball.getHitbox().intersects(paddle.getHitbox().getBounds2D())) {
            ball.setY(paddle.getY() - ball.getHeight());
            ball.reverseY();
        }

        for(int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (ball.getHitbox().intersects(brick.getHitbox().getBounds2D())) {
                brick.takeHit();
                ball.reverseY();
                if (brick.isDestroyed()) {
                    score += 100;
                    i--;
                    bricks.remove(brick);
                }
                break;
            }
        }


    }


    public void gameOver() {
        gameState = 2;
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
            if (gameState == 0) {
                startGame();
            } else if (gameState == 2) {
                gameOver();
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
                    long lastTime = System.nanoTime();
                    double nsPerTick = 1000000000.0 / 60.0;
                    double delta = 0;

                    while (true) {
                      long now = System.nanoTime();
                      delta += (now - lastTime) / nsPerTick;
                      lastTime = now;

                      if (delta >= 1) {
                        gameManager.updateGame();
                        renderer.repaint();
                        delta--;
                      }

                      try {
                        Thread.sleep(2);
                      } catch (InterruptedException ignored) {
                      }
                    }
                  })
              .start();
        });
    }
}