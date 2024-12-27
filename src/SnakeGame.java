import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH = 1024;  // Chiều rộng bảng trò chơi
    private final int BOARD_HEIGHT = 800; // Chiều cao bảng trò chơi
    private final int UNIT_SIZE = 25;     // Kích thước mỗi đơn vị (pixel) của rắn
    private Snake snake;                  // Đối tượng Snake đại diện cho rắn
    private Food food;                    // Đối tượng Food đại diện cho thức ăn
    private LinkedList<Point> obstacles; // Danh sách các chướng ngại vật
    private boolean running = false;      // Trạng thái của trò chơi (chạy hoặc dừng)
    private Timer timer;                  // Đồng hồ thời gian cập nhật trò chơi
    private Timer countdownTimer;         // Đồng hồ thời gian đếm ngược trước khi bắt đầu trò chơi
    private int countdown = 3;            // Số giây còn lại trước khi trò chơi bắt đầu
    private boolean countdownStarted = false; // Kiểm tra nếu đếm ngược đã bắt đầu
    private Clip eatFoodClip;   // Clip cho âm thanh ăn mồi
    private Clip collisionClip;  // Clip cho âm thanh va chạm

    private ImageIcon backgroundIcon;     // Hình nền của trò chơi
    private Image obstacleImage;          // Hình ảnh chướng ngại vật

    public SnakeGame() {
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);

        // Tải âm thanh
        loadSounds();

        // Tải hình nền từ tệp
        backgroundIcon = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\taoanhdep_text_to_img_27883.jpeg");

        // Khởi tạo rắn, thức ăn, và chướng ngại vật
        snake = new Snake(UNIT_SIZE * 3, UNIT_SIZE * 3, UNIT_SIZE);
        food = new Food(UNIT_SIZE, BOARD_WIDTH, BOARD_HEIGHT);
        obstacles = new LinkedList<>();
        obstacleImage = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\bomb_PNG38.png").getImage();
        generateObstacles(8 ); // Sinh 5 chướng ngại vật ngẫu nhiên

        // Đồng hồ thời gian cho cập nhật trò chơi
        timer = new Timer(100, this);

        // Đồng hồ thời gian đếm ngược
        countdownTimer = new Timer(1000, e -> {
            countdown--; // Giảm 1 giây mỗi khi tick
            if (countdown == 0) {
                countdownTimer.stop(); // Dừng đồng hồ thời gian đếm ngược
                running = true; // Bắt đầu trò chơi
            }
            repaint(); // Vẽ lại màn hình
        });
        timer.start(); // Bắt đầu đồng hồ thời gian trò chơi
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

    private boolean isPointCollidingWithSnake(Point point) {
        for (Point snakePart : snake.getBody()) {
            if (snakePart.equals(point)) {
                playCollisionSound();
                return true; // Va chạm với thân rắn
            }
        }
        return false;
    }

    private boolean checkCollisionWithObstacles() {
        Point head = snake.getBody().getFirst();
        for (Point obstacle : obstacles) {
            Rectangle obstacleRect = new Rectangle(obstacle.x, obstacle.y, 50, 50);
            if (obstacleRect.contains(head)) {
                playCollisionSound();
                return true; // Va chạm với chướng ngại vật
            }
        }
        return false;
    }

    private void generateObstacles(int count) {
        obstacles.clear(); // Xóa các chướng ngại vật cũ
        for (int i = 0; i < count; i++) {
            Point obstaclePosition;
            do {
                // Tạo vị trí ngẫu nhiên
                obstaclePosition = new Point(
                    (int) (Math.random() * (BOARD_WIDTH / UNIT_SIZE)) * UNIT_SIZE,
                    (int) (Math.random() * (BOARD_HEIGHT / UNIT_SIZE)) * UNIT_SIZE
                );
            } while (obstaclePosition.equals(food.getPosition()) || isPointCollidingWithSnake(obstaclePosition));

            obstacles.add(obstaclePosition); // Thêm chướng ngại vật
        }
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
            resetGame();
        } else {
            returnToStartScreen();
        }
    }

    public void resetGame() {
        snake = new Snake(UNIT_SIZE * 3, UNIT_SIZE * 3, UNIT_SIZE); // Tạo lại rắn mới
        food = new Food(UNIT_SIZE, BOARD_WIDTH, BOARD_HEIGHT); // Tạo lại thức ăn mới
        generateObstacles(5); // Sinh lại chướng ngại vật
        running = false; // Đặt trạng thái trò chơi về false
        countdown = 3; // Đặt lại đếm ngược
        countdownStarted = false;
        countdownTimer.restart(); // Khởi động lại đồng hồ đếm ngược
        repaint(); // Vẽ lại màn hình
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            snake.move();
            if (snake.checkCollisionWithWall(BOARD_WIDTH, BOARD_HEIGHT) || 
                snake.checkCollisionWithItself() || 
                checkCollisionWithObstacles()) {
                running = false;
                gameOver(null);
            }
            checkFood();
        }
        repaint();
    }

    private void checkFood() {
        if (snake.getBody().getFirst().equals(food.getPosition())) {
            snake.grow();
            generateFood();
            playEatFoodSound();  // Phát âm thanh ăn mồi
        }
    }


    private void generateFood() {
        Point newFoodPosition;
        do {
            newFoodPosition = new Point(
                (int) (Math.random() * (BOARD_WIDTH / UNIT_SIZE)) * UNIT_SIZE,
                (int) (Math.random() * (BOARD_HEIGHT / UNIT_SIZE)) * UNIT_SIZE
            );
        } while (isFoodCollidingWithObstacles(newFoodPosition) || isPointCollidingWithSnake(newFoodPosition));

        food.setPosition(newFoodPosition);
    }

    private boolean isFoodCollidingWithObstacles(Point foodPosition) {
        for (Point obstacle : obstacles) {
            if (foodPosition.equals(obstacle)) {
                return true;
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
                    g.drawImage(loadImage("C:\\dev\\snake_game - Sao chép\\assets\\body2.png"), p.x, p.y, UNIT_SIZE, UNIT_SIZE, this);
                }
            }

            // Vẽ thức ăn
            g.drawImage(food.getFoodImage(), food.getPosition().x, food.getPosition().y, UNIT_SIZE, UNIT_SIZE, this);

            // Vẽ chướng ngại vật
            for (Point obstacle : obstacles) {
                g.drawImage(obstacleImage, obstacle.x, obstacle.y, 50      , 50     , this);
            }

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
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

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