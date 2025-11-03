import static org.junit.jupiter.api.Assertions.*;

import GameManager.GameManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameManagerTest {
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new GameManager();
    }

    @Test
    void testStartGame() {
        gameManager.startGame();
        assertEquals(3, gameManager.getLives(), "Mạng sống phải bắt đầu từ 3 khi khởi động lại game");
        assertEquals(0, gameManager.getScore(), "Điểm phải bắt đầu từ 0 khi khởi động lại game");
    }

    @Test
    void testGameOver() {
        gameManager.setLives(0); // Thua cuộc
        gameManager.gameOver();
        assertEquals(2, gameManager.getGameState(), "Trạng thái game phải là 'game over' khi không còn mạng");
    }

    @Test
    void testNextLevel() {
        gameManager.setScore(100);  // Đảm bảo có điểm để qua level
        gameManager.nextLevel();
        assertEquals(2, gameManager.getCurrentLevel(), "Trạng thái game phải chuyển sang màn 2 khi phá hết gạch");
    }
}
