package typinggame;

import javax.swing.*;

/**
 * Entry point for the typing game.
 */
public class TypingGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. 彈出輸入視窗
            String name = JOptionPane.showInputDialog(null, 
                    "Welcome Runner!\nPlease enter your nickname:", 
                    "Game Start", 
                    JOptionPane.QUESTION_MESSAGE);

            // 如果使用者按取消或沒輸入，給個預設名字
            if (name == null || name.trim().isEmpty()) {
                name = "Unknown";
            }

            JFrame frame = new JFrame("English Typing Runner Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            GamePanel panel = new GamePanel(name);
            
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.startGame();
        });
    }
}