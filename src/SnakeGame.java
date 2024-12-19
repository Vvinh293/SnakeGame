import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH = 1024;  
    private final int BOARD_HEIGHT = 800; 
    private final int UNIT_SIZE = 25;     
    private Snake snake;                  
    private Food food;                    
    private boolean running = false;      
    private Timer timer;                  
    private Timer countdownTimer;
    private int countdown = 3;            
    private boolean countdownStarted = false;

    private ImageIcon backgroundIcon;  
    private Clip backgroundMusic;       // Định nghĩa biến để phát âm thanh nền

    public SnakeGame() {
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT)); 
        this.setFocusable(true);
        this.addKeyListener(this);

        backgroundIcon = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\taoanhdep_text_to_img_27883.jpeg"); 
        snake = new Snake(UNIT_SIZE * 3, UNIT_SIZE * 3, UNIT_SIZE); 
        food = new Food(UNIT_SIZE, BOARD_WIDTH, BOARD_HEIGHT); 
        timer = new Timer(100, this); 
        countdownTimer = new Timer(1000, e -> {
            countdown--;
            if (countdown == 0) {
                countdownTimer.stop();
                running = true; 
            }
            repaint();
        });
        timer.start();
    }

    private void returnToStartScreen() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this); 
        if (frame != null) {
            SnakeGameStartScreen.main(null); 
            frame.dispose(); 
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
        snake = new Snake(UNIT_SIZE * 3, UNIT_SIZE * 3, UNIT_SIZE);
        food = new Food(UNIT_SIZE, BOARD_WIDTH, BOARD_HEIGHT);
        running = false;
        countdown = 3;
        countdownStarted = false;
        countdownTimer.restart();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            snake.move();
            if (snake.checkCollisionWithWall(BOARD_WIDTH, BOARD_HEIGHT) || snake.checkCollisionWithItself()) {
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
        }
    }

    private void generateFood() {
        Point newFoodPosition;
        do {
            newFoodPosition = new Point(
                (int) (Math.random() * (BOARD_WIDTH / UNIT_SIZE)) * UNIT_SIZE,
                (int) (Math.random() * (BOARD_HEIGHT / UNIT_SIZE)) * UNIT_SIZE
            );
        } while (isFoodCollidingWithObstacles(newFoodPosition));

        food.setPosition(newFoodPosition);
    }

    private boolean isFoodCollidingWithObstacles(Point foodPosition) {
        for (int x = 0; x < BOARD_WIDTH; x += UNIT_SIZE) {
            if (foodPosition.equals(new Point(x, 0)) ||
                foodPosition.equals(new Point(x, BOARD_HEIGHT - UNIT_SIZE))) {
                return true;
            }
        }
        for (int y = 0; y < BOARD_HEIGHT; y += UNIT_SIZE) {
            if (foodPosition.equals(new Point(0, y)) ||
                foodPosition.equals(new Point(BOARD_WIDTH - UNIT_SIZE, y))) {
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
            resetGame();
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