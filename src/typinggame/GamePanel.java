package typinggame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 * Swing panel that wires input, timer, and rendering to the game engine.
 * * 更新：
 * 1. 在 paintComponent 中加入了背景繪製邏輯。
 * 2. 保留了所有的 UI 顯示（分數、單字條、Game Over 畫面）。
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final GameEngine engine;
    private final Timer timer;

    public GamePanel(String playerName) {
        LeaderboardStorage storage = new MemoryLeaderboard();
        storage = new MongoLeaderboard("mongodb://localhost:27017", "TypingGameDB", "scores");
        this.engine = new GameEngine(playerName, storage);
        this.timer = new Timer(GameConfig.TIMER_DELAY_MS, this);

        setPreferredSize(new Dimension(GameConfig.WIDTH, GameConfig.HEIGHT));
        setBackground(Color.WHITE); // 預設背景色（當圖片載入失敗或未設定時顯示）
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

        // --- 1. 繪製捲動背景 ---
        // (注意：這需要你的 GameEngine 有 getBackground() 方法)
        // 如果你還沒實作背景，這段程式碼會被跳過，不會報錯（前提是 getBackground 回傳 null）
        if (engine.getBackground() != null) {
            engine.getBackground().draw(g);
        }

        // --- 2. 繪製地面線 ---
        // (即使有背景圖，保留這條線通常有助於視覺定位，若背景圖已有地面可註解掉)
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, GameConfig.GROUND_Y, GameConfig.WIDTH, GameConfig.GROUND_Y);

        // --- 3. 繪製遊戲物件 (玩家與障礙物) ---
        engine.getPlayer().draw(g);
        for (Obstacle obs : engine.getObstacles()) {
            obs.draw(g);
        }

        // --- 4. 繪製 UI (目標單字) ---
        drawCurrentWordBar(g);

        // --- 5. 繪製 UI (一般資訊) ---
        drawHUD(g);

        // --- 6. 繪製 Game Over 遮罩 ---
        if (engine.isGameOver()) {
            // 傳入經過時間來顯示最終分數
            drawGameOverOverlay(g, engine.getElapsedMillis());
        }
    }

    /** 繪製抬頭顯示器 (HUD): 輸入文字、提示、分數 */
    private void drawHUD(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // 左下角：顯示目前輸入
        g.drawString("Typed: " + engine.getTypedInput(), 10, GameConfig.HEIGHT - 10);

        // 左上角：操作提示
        g.drawString("Type the word to auto-jump", 10, 20);

        // 右上角：分數（存活秒數）
        long elapsed = engine.getElapsedMillis();
        g.drawString("Score: " + elapsed / 1000, GameConfig.WIDTH - 120, 20);
    }

    /** 繪製目前障礙物上方的單字與輸入狀態 */
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
        boolean correctEffect = engine.isCorrectEffectActive() && !wrongEffect;

        int baseX = 10;
        int baseY = 50;

        // 錯誤時左右抖動特效
        if (wrongEffect) {
            int shake = (int) (Math.sin(System.currentTimeMillis() / 30.0) * 4);
            baseX += shake;
        }

        int x = baseX;
        int y = baseY;

        for (int i = 0; i < currentWord.length(); i++) {
            char c = currentWord.charAt(i);

            if (wrongEffect) {
                g2.setColor(new Color(220, 40, 40)); // 錯誤：整串變紅
            } else {
                if (i < typed.length()) {
                    char typedChar = typed.charAt(i);
                    if (Character.toLowerCase(typedChar) == Character.toLowerCase(c)) {
                        g2.setColor(new Color(0, 150, 0)); // 正確：綠色
                    } else {
                        g2.setColor(new Color(180, 0, 0)); // 打錯字元（理論上會瞬間被清空）
                    }
                } else {
                    g2.setColor(Color.DARK_GRAY); // 尚未輸入：灰色
                }
            }

            String s = String.valueOf(c);
            g2.drawString(s, x, y);
            x += fm.charWidth(c) + 2;
        }

        // 打對剛結束時的綠色勾勾特效
        if (correctEffect) {
            g2.setColor(new Color(0, 180, 0));
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.drawString("✔", x + 8, y);
        }
    }

    /** 繪製遊戲結束畫面 */
   private void drawGameOverOverlay(Graphics g, long elapsedMillis) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 半透明背景
        g2.setColor(new Color(0, 0, 0, 200)); // 顏色改深一點比較好看清楚字
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        
        String msg = "Game Over";
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2 - 80; // 往上提一點
        int msgWidth = g2.getFontMetrics().stringWidth(msg);
        g2.drawString(msg, centerX - msgWidth / 2, centerY);

        // === 顯示排行榜 ===
        g2.setFont(new Font("Monospaced", Font.PLAIN, 18));
        g2.setColor(Color.YELLOW);
        g2.drawString("--- Leaderboard ---", centerX - 100, centerY + 30);
        
        g2.setColor(Color.WHITE);
        java.util.List<ScoreEntry> scores = engine.getTopScores();
        int yOffset = centerY + 60;
        
        if (scores != null) {
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry s = scores.get(i);
                String line = String.format("%d. %-10s  %d s", i + 1, s.getPlayerName(), s.getScore());
                g2.drawString(line, centerX - 120, yOffset);
                yOffset += 25;
            }
        }
        // 重新開始提示
        g2.setColor(Color.LIGHT_GRAY);
        String hint = "Press SPACE to restart";
        g2.drawString(hint, centerX - g2.getFontMetrics().stringWidth(hint) / 2, yOffset + 30);

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
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not used
    }
}