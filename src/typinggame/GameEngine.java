package typinggame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Core game logic (state, updates, spawning, collisions, typing).
 *
 * 調整：
 * 1. 取消手動 Space 跳躍、取消滑行，只保留「自動跳躍」。
 * 2. 玩家正確輸入障礙物單字後，當障礙物靠近玩家時自動跳躍並清除障礙物。
 * 3. Space 在 Game Over 畫面中用來重新開始遊戲（在 GamePanel 處理）。
 */
public class GameEngine {
    private static final int MAX_TYPED_LENGTH = 20;

    private long lastWrongInputAt = -1;                 // 上一次打錯字的時間
    private static final long WRONG_EFFECT_MS = 200;    // 顯示 200ms 抖動變色

    // 打對的特效時間
    private long lastCorrectInputAt = -1;
    private static final long CORRECT_EFFECT_MS = 200;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final Player player;
    private final WordGenerator wordGenerator;
    private final Random random = new Random();

    private final StringBuilder typedInput = new StringBuilder();
    private final ScrollingBackground background; // 背景物件

    private boolean running;
    private boolean gameOver;
    private long startTime;
    private long endedAt;

    private int spawnCounter;
    private int spawnInterval;
    private double speed;
    private long lastDifficultyIncreaseAt;

    public GameEngine() {
        this.player = new Player(80, GameConfig.GROUND_Y);
        this.wordGenerator = new WordGenerator();
        // 初始化背景
        // 假設你有一張 bg.png，沒有的話它會自動用備案的藍天圖
        // 係數 0.5 代表它移動速度是障礙物的一半 (製造遠景感)
        this.background = new ScrollingBackground(0.5);
        resetGameState();
    }

    private void resetGameState() {
        obstacles.clear();
        player.reset(80, GameConfig.GROUND_Y);
        typedInput.setLength(0);

        running = false;
        gameOver = false;
        startTime = 0L;
        endedAt = 0L;

        spawnInterval = GameConfig.INITIAL_SPAWN_INTERVAL;
        spawnCounter = spawnInterval;
        speed = GameConfig.INITIAL_SPEED;
        lastDifficultyIncreaseAt = System.currentTimeMillis();

        if (background != null) {
            background.reset();
        }
    }

    public void startGame() {
        resetGameState();
        running = true;
        gameOver = false;
        startTime = System.currentTimeMillis();
        lastDifficultyIncreaseAt = startTime;
    }

    public void update() {
        if (!running) {
            return;
        }

        player.update();

        // 更新背景 (傳入當前的遊戲速度)
        background.update(speed);

        // 移動障礙物並移除離開畫面的
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Obstacle o = obstacles.get(i);
            o.moveLeft(speed);
            if (o.getX() + o.getWidth() < 0) {
                obstacles.remove(i);
            }
        }

        // 生成計數
        if (--spawnCounter <= 0) {
            spawnObstacle();
            spawnCounter = spawnInterval;
        }

        // 碰撞 / 自動閃避
        checkCollisionOrAvoid();

