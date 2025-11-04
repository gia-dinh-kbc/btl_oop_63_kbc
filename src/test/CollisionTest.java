import GameManager.GameManager;
import MovableObject.Ball;
import Brick.Brick;
import Brick.NormalBrick;
import GameManager.SpriteManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionTest {

    private SpriteManager spriteManager;
    private GameManager gameManager;
    private Ball ball;
    private Brick brick;

    // Cài đặt trước khi mỗi test case chạy
    @BeforeEach
    public void setUp() {
        // Khởi tạo SpriteManager để lấy ảnh cho các đối tượng
        SpriteManager spriteManager = SpriteManager.getInstance();
        gameManager = new GameManager();

        // Khởi tạo bóng tại vị trí (100, 100) và bán kính 20
        ball = new Ball(100, 100, 20, spriteManager);
        ball.setDx(1);  // Di chuyển ngang
        ball.setDy(1);  // Di chuyển theo chiều dọc

        // Khởi tạo gạch tại vị trí (110, 110) với kích thước 50x20
        brick = new NormalBrick(110, 110, 50, 20, spriteManager);
    }

    @Test
    public void testBallBrickCollision() {
        // Đảm bảo va chạm giữa bóng và gạch ban đầu
        assertTrue(ball.getHitbox().intersects(brick.getHitbox().getBounds2D()));

        // Kiểm tra va chạm và thực hiện cập nhật
        gameManager.checkCollisions();  // Chạy kiểm tra va chạm

        // Kiểm tra nếu gạch bị phá sau va chạm
        assertTrue(brick.isDestroyed(), "Brick should be destroyed after collision");
    }
}
