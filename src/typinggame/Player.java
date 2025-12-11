package typinggame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Player using a scaled dinosaur image.
 * Jump-only physics.
 */
public class Player {

    private static final double GRAVITY = 0.8;
    private static final double JUMP_VELOCITY = -12.0;

    // === 調整恐龍大小：1.0 = 原尺寸，0.5 = 50%，0.35 = 35% ===
    private static final double SCALE = 0.35;

    private int x;
    private int y;
    private int width;
    private int height;

    private double yVelocity;
    private boolean onGround;

    private BufferedImage dinoImg;       // 原始圖
    private BufferedImage scaledImg;     // 縮放後的圖

    public Player(int startX, int groundY) {
        loadImage();
        scaleImage();
        reset(startX, groundY);
    }

    /** Load dinosaur image */
    private void loadImage() {
        try {
            dinoImg = ImageIO.read(getClass().getResource("/typinggame/assets/dino.png"));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading dino image.");
            dinoImg = new BufferedImage(40, 60, BufferedImage.TYPE_INT_ARGB);
        }
    }

    /** Scale the image to desired size */
    private void scaleImage() {
        int newW = (int) (dinoImg.getWidth() * SCALE);
        int newH = (int) (dinoImg.getHeight() * SCALE);

        scaledImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = scaledImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(dinoImg, 0, 0, newW, newH, null);
        g2.dispose();

        width = newW;
        height = newH;
    }

    public void reset(int startX, int groundY) {
        this.x = startX;
        this.y = groundY - height;
        this.yVelocity = 0.0;
        this.onGround = true;
    }

    public void update() {
        if (!onGround) {
            yVelocity += GRAVITY;
            y += (int) Math.round(yVelocity);

            int floorY = GameConfig.GROUND_Y - height;
            if (y >= floorY) {
                y = floorY;
                yVelocity = 0.0;
                onGround = true;
            }
        }
    }

    /** Jump when on ground */
    public void jump() {
        if (onGround) {
            yVelocity = JUMP_VELOCITY;
            onGround = false;
        }
    }

    /** Draw the scaled dinosaur image */
    public void draw(Graphics g) {
        g.drawImage(scaledImg, x, y, null);
    }

    // --- Getters ---
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
