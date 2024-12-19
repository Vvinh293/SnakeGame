import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.sound.sampled.*;

public class SnakeGameStartScreen {
    private static boolean isVisible = true; // Biến theo dõi trạng thái hiển thị của thông báo
    private static JLabel startLabel;       // JLabel chứa dòng chữ nhấp nháy
    private static Clip clip;                     // Clip để phát âm thanh nền
    
        public static void main(String[] args) {
            // Tạo cửa sổ chính
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 800);  // Cửa sổ có kích thước 1024x800
            frame.setLocationRelativeTo(null);  // Đặt cửa sổ ở giữa màn hình
    
            // Hiển thị màn hình bắt đầu
            showStartScreen(frame);
    
            // Cài đặt frame hiển thị
            frame.setVisible(true);
        }
    
        // Hiển thị màn hình bắt đầu
        private static void showStartScreen(JFrame frame) {
            // Tạo JPanel với layout BorderLayout
            JPanel panel = new BackgroundPanel(); // Sử dụng lớp BackgroundPanel để vẽ nền
            panel.setLayout(new BorderLayout());
    
            // Tạo JLabel để hiển thị thông báo và đặt nó ở vị trí bottom
            startLabel = new JLabel("Press ENTER to start, SPACE to view Instructions", JLabel.CENTER);
            startLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 30));  // Tăng cỡ chữ cho phù hợp với cửa sổ lớn
            startLabel.setForeground(Color.WHITE);  // Màu trắng cho văn bản
            panel.add(startLabel, BorderLayout.SOUTH);
    
            // Thêm JPanel vào JFrame
            frame.getContentPane().removeAll();
            frame.add(panel);
    
            // Sử dụng Timer để tạo hiệu ứng nhấp nháy cho thông báo
            Timer blinkTimer = new Timer(500, e -> {
                isVisible = !isVisible;  // Thay đổi trạng thái hiển thị
                startLabel.setVisible(isVisible);  // Cập nhật trạng thái hiển thị của JLabel
            });
            blinkTimer.start();
    
            // Xử lý sự kiện nhấn phím Enter để bắt đầu game
            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        // Dừng Timer và chuyển sang trò chơi thực tế
                        blinkTimer.stop();
                        startGame(frame);
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        // Hiển thị popup hướng dẫn khi nhấn 'SPACE'
                        JOptionPane.showMessageDialog(frame, "Use up, down, left and right arrows to make the snake move towards the food, if it hits the animal the game will end.", "Instructions", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
    
            frame.revalidate();
            frame.repaint();
        }
    
        // Chuyển sang trò chơi thực tế
        private static void startGame(JFrame frame) {
            // Khởi tạo trò chơi SnakeGame
            SnakeGame game = new SnakeGame();
    
            // Thay thế nội dung JFrame bằng SnakeGame
            frame.getContentPane().removeAll();
            frame.add(game);
            frame.pack(); // Điều chỉnh kích thước JFrame phù hợp với nội dung
            frame.setLocationRelativeTo(null); // Căn giữa lại màn hình
            frame.setVisible(true); // Cập nhật lại JFrame
    
            // Bắt đầu trò chơi
            game.requestFocusInWindow();
        }
    
        // Lớp BackgroundPanel để vẽ hình ảnh nền và phát âm thanh
        static class BackgroundPanel extends JPanel {
            private Image backgroundImage;
    
            /**
             * 
             */
            public BackgroundPanel() {
                // Tải hình ảnh nền
                backgroundImage = new ImageIcon("C:\\dev\\snake_game - Sao chép\\assets\\taoanhdep_text_to_img_73903.jpeg").getImage(); // Đảm bảo thay đường dẫn hình ảnh đúng
                
                // Tải âm thanh nền
                File audioFile = new File("C:\\dev\\snake_game - Sao chép\\assets\\theme song.wav"); // Đường dẫn đến file âm thanh
                try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY); // Phát âm thanh liên tục xuyên suốt chương trình
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Vẽ hình ảnh nền
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
