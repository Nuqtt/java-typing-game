package typinggame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Obstacle {

    public enum Type { JUMP }

    // === 重點修改：座標改為 double ===
    private double x;
    private double y;
    
    private int width;
    private int height;

    private final String word;
    private final Type type;
    private boolean clearedByTyping;

    private static BufferedImage cactusImg;
    private static BufferedImage scaledImg;
    private static final double CACTUS_SCALE = 0.3;

    public Obstacle(double startX, int startY, int width, int height, String word, Type type) {
        this.x = startX;
        this.y = startY; // 這裡暫存，loadImage 會修正
        this.width = width;
        this.height = height;
        this.word = word;
        this.type = type;
        this.clearedByTyping = false;

        loadImageAndAlignToGround();
    }

    private void loadImageAndAlignToGround() {
        if (cactusImg == null) {
            try {
                cactusImg = ImageIO.read(getClass().getResource("/typinggame/assets/cactus.png"));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Error loading cactus image.");
                cactusImg = new BufferedImage(40, 80, BufferedImage.TYPE_INT_ARGB);
            }
            int newW = (int) (cactusImg.getWidth() * CACTUS_SCALE);
            int newH = (int) (cactusImg.getHeight() * CACTUS_SCALE);

            scaledImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaledImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(cactusImg, 0, 0, newW, newH, null);
            g2.dispose();
        }

        this.width = scaledImg.getWidth();
        this.height = scaledImg.getHeight();
        
        // 修正 Y 座標對齊地面
        this.y = GameConfig.GROUND_Y - this.height;
    }

    // === 重點修改：接收 double 並進行高精度計算 ===
    public void moveLeft(double dx) {
        x -= dx; // 不再轉型成 int，保留小數點精度
    }

    public void draw(Graphics g) {
        // 繪製時才轉成 int
        g.drawImage(scaledImg, (int)x, (int)y, null);

        g.setColor(Color.BLACK);
        Font original = g.getFont();
        g.setFont(original.deriveFont(Font.PLAIN, 14f));

        int textWidth = g.getFontMetrics().stringWidth(word);
        int textX = (int)x + (width - textWidth) / 2;
        int textY = (int)y - 5;
        g.drawString(word, textX, textY);

        g.setFont(original);
    }

    public void markCleared() { clearedByTyping = true; }
    public boolean isCleared() { return clearedByTyping; }

    public double getX() { return x; } // 回傳 double
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getWord() { return word; }
    public Type getType() { return type; }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}