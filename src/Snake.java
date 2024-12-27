import java.awt.Point;
import java.util.LinkedList;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Snake {
    private LinkedList<Point> body; // Danh sách các điểm đại diện cho thân rắn
    private int direction; // Hướng di chuyển của rắn
    private int unitSize; // Kích thước đơn vị
    private Clip eatFoodClip;   // Clip cho âm thanh ăn mồi
    private Clip collisionClip;

    public Snake(int x, int y, int unitSize) {
        body = new LinkedList<>(); // Khởi tạo danh sách thân rắn
        body.add(new Point(x, y)); // Đặt đầu rắn ở vị trí ban đầu
        body.add(new Point(x - unitSize, y)); // Đặt tiếp thân rắn
        direction = KeyEvent.VK_RIGHT; // Hướng ban đầu của rắn là sang phải
        this.unitSize = unitSize;
        loadSounds(); // Lưu trữ kích thước đơn vị
    }

    private void loadSounds() {
        try {
            // Tải âm thanh ăn mồi
            AudioInputStream eatFoodStream = AudioSystem.getAudioInputStream(new File("C:\\dev\\snake_game - Sao chép\\assets\\eating (1).wav"));
            eatFoodClip = AudioSystem.getClip();
            eatFoodClip.open(eatFoodStream);

            // Tải âm thanh va chạm
            AudioInputStream collisionStream = AudioSystem.getAudioInputStream(new File("C:\\dev\\snake_game - Sao chép\\assets\\lose (1).wav"));
            collisionClip = AudioSystem.getClip();
            collisionClip.open(collisionStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Phát âm thanh  khi rắn ăn mồi 
    private void playEatFoodSound() {
        eatFoodClip.setFramePosition(0);  // Đặt lại vị trí âm thanh về đầu
        eatFoodClip.start();              // Phát âm thanh
    }

    // Phát âm thanh khi rắn va chạm
    private void playCollisionSound() {
        collisionClip.setFramePosition(0);  // Đặt lại vị trí âm thanh về đầu
        collisionClip.start();              // Phát âm thanh
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
            playCollisionSound();
            return true;
        }
        return false;
    }

    public boolean checkCollisionWithItself() {
        Point head = body.getFirst(); // Lấy đầu rắn
        // Kiểm tra va chạm với chính rắn
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                playCollisionSound();
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