        // 難度提升
        maybeIncreaseDifficulty();
    }

    private void spawnObstacle() {
        int difficultyLevel = (int) ((speed - GameConfig.INITIAL_SPEED) / GameConfig.SPEED_INCREMENT);
        if (difficultyLevel < 0) {
            difficultyLevel = 0;
        }

        String word = wordGenerator.next(difficultyLevel);

        Obstacle.Type type = Obstacle.Type.JUMP; // 只保留跳躍型障礙
        int width = 40 + word.length() * 4;
        int height = 40;
        int y = GameConfig.GROUND_Y - height;

        obstacles.add(new Obstacle(GameConfig.WIDTH, y, width, height, word, type));
    }

    private void maybeIncreaseDifficulty() {
        long now = System.currentTimeMillis();
        if (now - lastDifficultyIncreaseAt >= GameConfig.DIFFICULTY_INTERVAL_MS) {
            lastDifficultyIncreaseAt = now;
            speed += GameConfig.SPEED_INCREMENT;
            spawnInterval = Math.max(GameConfig.MIN_SPAWN_INTERVAL,
                    spawnInterval - GameConfig.SPAWN_STEP);
        }
    }

    /**
     * 打字處理：只在遊戲進行中生效。
     */
    public void handleTypedChar(char ch) {
        if (!running || gameOver) {
            return;
        }

        // Backspace
        if (ch == '\b') {
            if (typedInput.length() > 0) {
                typedInput.setLength(typedInput.length() - 1);
            }
            return;
        }

        // 只接受英文字母
        if (Character.isLetter(ch)) {
            if (typedInput.length() < MAX_TYPED_LENGTH) {
                typedInput.append(Character.toLowerCase(ch));
            }
        }
    }

    /**
     * 碰撞與自動跳躍判斷：
     * - 若玩家打對單字，會把該障礙物標記為 cleared。
     * - 當 cleared 的障礙物接近玩家時，自動跳躍並清除。
     * - 若未 cleared 且發生碰撞，Game Over。
     */
    private void checkCollisionOrAvoid() {
        if (obstacles.isEmpty()) {
            return;
        }

        Obstacle first = obstacles.get(0);

        // 處理打字與前綴
        String target = first.getWord().toLowerCase();
        String typed = typedInput.toString();

        if (!typed.isEmpty() && !target.startsWith(typed)) {
            // 打錯，清空重打 + 啟動錯誤特效
            typedInput.setLength(0);
            typed = "";
            lastWrongInputAt = System.currentTimeMillis();
        }


        if (!typed.isEmpty() && typed.equals(target) && !first.isCleared()) {
            // 單字輸入完成，標記為已清除
            first.markCleared();
            typedInput.setLength(0);

            // ✅ 啟動「打勾」特效
            lastCorrectInputAt = System.currentTimeMillis();
        }


        // 當障礙物到達玩家 X 範圍附近時處理
        if (first.getX() < player.getX() + player.getWidth()) {
            if (first.isCleared()) {
                // 已經打對單字：自動跳躍並清除障礙物
                performAvoidAction(first.getType());
                obstacles.remove(0);
            } else {
                // 沒打對：檢查是否撞到
                Rectangle pRect = player.getBounds();
                Rectangle oRect = first.getBounds();
                if (pRect.intersects(oRect)) {
                    gameOver();
                }
            }
        }
    }

    private void performAvoidAction(Obstacle.Type type) {
        // 現在只有 JUMP 型態，直接觸發跳躍動畫
        player.jump();
    }

    private void gameOver() {
        if (!gameOver) {
            gameOver = true;
            running = false;
            endedAt = System.currentTimeMillis();
        }
    }

    // --- Getters for rendering & state ---

    public Player getPlayer() {
        return player;
    }

    public java.util.List<Obstacle> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getTypedInput() {
        return typedInput.toString();
    }

    public long getElapsedMillis() {
        long end = gameOver && endedAt > 0 ? endedAt : System.currentTimeMillis();
        return (startTime == 0L) ? 0L : end - startTime;
    }

    public boolean isWrongEffectActive() {
    if (lastWrongInputAt < 0) return false;
    return System.currentTimeMillis() - lastWrongInputAt < WRONG_EFFECT_MS;
    }

    public boolean isCorrectEffectActive() {
    if (lastCorrectInputAt < 0) return false;
    return System.currentTimeMillis() - lastCorrectInputAt < CORRECT_EFFECT_MS;
    }

    // Panel 畫圖用
    public ScrollingBackground getBackground() {
        return background;
    }

    /** 目前畫面中第一個障礙物的單字（UI 顯示用）。 */
    public String getCurrentWord() {
        if (obstacles.isEmpty()) {
            return null;
        }
        return obstacles.get(0).getWord();
    }
}
