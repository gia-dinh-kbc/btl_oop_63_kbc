package MovableObject;  // Định nghĩa gói (package) chứa lớp này, là nơi các đối tượng có thể di chuyển

import java.awt.Color;  // Nhập lớp Color để sử dụng trong việc chọn màu sắc khi vẽ
import java.awt.Graphics;  // Nhập lớp Graphics để sử dụng vẽ các đối tượng đồ họa
import java.awt.Shape;  // Nhập lớp Shape để định nghĩa các hình dạng (như hình elip)
import java.awt.geom.Ellipse2D;  // Nhập lớp Ellipse2D để tạo ra hình elip trong đồ họa
import java.util.Random;  // Nhập lớp Random để tạo các giá trị ngẫu nhiên, dùng trong việc thay đổi hướng đi của bóng

// Lớp Ball kế thừa từ lớp MovableObject, giúp quả bóng có thể di chuyển trên màn hình
public class Ball extends MovableObject {
    private int speed = 2;  // Tốc độ di chuyển của quả bóng
    private Ellipse2D.Double hitbox;  // Vùng va chạm của quả bóng (dùng để kiểm tra va chạm)

    public int getSpeed() {
        return speed;
    }

    // Hàm khởi tạo cho quả bóng, nhận vào vị trí x, y và bán kính của bóng
    public Ball(int x, int y, int radius) {
        super(x, y, radius, radius);  // Gọi hàm khởi tạo của lớp cha MovableObject để khởi tạo vị trí và kích thước của bóng
        hitbox = new Ellipse2D.Double(x, y, width, height);  // Khởi tạo vùng va chạm (hitbox) của quả bóng dưới dạng hình elip
        reset(x, y);  // Đặt lại vị trí và hướng di chuyển của quả bóng khi khởi tạo
    }

    // Phương thức trả về hitbox của quả bóng, dùng để kiểm tra va chạm
    public Shape getHitbox() {
        return hitbox;  // Trả về vùng va chạm của quả bóng
    }

    // Phương thức đặt lại vị trí và hướng di chuyển của bóng
    public void reset(int x, int y) {
        int[] angles = {-3, -2, -1, 1, 2, 3};  // Mảng các góc di chuyển có thể có theo hướng x (ngẫu nhiên)
        this.x = x;  // Cập nhật vị trí x của bóng
        this.y = y;  // Cập nhật vị trí y của bóng
        Random random = new Random();  // Tạo đối tượng Random để sinh các giá trị ngẫu nhiên
        this.dx = angles[random.nextInt(angles.length)];  // Chọn ngẫu nhiên một góc từ mảng angles để di chuyển theo trục x
        this.dy = -speed;  // Đặt tốc độ di chuyển theo trục y (đi xuống ban đầu)
        hitbox.setFrame(x, y, width, height);  // Cập nhật lại vị trí của hitbox theo vị trí của quả bóng
    }

    // Phương thức thay đổi hướng di chuyển theo trục x (đảo ngược hướng bóng khi va chạm với tường bên trái hoặc phải)
    public void reverseX() {
        dx = -dx;  // Đảo ngược hướng di chuyển theo trục x
    }

    // Phương thức thay đổi hướng di chuyển theo trục y (đảo ngược hướng bóng khi va chạm với tường trên hoặc paddle)
    public void reverseY() {
        dy = -dy;  // Đảo ngược hướng di chuyển theo trục y
    }

    // Phương thức di chuyển bóng theo hướng dx và dy
    @Override
    public void move() {
        x += dx;  // Cập nhật vị trí x của bóng
        y += dy;  // Cập nhật vị trí y của bóng
        hitbox.setFrame(x, y, width, height);  // Cập nhật lại vị trí hitbox theo vị trí của quả bóng
    }

    // Phương thức cập nhật trạng thái bóng (di chuyển bóng)
    @Override
    public void update() {
        move();  // Gọi phương thức di chuyển bóng
    }

    // Phương thức vẽ bóng lên màn hình
    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);  // Đặt màu của quả bóng là đỏ
        g.fillOval(x, y, width, height);  // Vẽ quả bóng dưới dạng hình elip tại vị trí (x, y) với kích thước width, height
    }
}
