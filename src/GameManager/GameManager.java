package GameManager;  // Định nghĩa gói (package) chứa lớp GameManager

import Brick.Brick;  // Nhập lớp Brick từ gói Brick để sử dụng trong game
import Brick.NormalBrick;  // Nhập lớp NormalBrick từ gói Brick để sử dụng
import Brick.StrongBrick;  // Nhập lớp StrongBrick từ gói Brick để sử dụng
import MovableObject.Ball;  // Nhập lớp Ball từ gói MovableObject để sử dụng
import MovableObject.Paddle;  // Nhập lớp Paddle từ gói MovableObject để sử dụng
import PowerUp.PowerUp;  // Nhập lớp PowerUp từ gói PowerUp để sử dụng trong game

import javax.swing.JFrame;  // Nhập JFrame từ thư viện Swing để tạo cửa sổ hiển thị game
import java.awt.event.KeyEvent;  // Nhập KeyEvent để xử lý các sự kiện bàn phím
import java.awt.event.KeyListener;  // Nhập KeyListener để lắng nghe sự kiện bàn phím
import java.util.ArrayList;  // Nhập ArrayList để sử dụng danh sách động
import java.util.Iterator;  // Nhập Iterator để duyệt qua danh sách các đối tượng
import java.util.List;  // Nhập List để tạo và xử lý danh sách các đối tượng trong game

public class GameManager implements KeyListener {  // Lớp GameManager điều khiển logic của trò chơi, thực hiện lắng nghe sự kiện bàn phím

    // Khai báo các đối tượng của trò chơi
    private Paddle paddle = new Paddle(200, 450, 80, 15);  // Tạo đối tượng paddle (chảo) với vị trí và kích thước
    private Ball ball = new Ball(240, 300, 15);  // Tạo đối tượng bóng với vị trí và bán kính
    private List<Brick> bricks = new ArrayList<>();  // Danh sách các brick (gạch)
    private List<PowerUp> powerUps = new ArrayList<>();  // Danh sách các PowerUp (tăng sức mạnh)
    private int score = 0;  // Điểm số của người chơi
    private int lives = 3;  // Số mạng của người chơi
    private int gameState = 0;  // Trạng thái game (0: chưa bắt đầu, 1: đang chơi, 2: game over, 3: win)

    private static final int WINDOW_WIDTH = 500;  // Chiều rộng của cửa sổ game
    private static final int WINDOW_HEIGHT = 600;  // Chiều cao của cửa sổ game

