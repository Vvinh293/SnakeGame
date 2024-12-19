import java.awt.*;
import javax.swing.*;

public class Food {
    private Point position;  // Vị trí của thức ăn
    private final int unitSize;
    private final int boardWidth;
    private final int boardHeight;
    private Image foodImage;  // Hình ảnh thức ăn

    public Food(int unitSize, int boardWidth, int boardHeight) {
        this.unitSize = unitSize;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;

        // Tải hình ảnh thức ăn
        foodImage = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\fruit.png").getImage();
        generateNewFood(boardWidth, boardHeight); // Khởi tạo vị trí thức ăn
    }
    // Cập nhật lớp Food
    public void setPosition(Point position) {
    this.position = position; // Cập nhật vị trí của thức ăn
}

    // Lấy vị trí thức ăn
    public Point getPosition() {
        return position;
    }

    // Lấy hình ảnh thức ăn
    public Image getFoodImage() {
        return foodImage;
    }

    // Tạo thức ăn mới trong phạm vi bảng
    public void generateNewFood(int boardWidth, int boardHeight) {
        position = new Point(
            (int) (Math.random() * (boardWidth / unitSize)) * unitSize,
            (int) (Math.random() * (boardHeight / unitSize)) * unitSize
        );
    }
}
