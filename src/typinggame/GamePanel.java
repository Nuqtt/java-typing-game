package typinggame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 * Swing panel that wires input, timer, and rendering to the game engine.
 *
 * 控制調整：
 * - 遊戲進行中只接受文字輸入，不再使用 Space 跳躍或向下鍵滑行。
 * - Game Over 時按 Space 重新開始遊戲。
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final GameEngine engine;
    private final Timer timer;

    public GamePanel() {
        this.engine = new GameEngine();
        this.timer = new Timer(GameConfig.TIMER_DELAY_MS, this);

        setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    public void startGame() {
        engine.startGame();
        requestFocusInWindow();
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        engine.update();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 背景地面線
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, GameConfig.GROUND_Y, GameConfig.WIDTH, GameConfig.GROUND_Y);

        // 玩家與障礙物
        engine.getPlayer().draw(g);
        for (Obstacle obs : engine.getObstacles()) {
            obs.draw(g);
        }

        // 顯示目前目標單字（上方高亮）
        drawCurrentWordBar(g);

        // 下方顯示實際輸入
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.drawString("Typed: " + engine.getTypedInput(), 10, GameConfig.HEIGHT - 10);

        // 左上角顯示操作提示
        g.drawString("Type the word to auto-jump over obstacles", 10, 20);

        // 右上角顯示分數（存活秒數）
        long elapsed = engine.getElapsedMillis();
        g.drawString("Score: " + elapsed / 1000, GameConfig.WIDTH - 120, 20);

        if (engine.isGameOver()) {
            drawGameOverOverlay(g, elapsed);
        }
    }

    private void drawCurrentWordBar(Graphics g) {
    String currentWord = engine.getCurrentWord();
    String typed = engine.getTypedInput();

    if (currentWord == null) {
        return;
    }

    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
    FontMetrics fm = g2.getFontMetrics();

    boolean wrongEffect = engine.isWrongEffectActive();
    boolean correctEffect = engine.isCorrectEffectActive() && !wrongEffect; // 錯誤優先於打勾特效

    int baseX = 10;
    int baseY = 50;

    // 錯誤時左右抖動
    if (wrongEffect) {
        int shake = (int) (Math.sin(System.currentTimeMillis() / 30.0) * 4);
        baseX += shake;
    }

    int x = baseX;
    int y = baseY;

    for (int i = 0; i < currentWord.length(); i++) {
        char c = currentWord.charAt(i);

        if (wrongEffect) {
            g2.setColor(new Color(220, 40, 40)); // 整串字變紅
        } else {
            if (i < typed.length()) {
                char typedChar = typed.charAt(i);
                if (Character.toLowerCase(typedChar) == Character.toLowerCase(c)) {
                    g2.setColor(new Color(0, 150, 0)); // 已正確輸入
                } else {
                    g2.setColor(new Color(180, 0, 0)); // 理論上瞬間就被清空
                }
            } else {
                g2.setColor(Color.DARK_GRAY); // 尚未輸入
            }
        }

        String s = String.valueOf(c);
        g2.drawString(s, x, y);

        x += fm.charWidth(c) + 2;
    }

    // ✅ 若打對剛結束，顯示一瞬間的綠色勾勾
    if (correctEffect) {
        g2.setColor(new Color(0, 180, 0));
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));

        // 勾勾畫在整個單字後面一點點
        int checkX = x + 8;
        int checkY = y;

        g2.drawString("✔", checkX, checkY);
    }
}



    private void drawGameOverOverlay(Graphics g, long elapsedMillis) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 半透明背景
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        String msg = "Game Over";
        int msgWidth = g2.getFontMetrics().stringWidth(msg);
        int x = (getWidth() - msgWidth) / 2;
        int y = getHeight() / 2 - 20;
        g2.drawString(msg, x, y);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
        String score = "Score: " + (elapsedMillis / 1000) + " s";
        int scoreWidth = g2.getFontMetrics().stringWidth(score);
        g2.drawString(score, (getWidth() - scoreWidth) / 2, y + 40);

        String hint = "Press SPACE to restart";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (getWidth() - hintWidth) / 2, y + 80);

        g2.dispose();
    }

    // --- KeyListener ---

    @Override
    public void keyTyped(KeyEvent e) {
        // 遊戲進行中：接受文字輸入
        if (engine.isRunning()) {
            char ch = e.getKeyChar();
            engine.handleTypedChar(ch);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Game Over 時，按 SPACE 重新開始
        if (engine.isGameOver() && e.getKeyCode() == KeyEvent.VK_SPACE) {
            engine.startGame();
            return;
        }
        // 遊戲進行中，已經不再使用 Space/Down 作為動作鍵
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not used
    }
}