    // Phương thức khởi động trò chơi
    public void startGame() {
        gameState = 1;  // Đặt trạng thái game thành "đang chơi"
        lives = 3;  // Khởi tạo lại số mạng
        score = 0;  // Khởi tạo lại điểm số
        paddle.setX(WINDOW_WIDTH / 2 - paddle.getWidth() / 2);  // Đặt lại vị trí của paddle (chảo)
        paddle.setY(450);  // Đặt lại vị trí của paddle (chảo)
        ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);  // Đặt lại vị trí và hướng của bóng
        bricks.clear();  // Xóa tất cả các brick cũ
        int width = WINDOW_WIDTH / 10;  // Tính chiều rộng của mỗi brick
        int height = width / 4;  // Tính chiều cao của mỗi brick
        for (int i = 0; i < 10; i++) {  // Vòng lặp tạo ra 10 brick theo chiều ngang
            for (int j = 1; j <= 3; j++) {  // Vòng lặp tạo ra 3 hàng brick
                if (j == 1) {  // Nếu là hàng đầu tiên, tạo brick mạnh
                    Brick brick = new StrongBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                    bricks.add(brick);  // Thêm brick mạnh vào danh sách
                } else {  // Nếu là hàng tiếp theo, tạo brick bình thường
                    Brick brick = new NormalBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                    bricks.add(brick);  // Thêm brick bình thường vào danh sách
                }
            }
        }
    }

    // Phương thức cập nhật trạng thái game mỗi khi game loop chạy
    public void updateGame() {
        if (gameState != 1) {  // Nếu game không phải ở trạng thái "đang chơi", không làm gì
            return;
        }

        ball.update();  // Cập nhật trạng thái của bóng
        paddle.update();  // Cập nhật trạng thái của paddle

        checkCollisions();  // Kiểm tra va chạm giữa các đối tượng trong game

        // Kiểm tra nếu bóng rơi ra ngoài màn hình
        if (ball.getY() > WINDOW_HEIGHT) {
            lives--;  // Giảm số mạng khi bóng rơi ra ngoài
            if (lives == 0) {  // Nếu không còn mạng, kết thúc game
                gameOver();
            } else {
                ball.reset(WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);  // Nếu còn mạng, đặt lại vị trí của bóng
            }
        }

        // Kiểm tra nếu tất cả các brick đã bị phá hủy, người chơi thắng
        if (bricks.isEmpty()) {
            youWin();
        }
    }

    // Phương thức khi game kết thúc
    public void gameOver() {
        gameState = 2;  // Đặt trạng thái game thành "game over"
    }

    // Phương thức khi người chơi thắng
    public void youWin() {
        gameState = 3;  // Đặt trạng thái game thành "win"
    }

    // Phương thức kiểm tra các va chạm trong game
    public void checkCollisions() {
        // Va chạm giữa bóng và tường
        if (ball.getX() <= 0 || ball.getX() + ball.getWidth() >= WINDOW_WIDTH) {
            ball.reverseX();  // Đảo ngược hướng di chuyển theo trục x
        }

        if (ball.getY() <= 0) {
            ball.reverseY();  // Đảo ngược hướng di chuyển theo trục y
        }

        // Va chạm giữa bóng và brick
        Iterator<Brick> iterator = bricks.iterator();  // Tạo một iterator để duyệt qua các brick
        while (iterator.hasNext()) {
            Brick brick = iterator.next();
            if (ball.getHitbox().intersects(brick.getHitbox().getBounds2D())) {  // Kiểm tra nếu bóng va chạm với brick
                brick.takeHit();  // Giảm điểm hit của brick
                ball.reverseY();  // Đảo ngược hướng bóng theo trục y
                if (brick.isDestroyed()) {  // Nếu brick bị phá hủy
                    score += 100;  // Thêm điểm cho người chơi
                    iterator.remove();  // Xóa brick khỏi danh sách
                }
                break;
            }
        }

        // Va chạm giữa bóng và paddle
        if (ball.getY() + ball.getWidth() >= paddle.getY()
                && ball.getY() < paddle.getY() + paddle.getHeight()
                && ball.getX() + ball.getWidth() > paddle.getX()
                && ball.getX() < paddle.getX() + paddle.getWidth()
                && ball.getDy() > 0) {

            ball.setY(paddle.getY() - ball.getWidth());  // Đặt lại vị trí của bóng sao cho không đi qua paddle

            int paddleCenter = paddle.getX() + paddle.getWidth() / 2;  // Tính trung tâm của paddle
            int ballCenter = ball.getX() + ball.getWidth() / 2;  // Tính trung tâm của bóng
            int hitOffset = ballCenter - paddleCenter;  // Tính độ lệch giữa trung tâm của bóng và paddle

            int unitPaddle = paddle.getWidth() / 10;


            double k = (double) hitOffset / unitPaddle;

            int dx = ball.getDx();
            int dy = ball.getDy();

            if (k <= -4) {
                dx = -5;
                dy = -1;
            } else if (k > -4 && k <= -3) {
                dx = -4;
                dy = -3;
            } else if (k > -3 && k <= -2) {
                dx = -3;
                dy = -4;
            } else if (k > -2 && k <= -1) {
                dx = -1;
                dy = -5;
            } else if ( k > -1 && k < 1) {
                dy = -Math.abs(dy);
            } else if (k >= 1 && k < 2) {
                dx = 1;
                dy = -5;
            } else if (k >= 2 && k < 3) {
                dx = 3;
                dy = -4;
            } else if (k >= 3 && k < 4) {
                dx = 4;
                dy = -3;
            } else {
                dx = 5;
                dy = -1;
            }

            ball.setDx(dx);  // Cập nhật hướng di chuyển theo trục x
            ball.setDy(dy);  // Cập nhật hướng di chuyển theo trục y
        }
    }

    // Các phương thức getter để truy cập các đối tượng và thông tin của game
    public List<Brick> getBricks() {
        return bricks;
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public Ball getBall() {
        return ball;
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getGameState() {
        return gameState;
    }

    // Các phương thức để xử lý các sự kiện bàn phím
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            paddle.moveLeft();  // Di chuyển paddle sang trái
        } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            paddle.moveRight();  // Di chuyển paddle sang phải
        } else if (key == KeyEvent.VK_SPACE) {
            if (gameState == 0 || gameState == 2 || gameState == 3) {
                startGame();  // Bắt đầu game mới khi nhấn phím Space
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A ||
                key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            paddle.stop();  // Dừng di chuyển khi người dùng bỏ phím
        }
    }

    // Phương thức main để chạy trò chơi
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
                () -> {
                    JFrame frame = new JFrame("Arkanoid");  // Tạo cửa sổ game
                    frame.setIgnoreRepaint(true);

                    GameManager gameManager = new GameManager();  // Tạo đối tượng GameManager để điều khiển game
                    Renderer renderer = new Renderer(gameManager);  // Tạo renderer để vẽ các đối tượng lên màn hình

                    // Tạo các brick
                    int width = WINDOW_WIDTH / 10;
                    int height = width / 4;
                    for (int i = 0; i < 10; i++) {
                        for (int j = 1; j <= 3; j++) {
                            if(j == 1){
                                Brick brick = new StrongBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                                gameManager.getBricks().add(brick);
                                continue;
                            }

                            Brick brick = new NormalBrick(i * width, j * height + 10 + 3 * height, width - 1, height - 1);
                            gameManager.getBricks().add(brick);
                        }
                    }

                    // Tạo cửa sổ game và xử lý sự kiện bàn phím
                    frame.add(renderer);
                    frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setResizable(false);
                    frame.setLocationRelativeTo(null);

                    renderer.addKeyListener(gameManager);  // Thêm lắng nghe sự kiện bàn phím
                    renderer.setFocusable(true);

                    frame.setVisible(true);
                    renderer.requestFocusInWindow();  // Yêu cầu renderer lấy focus từ cửa sổ

                    // Vòng lặp game
                    new Thread(
                            () -> {
                                final double TARGET_FPS = 40.0;
                                final long OPTIMAL_TIME = (long) (1000000000 / TARGET_FPS);
                                long lastLoopTime = System.nanoTime();

                                while (true) {
                                    long now = System.nanoTime();
                                    long updateLength = now - lastLoopTime;
                                    lastLoopTime = now;

                                    // Cập nhật game
                                    gameManager.updateGame();

                                    // Vẽ game
                                    renderer.repaint();

                                    // Thời gian chờ (sleep) để duy trì tốc độ FPS cố định
                                    long sleepTime = (OPTIMAL_TIME - updateLength) / 1000000;
                                    if (sleepTime > 0) {
                                        try {
                                            Thread.sleep(sleepTime);  // Chờ một khoảng thời gian để duy trì FPS ổn định
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            })
                            .start();
                });
    }
}
