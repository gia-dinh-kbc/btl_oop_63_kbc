package GameManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Renderer extends JPanel {
    private GameManager gameManager;

    public Renderer(GameManager gameManager) {
        this.gameManager = gameManager;
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    private void drawStartScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Arkanoid Game", 95, 300);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press SPACE to start", 150, 350);
    }

    private void drawGame(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + gameManager.getScore(), 10, 20);
        g.drawString("Lives: " + gameManager.getLives(), 430, 20);

        gameManager.getBall().render(g);
        gameManager.getPaddle().render(g);

        for (var brick : gameManager.getBricks()) {
            brick.render(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("GAME OVER", 135, 300);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press SPACE to restart", 150, 350);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameManager.getGameState() == 0) {
            drawStartScreen(g);
        } else if (gameManager.getGameState() == 1) {
            drawGame(g);
        }
        if (gameManager.getGameState() == 2) {
            drawGameOver(g);
        }
    }
}
