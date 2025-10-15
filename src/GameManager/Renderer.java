package GameManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

public class Renderer extends JPanel {
    private GameManager gameManager;
    private Font titleFont = new Font("Arial", Font.BOLD, 40);
    private Font subtitleFont = new Font("Arial", Font.PLAIN, 20);
    private Font gameFont = new Font("Arial", Font.BOLD, 20);

    public Renderer(GameManager gameManager) {
        this.gameManager = gameManager;
        setBackground(Color.BLACK);
        setFocusable(true);
        setDoubleBuffered(true);
    }

    private void drawStartScreen(Graphics g) {
        Image startScreenBackground = new ImageIcon(
                getClass().getResource("/Resource/Backgrounds/startScreenBackground.gif")
        ).getImage();
        g.drawImage(startScreenBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        g.setColor(Color.WHITE);
        g.setFont(titleFont);
        g.drawString("Arkanoid Game", GameManager.getWindowWidth() / 2 - 160, GameManager.getWindowHeight() / 2);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to start", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2 + 100);
    }

    private void drawGame(Graphics g) {
        Image startScreenBackground = new ImageIcon(
                getClass().getResource("/Resource/Backgrounds/gameBackground.jpeg")
        ).getImage();
        g.drawImage(startScreenBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        g.setColor(Color.WHITE);
        g.setFont(gameFont);
        g.drawString("Score: " + gameManager.getScore(), 1, 20);
        g.drawString("Lives: " + gameManager.getLives(), GameManager.getWindowWidth() - 80, 20);

        gameManager.getBall().render(g);
        gameManager.getPaddle().render(g);

        for (var brick : gameManager.getBricks()) {
            brick.render(g);
        }
    }

    private void drawGameOver(Graphics g) {
        Image startScreenBackground = new ImageIcon(
                getClass().getResource("/Resource/Backgrounds/gameOverBackground.gif")
        ).getImage();
        g.drawImage(startScreenBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        g.setColor(Color.RED);
        g.setFont(titleFont);
        g.drawString("GAME OVER", GameManager.getWindowWidth() / 2 - 120, GameManager.getWindowHeight() / 2);
        g.setColor(Color.BLACK);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to restart", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2 + 100);
    }

    private void drawYouWin(Graphics g) {
        Image startScreenBackground = new ImageIcon(
                getClass().getResource("/Resource/Backgrounds/youWinBackground.gif")
        ).getImage();
        g.drawImage(startScreenBackground, 0, 0, GameManager.getWindowWidth(), GameManager.getWindowHeight(), this);
        g.setColor(Color.YELLOW);
        g.setFont(titleFont);
        g.drawString("YOU WIN!", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2);
        g.setColor(Color.BLACK);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to restart", GameManager.getWindowWidth() / 2 - 100, GameManager.getWindowHeight() / 2 + 100);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

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
