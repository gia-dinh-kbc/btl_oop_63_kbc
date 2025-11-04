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
                    getClass().getResource("/Resource/Backgrounds/leaderboard.jpg")
            ).getImage();
            menuBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/menu.jpg")
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
        // VẼ TIÊU ĐỀ
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

        // ======= VẼ DÒNG PHỤ 2: "Press L for Leaderboard" =======
        // ======= VẼ DÒNG PHỤ 2: "Press L for Leaderboard" (KHÔNG NHẤP NHÁY) =======
        String subtitle2 = "Press L for Leaderboard";
        FontMetrics fmSub2 = g2d.getFontMetrics(subtitleFont);
        int sub2Width = fmSub2.stringWidth(subtitle2);
        int sub2X = (GameManager.getWindowWidth() - sub2Width) / 2;
        int sub2Y = subY + 50;  // khoảng cách 50 px

// luôn vẽ ở alpha = 1.0f để không nhấp nháy
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

// viền đen 8 hướng
        g2d.setColor(Color.BLACK);
        g2d.drawString(subtitle2, sub2X - outline, sub2Y - outline);
        g2d.drawString(subtitle2, sub2X + outline, sub2Y - outline);
        g2d.drawString(subtitle2, sub2X - outline, sub2Y + outline);
        g2d.drawString(subtitle2, sub2X + outline, sub2Y + outline);
        g2d.drawString(subtitle2, sub2X - outline, sub2Y);
        g2d.drawString(subtitle2, sub2X + outline, sub2Y);
        g2d.drawString(subtitle2, sub2X, sub2Y - outline);
        g2d.drawString(subtitle2, sub2X, sub2Y + outline);

// chữ trắng
        g2d.setColor(Color.WHITE);
        g2d.drawString(subtitle2, sub2X, sub2Y);


// ======= VẼ DÒNG PHỤ 3: "Press M for Rule Menu" (KHÔNG NHẤP NHÁY) =======
        String subtitle3 = "Press M for Rule Menu"; // đổi M nếu bạn muốn giữ như trước
        FontMetrics fmSub3 = g2d.getFontMetrics(subtitleFont);
        int sub3Width = fmSub3.stringWidth(subtitle3);
        int sub3X = (GameManager.getWindowWidth() - sub3Width) / 2;
        int sub3Y = sub2Y + 50;  // tiếp tục cách 50 px

// luôn vẽ ở alpha = 1.0f để không nhấp nháy
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

// viền đen 8 hướng
        g2d.setColor(Color.BLACK);
        g2d.drawString(subtitle3, sub3X - outline, sub3Y - outline);
        g2d.drawString(subtitle3, sub3X + outline, sub3Y - outline);
        g2d.drawString(subtitle3, sub3X - outline, sub3Y + outline);
        g2d.drawString(subtitle3, sub3X + outline, sub3Y + outline);
        g2d.drawString(subtitle3, sub3X - outline, sub3Y);
        g2d.drawString(subtitle3, sub3X + outline, sub3Y);
        g2d.drawString(subtitle3, sub3X, sub3Y - outline);
        g2d.drawString(subtitle3, sub3X, sub3Y + outline);

