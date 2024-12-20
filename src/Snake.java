import java.awt.Point;
import java.util.LinkedList;
import java.awt.event.*;

public class Snake {
    private LinkedList<Point> body; // Danh sách các điểm đại diện cho thân rắn
    private int direction; // Hướng di chuyển của rắn
    private int unitSize; // Kích thước đơn vị

    public Snake(int x, int y, int unitSize) {
        body = new LinkedList<>(); // Khởi tạo danh sách thân rắn
        body.add(new Point(x, y)); // Đặt đầu rắn ở vị trí ban đầu
        body.add(new Point(x - unitSize, y)); // Đặt tiếp thân rắn
        direction = KeyEvent.VK_RIGHT; // Hướng ban đầu của rắn là sang phải
        this.unitSize = unitSize; // Lưu trữ kích thước đơn vị
    }

    public LinkedList<Point> getBody() {
        return body; // Trả về danh sách các điểm thân rắn
    }

    public void setDirection(int newDirection) {
        // Ngăn không cho rắn đi ngược lại với hướng của nó
        if ((newDirection == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) ||
            (newDirection == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) ||
            (newDirection == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) ||
            (newDirection == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP)) {
            direction = newDirection; // Cập nhật hướng mới
        }
    }

    public void move() {
        Point head = body.getFirst(); // Lấy đầu rắn
        Point newHead = null;

        // Di chuyển đầu rắn theo hướng hiện tại
        switch (direction) {
            case KeyEvent.VK_LEFT:
                newHead = new Point(head.x - unitSize, head.y);
                break;
            case KeyEvent.VK_RIGHT:
                newHead = new Point(head.x + unitSize, head.y);
                break;
            case KeyEvent.VK_UP:
                newHead = new Point(head.x, head.y - unitSize);
                break;
            case KeyEvent.VK_DOWN:
                newHead = new Point(head.x, head.y + unitSize);
                break;
        }

        // Thêm đầu mới vào đầu danh sách và loại bỏ đuôi
        body.addFirst(newHead);
        body.removeLast();
    }

    public boolean checkCollisionWithWall(int boardWidth, int boardHeight) {
        Point head = body.getFirst(); // Lấy đầu rắn
        // Kiểm tra va chạm với các bức tường
        if (head.x < 0 || head.x >= boardWidth || head.y < 0 || head.y >= boardHeight) {
            return true;
        }
        return false;
    }

    public boolean checkCollisionWithItself() {
        Point head = body.getFirst(); // Lấy đầu rắn
        // Kiểm tra va chạm với chính rắn
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void grow() {
        // Thêm một đoạn vào rắn mỗi khi ăn thức ăn
        body.addLast(body.getLast());
    }

    public int getDirection() {
        return direction; // Trả về hướng hiện tại của rắn
    }
}
