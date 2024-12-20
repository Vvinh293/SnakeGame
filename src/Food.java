import java.awt.*;
import javax.swing.*;

public class Food {
    private Point position; // Vị trí của thức ăn
    private final int unitSize;
    private Image foodImage; // Hình ảnh thức ăn

    public Food(int unitSize, int boardWidth, int boardHeight) {
        this.unitSize = unitSize;

        // Tải hình ảnh thức ăn
        foodImage = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\fruit.png").getImage();
        generateNewFood(boardWidth, boardHeight); // Khởi tạo vị trí thức ăn
    }

    public void setPosition(Point position) {
        this.position = position; // Cập nhật vị trí của thức ăn
    }

    public Point getPosition() {
        return position; // Trả về vị trí thức ăn
    }

    public Image getFoodImage() {
        return foodImage; // Trả về hình ảnh thức ăn
    }

    // Tạo thức ăn mới trong phạm vi bảng
    public void generateNewFood(int boardWidth, int boardHeight) {
        position = new Point(
            (int) (Math.random() * (boardWidth / unitSize)) * unitSize, // Xác định tọa độ x
            (int) (Math.random() * (boardHeight / unitSize)) * unitSize  // Xác định tọa độ y
        );
    }
}
