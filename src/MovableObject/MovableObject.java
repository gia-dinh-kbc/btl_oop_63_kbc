package MovableObject;  // Định nghĩa gói (package) chứa lớp MovableObject, dùng cho các đối tượng có thể di chuyển

import GameManager.GameObject;  // Nhập lớp GameObject từ gói GameManager, lớp cha của MovableObject

// Lớp MovableObject là lớp trừu tượng, kế thừa từ GameObject, dùng để định nghĩa các đối tượng có thể di chuyển
public abstract class MovableObject extends GameObject {
    protected int dx;  // Tốc độ di chuyển theo trục X (ngang) của đối tượng
    protected int dy;  // Tốc độ di chuyển theo trục Y (dọc) của đối tượng

    // Phương thức getter cho dx, trả về tốc độ di chuyển theo trục X
    public int getDx() {
        return dx;
    }

    // Phương thức setter cho dx, cho phép thay đổi tốc độ di chuyển theo trục X
    public void setDx(int dx) {
        this.dx = dx;
    }

    // Phương thức getter cho dy, trả về tốc độ di chuyển theo trục Y
    public int getDy() {
        return dy;
    }

    // Phương thức setter cho dy, cho phép thay đổi tốc độ di chuyển theo trục Y
    public void setDy(int dy) {
        this.dy = dy;
    }

    // Hàm khởi tạo của lớp MovableObject, nhận vào vị trí x, y và kích thước width, height
    public MovableObject(int x, int y, int width, int height){
        super(x, y, width, height);  // Gọi hàm khởi tạo của lớp cha GameObject để thiết lập vị trí và kích thước
    }

    // Phương thức trừu tượng move(), các lớp con phải triển khai để di chuyển đối tượng
    public abstract void move();  // Ví dụ: lớp Ball hoặc Paddle sẽ triển khai move() riêng để di chuyển theo dx, dy
}
