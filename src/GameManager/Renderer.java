package GameManager;

import Brick.Brick;
import MovableObject.Ball;
import java.awt.*;
import PowerUp.PowerUp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

/**
 * Renderer class is responsible for rendering the game graphics.
 */
public class Renderer extends JPanel {
    private GameManager gameManager;
    private Font titleFont = new Font("Press Start 2P", Font.BOLD, 40);
    private Font subtitleFont = new Font("Press Start 2P", Font.PLAIN, 20);
    private Font gameFont = new Font("Arial", Font.BOLD, 20);

    // Cache the images to prevent flickering
    private Image startScreenBackground;
    private Image gameBackground;
    private Image gameOverBackground;
    private Image youWinBackground;
    private Image leaderboardBackground;
    private Image menuBackground;

    // Biến hiệu ứng fade và neon
    private float subtitleAlpha = 1f;
    private boolean fadingOut = true;
    private float neonPhase = 0f;

    // constructor
    public Renderer(GameManager gameManager) {
        this.gameManager = gameManager;
        setBackground(Color.BLACK);
        setFocusable(true);
        setDoubleBuffered(true);
        // Load all images once during initialization
        loadImages();

        // Timer để cập nhật hiệu ứng fade và neon
        new javax.swing.Timer(40, e -> {
            // Fade in/out chữ
            if (fadingOut) {
                subtitleAlpha -= 0.05f;
                if (subtitleAlpha <= 0f) {
                    subtitleAlpha = 0f;
                    fadingOut = false;
                }
            } else {
                subtitleAlpha += 0.05f;
                if (subtitleAlpha >= 1f) {
                    subtitleAlpha = 1f;
                    fadingOut = true;
                }
            }

            // Cập nhật pha neon
            neonPhase += 0.1f;
            repaint();
        }).start();
    }

    private void loadImages() {
        try {
            startScreenBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/Gamestart.gif")
            ).getImage();

            // Use the JPEG for game background (already static)
            gameBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/Background.jpg")
            ).getImage();

            gameOverBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/Gameover.gif")
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ background
        if (startScreenBackground != null) {
            g2d.drawImage(startScreenBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }

        // ======= VẼ TIÊU ĐỀ (Cách 1: Glitch + Bóng đen) =======
        g2d.setFont(titleFont);
        String title = "ARKANOID GAME";
        FontMetrics fm = g2d.getFontMetrics(titleFont);
        int titleWidth = fm.stringWidth(title);
        int titleHeight = fm.getAscent();
        int x = (GameManager.getWindowWidth() - titleWidth) / 2;
        int y = (GameManager.getWindowHeight() / 2) - titleHeight;

        int shadowOffset = 4; // Độ dày và độ lệch của bóng đen (có thể thử 3, 4, 5)
        int glitchOffset = 6; // Độ lệch của các lớp màu (TĂNG LÊN để hiệu ứng glitch rõ hơn)

        // --- 1. VẼ BÓNG ĐEN (VẼ TRƯỚC HẾT để tạo nền) ---
        // Dùng màu đen hơi mờ để tạo độ sâu, nhưng không che khuất quá nhiều
        g2d.setColor(new Color(0, 0, 0, 180)); // Alpha 180/255 (khoảng 70% mờ)
        g2d.drawString(title, x + shadowOffset, y + shadowOffset);

        // --- 2. VẼ LỚP CYAN (LỆCH TRÁI) ---
        // Giữ độ mờ của lớp màu để nó không quá choáng ngợp
        g2d.setColor(new Color(0, 255, 255, 120)); // Màu Cyan hơi mờ
        g2d.drawString(title, x - glitchOffset, y);

        // --- 3. VẼ LỚP MAGENTA (LỆCH PHẢI) ---
        g2d.setColor(new Color(255, 0, 255, 120)); // Màu Magenta hơi mờ
        g2d.drawString(title, x + glitchOffset, y);

        // --- 4. VẼ LỚP TRẮNG CHÍNH (Ở GIỮA) ---
        // Lớp này sẽ rõ nhất
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, x, y);


        // ======= VẼ DÒNG PHỤ "Press SPACE to start" =======
        g2d.setFont(subtitleFont);
        String subtitle = "Press SPACE to start";
        FontMetrics fm2 = g2d.getFontMetrics(subtitleFont);
        int subWidth = fm2.stringWidth(subtitle);
        int subHeight = fm2.getAscent();
        int subX = (GameManager.getWindowWidth() - subWidth) / 2;
        int subY = y + 220;

