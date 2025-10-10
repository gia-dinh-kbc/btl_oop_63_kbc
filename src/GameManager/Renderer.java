package GameManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

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
        g.setColor(Color.WHITE);
        g.setFont(titleFont);
        g.drawString("Arkanoid Game", 95, 300);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to start", 150, 350);
    }

    private void drawGame(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(gameFont);
        g.drawString("Score: " + gameManager.getScore(), 5, 20);
        g.drawString("Lives: " + gameManager.getLives(), 400, 20);

        gameManager.getBall().render(g);
        gameManager.getPaddle().render(g);

        for (var brick : gameManager.getBricks()) {
            brick.render(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(titleFont);
        g.drawString("GAME OVER", 135, 300);
        g.setColor(Color.WHITE);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to restart", 150, 350);
    }

    private void drawYouWin(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(titleFont);
        g.drawString("YOU WIN!", 140, 300);
        g.setColor(Color.WHITE);
        g.setFont(subtitleFont);
        g.drawString("Press SPACE to restart", 150, 350);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        // Enable anti-aliasing
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
