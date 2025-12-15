package typinggame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player {

    private static final double GRAVITY = 0.8;
    private static final double JUMP_VELOCITY = -12.0;
    private static final double SCALE = 0.35;

    // === 重點修改：改用 double 以獲得平滑移動 ===
    private double x;
    private double y;
    
    private int width;
    private int height;
    private double yVelocity;
    private boolean onGround;

    // 動畫相關
    private BufferedImage[] frames; 
    private final int[] animSequence = {0, 1, 0, 2}; // 1->2->1->3 步態
    private int currentSeqIndex = 0;
    private int animTimer = 0;
    private static final int ANIM_SPEED = 8; 
    private BufferedImage currentImage;

    public Player(int startX, int groundY) {
        loadImages();
        reset(startX, groundY);
    }

    private void loadImages() {
        frames = new BufferedImage[3];
        try {
            // 請確保這些圖片存在，若無則使用 try-catch 中的備案
            frames[0] = loadAndScale("/typinggame/assets/dino1.png"); // 站立/中
            frames[1] = loadAndScale("/typinggame/assets/dino2.png"); // 左腳
            frames[2] = loadAndScale("/typinggame/assets/dino3.png"); // 右腳
        } catch (Exception e) {
            System.err.println("Error loading player images. Using fallback.");
            createFallbackImages();
        }
        updateCurrentImage();
        this.width = currentImage.getWidth();
        this.height = currentImage.getHeight();
    }

    private BufferedImage loadAndScale(String path) throws IOException {
        BufferedImage raw = ImageIO.read(getClass().getResource(path));
        int newW = (int) (raw.getWidth() * SCALE);
        int newH = (int) (raw.getHeight() * SCALE);
        BufferedImage scaled = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(raw, 0, 0, newW, newH, null);
        g2.dispose();
        return scaled;
    }
    
    private void createFallbackImages() {
        BufferedImage box = new BufferedImage(40, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics g = box.getGraphics();
        g.setColor(Color.GREEN); g.fillRect(0,0,40,60); g.dispose();
        frames[0] = frames[1] = frames[2] = box;
    }

    public void reset(int startX, int groundY) {
        this.x = startX;
        // 確保初始位置準確
        this.y = groundY - height;
        this.yVelocity = 0.0;
        this.onGround = true;
        this.currentSeqIndex = 0;
        this.animTimer = 0;
        updateCurrentImage();
    }

    public void update() {
        // 1. 物理計算 (使用 double 保留精度)
        if (!onGround) {
            yVelocity += GRAVITY;
            y += yVelocity; // === 這裡不再轉成 int，保留小數點 ===

            int floorY = GameConfig.GROUND_Y - height;
            if (y >= floorY) {
                y = floorY;
                yVelocity = 0.0;
                onGround = true;
            }
        }

        // 2. 動畫邏輯
        animate();
    }

    private void animate() {
        if (!onGround) {
            currentImage = frames[0]; 
        } else {
            animTimer++;
            if (animTimer >= ANIM_SPEED) {
                animTimer = 0;
                currentSeqIndex = (currentSeqIndex + 1) % animSequence.length;
                updateCurrentImage();
            }
        }
    }
    
    private void updateCurrentImage() {
        currentImage = frames[animSequence[currentSeqIndex]];
    }

    public void jump() {
        if (onGround) {
            yVelocity = JUMP_VELOCITY;
            onGround = false;
        }
    }

    public void draw(Graphics g) {
        // === 只有在畫圖的最後一刻才轉成 int ===
        g.drawImage(currentImage, (int)x, (int)y, null);
    }

    // Getters 改回傳 double 或 int 皆可，碰撞偵測通常用 Rectangle (int)
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}