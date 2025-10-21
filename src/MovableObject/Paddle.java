package MovableObject;  // Định nghĩa gói (package) chứa lớp Paddle

import PowerUp.PowerUp;  // Nhập lớp PowerUp từ gói PowerUp để có thể sử dụng trong lớp Paddle

import java.awt.Color;  // Nhập lớp Color để sử dụng cho việc đặt màu sắc
import java.awt.Graphics;  // Nhập lớp Graphics để vẽ đối tượng lên màn hình
import java.awt.Shape;  // Nhập lớp Shape để định nghĩa các hình dạng, như hình chữ nhật
import java.awt.geom.Rectangle2D;  // Nhập lớp Rectangle2D để tạo ra các hình chữ nhật trong đồ họa

// Lớp Paddle kế thừa từ MovableObject, điều khiển sự di chuyển của paddle (chảo) trong game
public class Paddle extends MovableObject {
    private int speed = 5;  // Tốc độ di chuyển của paddle
    private PowerUp currentPowerUp;  // Biến lưu trữ PowerUp hiện tại (dùng cho tương lai)
    private Rectangle2D.Double hitbox;  // Hitbox (vùng va chạm) của paddle để kiểm tra va chạm

    // Hàm khởi tạo paddle, nhận vào các tham số x, y, width, height
    public Paddle(int x, int y, int width, int height) {
        super(x, y, width, height);  // Gọi hàm khởi tạo của lớp cha MovableObject để khởi tạo vị trí và kích thước của paddle
        hitbox = new Rectangle2D.Double(x, y, width, height);  // Khởi tạo hitbox dưới dạng hình chữ nhật với vị trí và kích thước đã cho
    }

    // Phương thức trả về hitbox của paddle, dùng để kiểm tra va chạm với các đối tượng khác
    public Shape getHitbox() {
        return hitbox;  // Trả về hitbox của paddle
    }

    // Phương thức di chuyển paddle sang trái
    public void moveLeft() {
        dx = -speed;  // Thiết lập tốc độ di chuyển theo hướng trái (dx âm)
    }

    // Phương thức di chuyển paddle sang phải
    public void moveRight() {
        dx = speed;  // Thiết lập tốc độ di chuyển theo hướng phải (dx dương)
    }

    // Phương thức dừng di chuyển paddle (khi người dùng không ấn phím điều khiển nữa)
    public void stop() {
        dx = 0;  // Dừng di chuyển paddle bằng cách đặt dx bằng 0
    }

    // Phương thức áp dụng PowerUp cho paddle (chưa được triển khai trong mã này)
    public void applyPowerUp() {
        // Chức năng này có thể được triển khai trong tương lai nếu cần
    }

    // Phương thức di chuyển paddle theo hướng dx đã được tính toán
    @Override
    public void move() {
        x += dx;  // Cập nhật vị trí x của paddle

        // Nếu paddle vượt ra ngoài biên trái của cửa sổ, giữ paddle tại vị trí x = 0
        if (x < 0) {
            x = 0;
        }

        // Nếu paddle vượt ra ngoài biên phải của cửa sổ, giữ paddle tại vị trí x = 500 - width
        if (x + width > 500) {
            x = 500 - width;
        }

        // Cập nhật lại hitbox của paddle sau mỗi lần di chuyển
        hitbox.setRect(x, y, width, height);
    }

    // Phương thức cập nhật trạng thái của paddle, gọi phương thức move() để di chuyển paddle
    @Override
    public void update() {
        move();  // Cập nhật lại vị trí paddle sau mỗi lần cập nhật
    }

    // Phương thức vẽ paddle lên màn hình
    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLUE);  // Đặt màu cho paddle là màu xanh
        g.fillRect(x, y, width, height);  // Vẽ paddle dưới dạng hình chữ nhật với vị trí (x, y) và kích thước (width, height)
    }
}
