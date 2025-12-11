package typinggame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Obstacle rendered as a cactus PNG image.
 */
public class Obstacle {

    public enum Type { JUMP }

    private int x;
    private int y;      // 不再是 final，要能重算位置
    private int width;
    private int height;

    private final String word;
    private final Type type;

    private boolean clearedByTyping;

    private static BufferedImage cactusImg;
    private static BufferedImage scaledImg;

    // 調整仙人掌大小：越小圖片越小
    private static final double CACTUS_SCALE = 0.3;

    public Obstacle(int x, int y, int width, int height, String word, Type type) {
        // 先暫存，稍後 loadImage 後會依圖片實際尺寸覆蓋
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.word = word;
        this.type = type;
        this.clearedByTyping = false;

        loadImageAndAlignToGround();
    }

    /** 載入並縮放仙人掌圖片，同時讓底部對齊地面線 */
    private void loadImageAndAlignToGround() {
        if (cactusImg == null) {
            try {
                cactusImg = ImageIO.read(getClass().getResource("/typinggame/assets/cactus.png"));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Error loading cactus image.");
                cactusImg = new BufferedImage(40, 80, BufferedImage.TYPE_INT_ARGB);
            }

            // 縮放圖片
            int newW = (int) (cactusImg.getWidth() * CACTUS_SCALE);
            int newH = (int) (cactusImg.getHeight() * CACTUS_SCALE);

            scaledImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaledImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(cactusImg, 0, 0, newW, newH, null);
            g2.dispose();
        }

        // 用縮放後圖片尺寸當成實際碰撞盒大小
        this.width = scaledImg.getWidth();
        this.height = scaledImg.getHeight();

        // 讓仙人掌底部剛好落在地面線上
        int groundY = GameConfig.GROUND_Y;
        this.y = groundY - this.height;
    }

    public void moveLeft(double dx) {
        x -= (int) Math.round(dx);
    }

    public void draw(Graphics g) {
        // 畫仙人掌
        g.drawImage(scaledImg, x, y, null);

        // 單字畫在仙人掌上方
        g.setColor(Color.BLACK);
        Font original = g.getFont();
        g.setFont(original.deriveFont(Font.PLAIN, 14f));

        int textWidth = g.getFontMetrics().stringWidth(word);
        int textX = x + (width - textWidth) / 2;
        int textY = y - 5;
        g.drawString(word, textX, textY);

        g.setFont(original);
    }

    public void markCleared() {
        clearedByTyping = true;
    }

    public boolean isCleared() {
        return clearedByTyping;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public String getWord() { return word; }
    public Type getType() { return type; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
