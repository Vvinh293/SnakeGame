import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH = 1024;  // Chiều rộng bảng trò chơi
    private final int BOARD_HEIGHT = 800; // Chiều cao bảng trò chơi
    private final int UNIT_SIZE = 25;     // Kích thước mỗi đơn vị (pixel) của rắn
    private Snake snake;                   // Đối tượng Snake đại diện cho rắn
    private Food food;                     // Đối tượng Food đại diện cho thức ăn
    private boolean running = false;       // Trạng thái của trò chơi (chạy hoặc dừng)
    private Timer timer;                   // Đồng hồ thời gian cập nhật trò chơi
    private Timer countdownTimer;          // Đồng hồ thời gian đếm ngược trước khi bắt đầu trò chơi
    private int countdown = 3;             // Số giây còn lại trước khi trò chơi bắt đầu
    private boolean countdownStarted = false; // Kiểm tra nếu đếm ngược đã bắt đầu

    private ImageIcon backgroundIcon;      // Hình nền của trò chơi         

    public SnakeGame() {
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT)); 
        this.setFocusable(true);
        this.addKeyListener(this);

        // Tải hình nền từ tệp
        backgroundIcon = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\taoanhdep_text_to_img_27883.jpeg"); 
        
        // Khởi tạo rắn và thức ăn
        snake = new Snake(UNIT_SIZE * 3, UNIT_SIZE * 3, UNIT_SIZE); 
        food = new Food(UNIT_SIZE, BOARD_WIDTH, BOARD_HEIGHT); 
        
        // Đồng hồ thời gian cho cập nhật trò chơi
        timer = new Timer(100, this); 
        
        // Đồng hồ thời gian đếm ngược
        countdownTimer = new Timer(1000, e  -> {
            countdown--; // Giảm 1 giây mỗi khi tick
            if (countdown == 0) {
                countdownTimer.stop(); // Dừng đồng hồ thời gian đếm ngược
                running = true; // Bắt đầu trò chơi
            }
            repaint(); // Vẽ lại màn hình
        });
        timer.start(); // Bắt đầu đồng hồ thời gian trò chơi
    }

    private void returnToStartScreen() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this); 
        if (frame != null) {
            SnakeGameStartScreen.main(null); // Khởi động màn hình bắt đầu của trò chơi
            frame.dispose(); // Đóng khung cửa sổ hiện tại
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over! Score: " + (snake.getBody().size() - 1);

        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        int response = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            resetGame(); // Khởi động lại trò chơi
        } else {
            returnToStartScreen(); // Trở lại màn hình bắt đầu
        }
    }

    public void resetGame() {
        snake = new Snake(UNIT_SIZE * 3, UNIT_SIZE * 3, UNIT_SIZE); // Tạo lại rắn mới
        food = new Food(UNIT_SIZE, BOARD_WIDTH, BOARD_HEIGHT); // Tạo lại thức ăn mới
        running = false; // Đặt trạng thái trò chơi về false
        countdown = 3; // Đặt lại đếm ngược
        countdownStarted = false;
        countdownTimer.restart(); // Khởi động lại đồng hồ đếm ngược
        repaint(); // Vẽ lại màn hình
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            snake.move(); // Di chuyển rắn
            if (snake.checkCollisionWithWall(BOARD_WIDTH, BOARD_HEIGHT) || snake.checkCollisionWithItself()) {
                running = false;
                gameOver(null); // Kết thúc trò chơi
            }
            checkFood(); // Kiểm tra nếu rắn ăn thức ăn
        }
        repaint(); // Vẽ lại màn hình
    }

    private void checkFood() {
        if (snake.getBody().getFirst().equals(food.getPosition())) {
            snake.grow(); // Mở rộng rắn khi ăn thức ăn
            generateFood(); // Tạo thức ăn mới
        }
    }

    private void generateFood() {
        Point newFoodPosition;
        do {
            // Tạo vị trí ngẫu nhiên cho thức ăn
            newFoodPosition = new Point(
                (int) (Math.random() * (BOARD_WIDTH / UNIT_SIZE)) * UNIT_SIZE,
                (int) (Math.random() * (BOARD_HEIGHT / UNIT_SIZE)) * UNIT_SIZE
            );
        } while (isFoodCollidingWithObstacles(newFoodPosition));

        food.setPosition(newFoodPosition); // Đặt vị trí thức ăn mới
    }

    private boolean isFoodCollidingWithObstacles(Point foodPosition) {
        for (int x = 0; x < BOARD_WIDTH; x += UNIT_SIZE) {
            if (foodPosition.equals(new Point(x, 0)) ||
                foodPosition.equals(new Point(x, BOARD_HEIGHT - UNIT_SIZE))) {
                return true; // Thức ăn va chạm với biên trên hoặc dưới
            }
        }
        for (int y = 0; y < BOARD_HEIGHT; y += UNIT_SIZE) {
            if (foodPosition.equals(new Point(0, y)) ||
                foodPosition.equals(new Point(BOARD_WIDTH - UNIT_SIZE, y))) {
                return true; // Thức ăn va chạm với biên trái hoặc phải
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vẽ hình nền
        backgroundIcon.paintIcon(this, g, 0, 0);

        if (running) {
            // Vẽ rắn
            for (int i = 0; i < snake.getBody().size(); i++) {
                Point p = snake.getBody().get(i);
                if (i == 0) {
                    // Vẽ đầu rắn theo hướng
                    switch (snake.getDirection()) {
                        case KeyEvent.VK_LEFT:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\snake_head_RtL.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                        case KeyEvent.VK_RIGHT:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\snake2.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                        case KeyEvent.VK_UP:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\snake_head_up.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                        case KeyEvent.VK_DOWN:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\snake_head_down.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                    }
                } else if (i == snake.getBody().size() - 1) {
                    // Lấy hướng di chuyển của đoạn thân rắn ngay trước đuôi
                    int previousDirection = snake.getDirection();
                    if (i - 1 >= 0) {
                        Point previousSegment = snake.getBody().get(i - 1);
                        int dx = previousSegment.x - p.x;
                        int dy = previousSegment.y - p.y;

                        if (dx > 0) previousDirection = KeyEvent.VK_RIGHT;
                        else if (dx < 0) previousDirection = KeyEvent.VK_LEFT;
                        else if (dy > 0) previousDirection = KeyEvent.VK_DOWN;
                        else if (dy < 0) previousDirection = KeyEvent.VK_UP;
                    }

                    // Vẽ đuôi rắn theo hướng của đoạn thân rắn ngay trước đuôi
                    switch (previousDirection) {
                        case KeyEvent.VK_LEFT:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\tail_RtL.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                        case KeyEvent.VK_RIGHT:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\last_tail2.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                        case KeyEvent.VK_UP:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\tail_up.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                        case KeyEvent.VK_DOWN:
                            g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\tail_down.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                            break;
                    }
                } else {
                    // Vẽ thân rắn
                    g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\body2.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                }
            }

            // Vẽ thức ăn
            g.drawImage(food.getFoodImage(), food.getPosition().x, food.getPosition().y, UNIT_SIZE, UNIT_SIZE, this);

            // Hiển thị điểm
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
            g.drawString("Score: " + (snake.getBody().size() - 1), 20, 20);
        } else {
            if (!countdownStarted) {
                countdownStarted = true;
                countdownTimer.start();
            }

            // Hiển thị đếm ngược
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
            String countdownText = "Starting in: " + countdown;
            FontMetrics metrics = g.getFontMetrics();
            int x = (BOARD_WIDTH - metrics.stringWidth(countdownText)) / 2;
            int y = (BOARD_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(countdownText, x, y);
        }
    }

    private Image loadImage(String path) {
        return new ImageIcon(path).getImage();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int newDirection = e.getKeyCode();
        snake.setDirection(newDirection);

        if (newDirection == KeyEvent.VK_R && !running) {
            resetGame(); // Khởi động lại trò chơi nếu nhấn R
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
