package GameManager;

import MovableObject.Ball;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

public class Renderer extends JPanel {
    private GameManager gameManager;
    private Font titleFont = new Font("Arial", Font.BOLD, 40);
    private Font subtitleFont = new Font("Arial", Font.PLAIN, 20);
    private Font gameFont = new Font("Arial", Font.BOLD, 20);

    // Cache the images to prevent flickering
    private Image startScreenBackground;
    private Image gameBackground;
    private Image gameOverBackground;
    private Image youWinBackground;
    private Image leaderboardBackground;
    private Image menuBackground;

    public Renderer(GameManager gameManager) {
        this.gameManager = gameManager;
        setBackground(Color.BLACK);
        setFocusable(true);
        setDoubleBuffered(true);

        // Load all images once during initialization
        loadImages();
    }

    private void loadImages() {
        try {
            startScreenBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/startScreenBackground.gif")
            ).getImage();

            // Use the JPEG for game background (already static)
            gameBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/gameBackground.jpeg")
            ).getImage();

            gameOverBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/gameOverBackground.gif")
            ).getImage();
            youWinBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/youWinBackground.gif")
            ).getImage();
            leaderboardBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/leaderboard.gif")
            ).getImage();
            menuBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/menu.gif")
            ).getImage();

            System.out.println("Background images loaded successfully!");
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawStartScreen(Graphics g) {
        if (startScreenBackground != null) {
            g.drawImage(startScreenBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.WHITE);
        g.setFont(titleFont);
        g.drawString("Arkanoid Game", GameManager.getWindowWidth() / 2 - 160, GameManager.getWindowHeight() / 2);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to start", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2 + 100);
        g.drawString("Press L for Leaderboard", GameManager.getWindowWidth() / 2 - 120, GameManager.getWindowHeight() / 2 + 150);
        g.drawString("Press M for rule menu", GameManager.getWindowWidth() / 2 - 115, GameManager.getWindowHeight() / 2 + 200);
    }

    private void drawGame(Graphics g) {
        if (gameBackground != null) {
            g.drawImage(gameBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.WHITE);
        g.setFont(gameFont);
        g.drawString("Score: " + gameManager.getScore(), 10, 25);
        g.drawString("Lives: " + gameManager.getLives(), GameManager.getWindowWidth() - 80, 25);
        g.drawString("Level: " + gameManager.getLevelManager().getCurrentLevel(), GameManager.getWindowWidth() / 2 - 40, 25);

        for (Ball b : gameManager.getBalls()) {
            b.render(g);
        }
        gameManager.getPaddle().render(g);

        for (var brick : gameManager.getBricks()) {
            brick.render(g);
        }

        for (var powerUp : gameManager.getPowerUps()) {
            powerUp.render(g);
        }
    }

    private void drawGameOver(Graphics g) {
        if (gameOverBackground != null) {
            g.drawImage(gameOverBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }

        // Check if this is a high score
        boolean isHighScore = gameManager.getScoreManager().isHighScore(gameManager.getScore());

        if (isHighScore && gameManager.getScore() > 0) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("NEW HIGH SCORE!", GameManager.getWindowWidth() / 2 - 140, GameManager.getWindowHeight() / 2 + 130);
        }

        g.setColor(Color.WHITE);
        g.setFont(subtitleFont);
        g.drawString("Final Score: " + gameManager.getScore(), GameManager.getWindowWidth() / 2 - 80, GameManager.getWindowHeight() / 2 + 180);

        g.setColor(Color.WHITE);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to restart", GameManager.getWindowWidth() / 2 - 110, GameManager.getWindowHeight() / 2 + 260);
        g.drawString("Press L for Full Leaderboard", GameManager.getWindowWidth() / 2 - 130, GameManager.getWindowHeight() / 2 + 300);
    }

    private void drawYouWin(Graphics g) {
        if (youWinBackground != null) {
            g.drawImage(youWinBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.YELLOW);
        g.setFont(titleFont);
        g.drawString("YOU WIN!", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2 - 100);

        // Check if this is a high score
        boolean isHighScore = gameManager.getScoreManager().isHighScore(gameManager.getScore());

        if (isHighScore && gameManager.getScore() > 0) {
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("NEW HIGH SCORE!", GameManager.getWindowWidth() / 2 - 120, GameManager.getWindowHeight() / 2 - 50);
        }

        g.setColor(Color.BLACK);
        g.setFont(subtitleFont);
        g.drawString("Final Score: " + gameManager.getScore(), GameManager.getWindowWidth() / 2 - 80, GameManager.getWindowHeight() / 2);

        g.setColor(Color.BLACK);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to restart", GameManager.getWindowWidth() / 2 - 110, GameManager.getWindowHeight() / 2 + 260);
        g.drawString("Press L for Full Leaderboard", GameManager.getWindowWidth() / 2 - 130, GameManager.getWindowHeight() / 2 + 300);
    }

    private void drawLeaderboard(Graphics g) {
        if (leaderboardBackground != null) {
            g.drawImage(leaderboardBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        g.drawString("LEADERBOARD", GameManager.getWindowWidth() / 2 - 120, 150);

        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("HIGH SCORES", GameManager.getWindowWidth() / 2 - 80, 250);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        List<Integer> highScores = gameManager.getScoreManager().getHighScores();

        if (highScores.isEmpty()) {
            g.drawString("No scores yet!", GameManager.getWindowWidth() / 2 - 70, 320);
        } else {
            for (int i = 0; i < highScores.size() && i < 5; i++) {
                String scoreText = (i + 1) + ". " + highScores.get(i) + " points";
                g.drawString(scoreText, GameManager.getWindowWidth() / 2 - 70, 320 + (i * 35));
            }
        }

        g.setFont(subtitleFont);
    }

    private void drawMenu(Graphics g) {
        if (menuBackground != null) {
            g.drawImage(menuBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }

        // Get sprite manager for rendering sprites
        SpriteManager spriteManager = new SpriteManager();

        g.setColor(Color.WHITE);
        g.setFont(titleFont);
        g.drawString("MENU", GameManager.getWindowWidth() / 2 - 70, 80);

        // === CONTROLS SECTION ===
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Controls:", 50, 140);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("← / A - Move left", 50, 170);
        g.drawString("→ / D - Move right", 50, 195);
        g.drawString("SPACE - Launch/Start", 50, 220);
        g.drawString("L - Leaderboard", 50, 245);
        g.drawString("M - Menu", 50, 270);

        // === BRICKS SECTION ===
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Bricks:", 50, 320);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        int brickY = 350;
        int brickSpacing = 35;

        // Draw brick sprites with labels
        g.drawImage(spriteManager.getSprite("brick_pink"), 50, brickY - 20, 48, 24, this);
        g.drawString("Pink (normal brick) - 1 hit", 110, brickY);

        g.drawImage(spriteManager.getSprite("brick_blue"), 50, brickY + brickSpacing - 20, 48, 24, this);
        g.drawString("Blue (strong brick) - 2 hit", 110, brickY + brickSpacing);

        g.drawImage(spriteManager.getSprite("brick_purple"), 50, brickY + brickSpacing * 2 - 20, 48, 24, this);
        g.drawString("Purple (indestructible brick)", 110, brickY + brickSpacing * 2);

        // === BALL SECTION ===
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Ball:", 340, 140);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawImage(spriteManager.getSprite("ball_red"), 340, 155, 24, 24, this);
        g.drawString("Breaks bricks", 375, 173);
        g.drawString("Bounces off paddle", 340, 200);
        g.drawString("and walls", 340, 220);

        // === POWER-UPS SECTION ===
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Power-Ups:", 340, 270);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        int powerUpY = 300;
        int powerUpSpacing = 35;

        // Draw power-up icons (using brick sprites as placeholders with colors)
        // Expand Paddle (Purple-ish)
        g.drawImage(spriteManager.getSprite("ball_blue"), 340, powerUpY - 15, 24, 24, this);
        g.drawString("Expand Paddle", 380, powerUpY);

        // Fast Ball (Red)
        g.drawImage(spriteManager.getSprite("ball_green"), 340, powerUpY + powerUpSpacing - 15, 24, 24, this);
        g.drawString("Fast Ball", 380, powerUpY + powerUpSpacing);

        // Slow Ball (Blue)
        g.drawImage(spriteManager.getSprite("ball_brown"), 340, powerUpY + powerUpSpacing * 2 - 15, 24, 24, this);
        g.drawString("Slow Ball", 380, powerUpY + powerUpSpacing * 2);

        // Split Ball (Yellow)
        g.drawImage(spriteManager.getSprite("ball_red"), 340, powerUpY + powerUpSpacing * 3 - 15, 24, 24, this);
        g.drawString("Split Ball", 380, powerUpY + powerUpSpacing * 3);

        // GAME INFO
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.setColor(Color.CYAN);
        g.drawString("Score: 100 points per brick", GameManager.getWindowWidth() / 2 - 130, 540);
        g.drawString("Lives: Start with 3 lives", GameManager.getWindowWidth() / 2 - 110, 570);

        g.setFont(subtitleFont);
        g.setColor(Color.YELLOW);
        g.drawString("Press SPACE to return to start screen", GameManager.getWindowWidth() / 2 - 180, 630);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (gameManager.getGameState() == 0) {
            drawStartScreen(g2d);
        } else if (gameManager.getGameState() == 1) {
            drawGame(g2d);
        } else if (gameManager.getGameState() == 2) {
            drawGameOver(g2d);
        } else if (gameManager.getGameState() == 3) {
            drawYouWin(g2d);
        } else if (gameManager.getGameState() == 4) {
            drawLeaderboard(g2d);
        } else if (gameManager.getGameState() == 5) {
            drawMenu(g2d);
        }
    }
}