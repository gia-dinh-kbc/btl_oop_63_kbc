import GameManager.SpriteManager;
import MovableObject.Ball;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BallTest {
    private SpriteManager spriteManager;
    private Ball ball;

    @BeforeEach
    public void setUp() {
        // Khởi tạo SpriteManager (hoặc có thể mock nếu cần thiết)
        spriteManager = new SpriteManager();
        ball = new Ball(0, 0, 24, spriteManager);
    }

    @Test
    public void testBallMovement() {
        ball.move();
        assertNotNull(ball.getHitbox());  // Kiểm tra xem hitbox có phải là null không
    }

    @Test
    public void testIncreaseSpeed() {
        double initialSpeed = ball.getSpeed();
        ball.increaseSpeed();
        assertTrue(ball.getSpeed() > initialSpeed);  // Kiểm tra xem tốc độ có tăng lên không
    }

    @Test
    public void testDecreaseSpeed() {
        ball.decreaseSpeed();
        assertTrue(ball.getSpeed() < 3);  // Kiểm tra xem tốc độ có giảm xuống không
    }

    @Test
    public void testResetSpeed() {
        ball.resetSpeed();
        assertEquals(3, ball.getSpeed());  // Kiểm tra xem tốc độ có quay lại mặc định không
    }
}