        int outline = 2;

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, subtitleAlpha));

        g2d.setColor(Color.BLACK);
        g2d.drawString(subtitle, subX - outline, subY - outline);
        g2d.drawString(subtitle, subX + outline, subY - outline);
        g2d.drawString(subtitle, subX - outline, subY + outline);
        g2d.drawString(subtitle, subX + outline, subY + outline);
        g2d.drawString(subtitle, subX - outline, subY);
        g2d.drawString(subtitle, subX + outline, subY);
        g2d.drawString(subtitle, subX, subY - outline);
        g2d.drawString(subtitle, subX, subY + outline);

        g2d.setColor(Color.WHITE);
        g2d.drawString(subtitle, subX, subY);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, subtitleAlpha));
        g2d.setColor(new Color(255, 255, 255));
        g2d.drawString(subtitle, subX, subY);


    }

    private void drawGame(Graphics g) {
        if (gameBackground != null) {
            g.drawImage(gameBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.WHITE);
        Font scoreFont = new Font("Press Start 2P", Font.BOLD, 12);
        g.setFont(scoreFont);
        g.drawString("SCORE " + gameManager.getScore(), 10, 25);
        g.drawString("LIVES: " + gameManager.getLives(), GameManager.getWindowWidth() - 80, 25);
        g.drawString("LEVEL : " + gameManager.getLevelManager().getCurrentLevel(), GameManager.getWindowWidth() / 2 - 40, 25);

        List<Ball> ballsSnapshot = new ArrayList<>(gameManager.getBalls());
        List<Brick> bricksSnapshot = new ArrayList<>(gameManager.getBricks());
        List<PowerUp> powerUpsSnapshot = new ArrayList<>(gameManager.getPowerUps());

        // Render using snapshots (safe from concurrent modification)
        for (Ball b : ballsSnapshot) {
            b.render(g);
        }

        gameManager.getPaddle().render(g);

        for (Brick brick : bricksSnapshot) {
            brick.render(g);
        }

        for (PowerUp powerUp : powerUpsSnapshot) {
            powerUp.render(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight());

        if (gameOverBackground != null) {
            g.drawImage(gameOverBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        // ======= VẼ TIÊU ĐỀ =======
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(titleFont);
        String title = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics(titleFont);
        int titleWidth = fm.stringWidth(title);
        int titleHeight = fm.getAscent();
        int x = (GameManager.getWindowWidth() - titleWidth) / 2;
        int y = (GameManager.getWindowHeight() / 2) - titleHeight;


        // Hiệu ứng đổ bóng
        g2d.setColor(new Color(32, 15, 217, 255)); // xanh blue
        for (int i = 1; i <= 5; i++) {
            g2d.drawString(title, x - i, y - i);
            g2d.drawString(title, x + i, y + i);
        }

        // Hiệu ứng gradient chuyển màu
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(
                0, y - titleHeight,
                new Color(60, 181, 255),
                0, y,
                new Color(198, 247, 255)
        );
        g2d.setPaint(gradient);
        g2d.drawString(title, x, y);

// ======= VẼ ĐIỂM SỐ CUỐI CÙNG VỚI HIỆU ỨNG GLOW =======
        Graphics2D g2d2 = (Graphics2D) g;
        Font scoreFont = new Font("Press Start 2P", Font.BOLD, 20);
        g.setFont(scoreFont);

        Color mainColor = Color.WHITE;
        Color glowColor = new Color(113, 6, 150, 100);
        String scoreText = "Final Score: " + gameManager.getScore();
        String restartText = "Press SPACE to restart";

        int scoreX = GameManager.getWindowWidth() / 2 - 160;
        int scoreY = GameManager.getWindowHeight() / 2 + 70;
        int restartX = GameManager.getWindowWidth() / 2 - 210;
        int restartY = GameManager.getWindowHeight() / 2 + 100;

        int glowOffset = 5;

        g2d.setColor(glowColor);
        g2d.drawString(scoreText, scoreX - glowOffset, scoreY);
        g2d.drawString(scoreText, scoreX + glowOffset, scoreY);
        g2d.drawString(scoreText, scoreX, scoreY - glowOffset);
        g2d.drawString(scoreText, scoreX, scoreY + glowOffset);

        g2d.setColor(mainColor);
        g2d.drawString(scoreText, scoreX, scoreY);

        boolean isRestartVisible = (System.currentTimeMillis() % 1000) < 500;

        if (isRestartVisible) {
            // Vẽ viền (glow)
            g2d.setColor(glowColor);
            g2d.drawString(restartText, restartX - glowOffset, restartY);
            g2d.drawString(restartText, restartX + glowOffset, restartY);
            g2d.drawString(restartText, restartX, restartY - glowOffset);
            g2d.drawString(restartText, restartX, restartY + glowOffset);

            // Vẽ chữ chính
            g2d.setColor(mainColor);
            g2d.drawString(restartText, restartX, restartY);
        }

        // Check if this is a high score
        boolean isHighScore = gameManager.getScoreManager().isHighScore(gameManager.getScore());

        if (isHighScore && gameManager.getScore() > 0) {
            g.setColor(new Color(0,0,0));
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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight());

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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight());

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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight());

        if (menuBackground != null) {
            g.drawImage(menuBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }

        // Get sprite manager for rendering sprites
        SpriteManager spriteManager = new SpriteManager();

        g.setColor(Color.WHITE);
        g.setFont(titleFont);
        g.drawString("MENU", GameManager.getWindowWidth() / 2 - 70, 80);

        // CONTROLS SECTION
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Controls:", 50, 140);

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("← / A - Move left", 50, 170);
        g.drawString("→ / D - Move right", 50, 195);
        g.drawString("SPACE - Launch/Start", 50, 220);
        g.drawString("L - Leaderboard", 50, 245);
        g.drawString("M - Menu", 50, 270);

        // BRICKS SECTION
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

        // BALL SECTION
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Ball:", 340, 140);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawImage(spriteManager.getSprite("ball_red"), 340, 155, 24, 24, this);
        g.drawString("Breaks bricks", 375, 173);
        g.drawString("Bounces off paddle", 340, 200);
        g.drawString("and walls", 340, 220);

        // POWER-UPS
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