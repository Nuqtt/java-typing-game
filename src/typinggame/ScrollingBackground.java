package typinggame;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 負責處理無限捲動的背景。
 * 採用程序化生成 (Procedural Generation) 繪製白色背景與灰色小雲。
 */
public class ScrollingBackground {
    private BufferedImage image;
    private double x;
    private int width;
    private int height;
    
    // 捲動速度係數 (0.5 代表半速，製造遠景感)
    private final double scrollFactor; 

    public ScrollingBackground(double scrollFactor) {
        this.scrollFactor = scrollFactor;
        // 根據遊戲設定的寬高來建立畫布
        this.width = GameConfig.WIDTH;
        this.height = GameConfig.HEIGHT;
        
        // 產生背景圖
        createProceduralImage();
    }

    /** 直接用程式碼畫出背景，不需讀取外部檔案 */
    private void createProceduralImage() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 1. 填滿白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 2. 繪製灰色小雲
        g.setColor(new Color(220, 220, 220)); // 淺灰色
        
        // 手動配置幾朵雲的位置 (x, y)，你可以自由增減
        drawSmallCloud(g, 50, 40);
        drawSmallCloud(g, 250, 80);
        drawSmallCloud(g, 450, 30);
        drawSmallCloud(g, 650, 100);
        drawSmallCloud(g, 780, 50);

        g.dispose();
    }

    /** 輔助方法：畫一朵較小的雲 */
    private void drawSmallCloud(Graphics2D g, int x, int y) {
        // 用三個橢圓組合成一朵雲，尺寸縮小
        g.fillOval(x, y, 30, 20);       // 左
        g.fillOval(x + 15, y - 10, 35, 25); // 中 (較高)
        g.fillOval(x + 30, y + 5, 25, 15);  // 右
    }

    public void update(double gameSpeed) {
        // 移動背景
        x -= gameSpeed * scrollFactor;

        // 無限循環邏輯
        if (x <= -width) {
            x += width;
        }
    }

    public void draw(Graphics g) {
        // 畫第一張
        g.drawImage(image, (int)x, 0, null);

        // 畫第二張接在後面 (無縫銜接)
        if (x < width) {
            g.drawImage(image, (int)x + width, 0, null);
        }
    }
    
    public void reset() {
        this.x = 0;
    }
}