// chữ trắng
        g2d.setColor(Color.WHITE);
        g2d.drawString(subtitle3, sub3X, sub3Y);


    }

    private void drawGame(Graphics g) {
        if (gameBackground != null) {
            g.drawImage(gameBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.WHITE);
        Font scoreFont = new Font("Press Start 2P", Font.BOLD, 12);
        g.setFont(scoreFont);
        g.drawString("SCORE: " + gameManager.getScore(), 10, 25);
        g.drawString("LIVES: " + gameManager.getLives(), GameManager.getWindowWidth() - 130, 25);
        g.drawString("LEVEL: " + gameManager.getLevelManager().getCurrentLevel(), GameManager.getWindowWidth() / 2 - 60, 25);

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
    }

    private void drawYouWin(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight());

        if (youWinBackground != null) {
            g.drawImage(youWinBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }

        // ======= VẼ TIÊU ĐỀ =======
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(titleFont);
        String title = "YOU WIN !";
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

        // Check if this is a high score
        boolean isHighScore = gameManager.getScoreManager().isHighScore(gameManager.getScore());

        if (isHighScore && gameManager.getScore() > 0) {
            g.setFont(new Font("Press Start 2P", Font.BOLD, 28));
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
        // VẼ TIÊU ĐỀ LEADERBOARD
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(titleFont);
        String title = "HIGH SCORES";
        FontMetrics fm = g2d.getFontMetrics(titleFont);
        int titleWidth = fm.stringWidth(title);
        int titleHeight = fm.getAscent();
        int x = (GameManager.getWindowWidth() - titleWidth) / 2 ;
        int y = (GameManager.getWindowHeight() / 2) - 4 * titleHeight ;

        int shadowOffset = 4;
        int glitchOffset = 6;


        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(title, x + shadowOffset, y + shadowOffset);


        g2d.setColor(new Color(0, 255, 255, 120));
        g2d.drawString(title, x - glitchOffset, y);


        g2d.setColor(new Color(255, 0, 255, 120)); // Màu Magenta hơi mờ
        g2d.drawString(title, x + glitchOffset, y);

        g2d.setColor(Color.WHITE);
        g2d.drawString(title, x, y);

        // VẼ DANH SÁCH ĐIỂM CAO
        g.setFont(new Font("Press Start 2P", Font.BOLD, 30));
        g.drawString(" ", GameManager.getWindowWidth() / 2 - 150, 300);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 20));
        List<Integer> highScores = gameManager.getScoreManager().getHighScores();

        if (highScores.isEmpty()) {
            g.drawString("No scores yet!", GameManager.getWindowWidth() / 2 - 140, 320);
        } else {
            for (int i = 0; i < highScores.size() && i < 5; i++) {
                String scoreText = (i + 1) + ". " + highScores.get(i) + " points";
                g.drawString(scoreText, GameManager.getWindowWidth() / 2 - 140, 330 + (i * 35));
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

        // VẼ TIÊU ĐỀ
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(titleFont);
        String title = "MENU";
        FontMetrics fm = g2d.getFontMetrics(titleFont);
        int titleWidth = fm.stringWidth(title);
        int titleHeight = fm.getAscent();
        int x = (GameManager.getWindowWidth() - titleWidth) / 2 ;
        int y = (GameManager.getWindowHeight() / 2) - 7 * titleHeight ;

        int shadowOffset = 4;
        int glitchOffset = 6;


        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(title, x + shadowOffset, y + shadowOffset);


        g2d.setColor(new Color(0, 255, 255, 120));
        g2d.drawString(title, x - glitchOffset, y);


        g2d.setColor(new Color(255, 0, 255, 120)); // Màu Magenta hơi mờ
        g2d.drawString(title, x + glitchOffset, y);

        g2d.setColor(Color.WHITE);
        g2d.drawString(title, x, y);

        // CONTROLS SECTION
        g.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        g.setColor(new Color(0,0,0));
        g.drawString("Controls:", 50, 200);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        g2d.setColor(Color.WHITE);
        g.drawString("← / A - Move left", 50, 230);
        g.drawString("→ / D - Move right", 50, 255);
        g.drawString("SPACE - Launch/Start", 50, 280);
        g.drawString("L - Leaderboard", 50, 305);
        g.drawString("M - Menu", 50, 330);

        // BRICKS SECTION
        g.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        g.setColor(new Color(0,0,0));
        g.drawString("Bricks:", 50, 380);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        int brickY = 410;
        int brickSpacing = 35;

        // Draw brick sprites with labels
        g.drawImage(spriteManager.getSprite("brick_pink"), 50, brickY - 20, 48, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Normal brick", 110, brickY);

        g.drawImage(spriteManager.getSprite("brick_blue"), 50, brickY + brickSpacing - 20, 48, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Strong brick", 110, brickY + brickSpacing);

        g.drawImage(spriteManager.getSprite("brick_purple"), 50, brickY + brickSpacing * 2 - 20, 48, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Indestructible brick", 110, brickY + brickSpacing * 2);

        // BALL SECTION
        g.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        g.setColor(new Color(0,0,0));
        g.drawString("Ball:", 380, 200);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        g.drawImage(spriteManager.getSprite("ball_red"), 380, 230, 24, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Breaks bricks", 420, 230);
        g.drawString("Bounces off paddle", 420, 255);
        g.drawString("and walls", 420, 280);

        // POWER-UPS
        g.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        g.setColor(new Color(0,0,0));
        g.drawString("Power-Ups:", 380, 380);

        g.setFont(new Font("Press Start 2P", Font.PLAIN, 12));
        int powerUpY = 410;
        int powerUpSpacing = 35;

        // Draw power-up icons (using brick sprites as placeholders with colors)
        // Expand Paddle (Purple-ish)
        g.drawImage(spriteManager.getSprite("ball_blue"), 380, powerUpY - 15, 24, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Expand Paddle", 420, powerUpY);

        // Fast Ball (Red)
        g.drawImage(spriteManager.getSprite("ball_green"), 380, powerUpY + powerUpSpacing - 15, 24, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Fast Ball", 420, powerUpY + powerUpSpacing);

        // Slow Ball (Blue)
        g.drawImage(spriteManager.getSprite("ball_brown"), 380, powerUpY + powerUpSpacing * 2 - 15, 24, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Slow Ball", 420, powerUpY + powerUpSpacing * 2);

        // Split Ball (Yellow)
        g.drawImage(spriteManager.getSprite("ball_red"), 380, powerUpY + powerUpSpacing * 3 - 15, 24, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Split Ball", 420, powerUpY + powerUpSpacing * 3);

        // Tăng mạng (Heart)
        g.drawImage(spriteManager.getSprite("heart"), 380, powerUpY + powerUpSpacing * 4 - 15, 24, 24, this);
        g2d.setColor(Color.WHITE);
        g.drawString("Increase heart", 420, powerUpY + powerUpSpacing * 4);

        // GAME INFO
        g.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        g.setColor(new Color(0,0,0));
        g.drawString("Score: 100 points per brick", GameManager.getWindowWidth() / 2 - 220, 570);
        g.drawString("Lives: Start with 3 lives", GameManager.getWindowWidth() / 2 - 200, 600);

        g.setFont(new Font("Press Start 2P", Font.BOLD, 16));
        g.setColor(new Color(25,143,214));
        g.drawString("Press SPACE", GameManager.getWindowWidth() / 2 - 150, 660);
        g.drawString("to return to start screen", GameManager.getWindowWidth() / 2 - 220, 685);
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