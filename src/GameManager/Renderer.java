package GameManager;

import MovableObject.Ball;
import java.awt.*;
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

            gameBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/Background.jpg")
            ).getImage();

            gameOverBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/Gameover.gif")
            ).getImage();

            youWinBackground = new ImageIcon(
                    getClass().getResource("/Resource/Backgrounds/youWinBackground.gif")
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

        // ======= VẼ TIÊU ĐỀ =======
        g2d.setFont(titleFont);
        String title = "ARKANOID GAME";
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

        // ======= VẼ DÒNG PHỤ "Press SPACE to start" =======
        g2d.setFont(subtitleFont);
        String subtitle = "Press SPACE to start";
        FontMetrics fm2 = g2d.getFontMetrics(subtitleFont);
        int subWidth = fm2.stringWidth(subtitle);
        int subHeight = fm2.getAscent();
        int subX = (GameManager.getWindowWidth() - subWidth) / 2;
        int subY = y + 100;

        // Hiệu ứng fade sáng tối cho chữ
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, subtitleAlpha));
        g2d.setColor(new Color(250, 252, 255));
        g2d.drawString(subtitle, subX, subY);

        // ======= KHUNG NEON NHẤP NHÁY =======
        int paddingX = 30;
        int paddingY = 15;
        int boxX = subX - paddingX / 2;
        int boxY = subY - subHeight - paddingY / 3;
        int boxWidth = subWidth + paddingX;
        int boxHeight = subHeight + paddingY;

// Màu neon cố định (không đổi màu)
        Color neonColor = new Color(60, 181, 255);


// === TÔ NỀN KHUNG PHÁT SÁNG MỜ ===
        Color innerGlow = new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), 60);
        g2d.setColor(innerGlow);
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

// Hiệu ứng glow bằng cách vẽ nhiều lớp viền mờ
        for (int i = 4; i >= 1; i--) {
            float alpha = 0.1f * i;
            g2d.setColor(new Color(neonColor.getRed(), neonColor.getGreen(), neonColor.getBlue(), (int) (255 * alpha)));
            g2d.setStroke(new BasicStroke(i * 2f));
            g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        }

// Viền chính neon
        g2d.setColor(neonColor);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

    }

    private void drawGame(Graphics g) {
        if (gameBackground != null) {
            g.drawImage(gameBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.WHITE);
        Font scoreFont = new Font("Press Start 2P", Font.BOLD, 12);
        g.setFont(scoreFont);
        g.drawString("SCORE: " + gameManager.getScore(), 10, 25);
        g.drawString("LIVES: " + gameManager.getLives(), GameManager.getWindowWidth() - 120, 25);

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

        int glowOffset = 5; // Độ dày/lan toả của viền glow

        // --- 4. Vẽ lớp VIỀN (GLOW) trước ---
        // Vẽ chữ 4 lần ở 4 góc (trên, dưới, trái, phải) bằng màu mờ
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
    }

    private void drawYouWin(Graphics g) {
        if (youWinBackground != null) {
            g.drawImage(youWinBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        }
        g.setColor(Color.YELLOW);
        g.setFont(titleFont);
        g.drawString("YOU WIN!", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2);
        g.setColor(Color.BLACK);
        g.setFont(subtitleFont);
        g.drawString("Final Score: " + gameManager.getScore(), GameManager.getWindowWidth() / 2 - 80, GameManager.getWindowHeight() / 2 + 50);
        g.drawString("Press SPACE to restart", GameManager.getWindowWidth() / 2 - 110, GameManager.getWindowHeight() / 2 + 100);
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
        }
    }
}
