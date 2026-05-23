package mypackage;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class MapGenerator {
    public int map[][];
    public int bubbleWidth;
    public int bubbleHeight;

    // One cached tile image per row color — drawn once, stamped every frame
    private BufferedImage[] tileCache;

    private static final Color[] NEON_COLORS = {
        new Color(0, 240, 255),   // Electric cyan
        new Color(180, 0, 255),   // Deep violet
        new Color(0, 255, 140),   // Acid green
        new Color(255, 50, 120),  // Hot magenta
        new Color(255, 170, 0),   // Amber gold
        new Color(80, 140, 255),  // Ice blue
    };

    private static final int CORNER_RADIUS = 6;
    private static final int INSET         = 2;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                map[i][j] = 1;

        bubbleWidth  = 540 / col;
        bubbleHeight = 150 / row;

        // Pre-render one tile per unique row color into BufferedImages
        tileCache = new BufferedImage[NEON_COLORS.length];
        int w = bubbleWidth  - INSET * 2;
        int h = bubbleHeight - INSET * 2;
        for (int i = 0; i < NEON_COLORS.length; i++) {
            tileCache[i] = renderTile(w, h, NEON_COLORS[i]);
        }
    }

    /** Render one neon tile to an offscreen image — called once per color at startup. */
    private BufferedImage renderTile(int w, int h, Color neon) {
        // Add padding so the glow halo isn't clipped
        int pad = 8;
        BufferedImage img = new BufferedImage(w + pad * 2, h + pad * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);

        int x = pad, y = pad;
        RoundRectangle2D block = new RoundRectangle2D.Float(x, y, w, h, CORNER_RADIUS, CORNER_RADIUS);

        // 1. Soft glow — two passes only (was 6), enough to look great
        Color halo1 = new Color(neon.getRed(), neon.getGreen(), neon.getBlue(), 45);
        Color halo2 = new Color(neon.getRed(), neon.getGreen(), neon.getBlue(), 20);
        g.setColor(halo1);
        g.fill(new RoundRectangle2D.Float(x - 4, y - 4, w + 8, h + 8, CORNER_RADIUS + 4, CORNER_RADIUS + 4));
        g.setColor(halo2);
        g.fill(new RoundRectangle2D.Float(x - 7, y - 7, w + 14, h + 14, CORNER_RADIUS + 7, CORNER_RADIUS + 7));

        // 2. Drop shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.fill(new RoundRectangle2D.Float(x + 2, y + 3, w, h, CORNER_RADIUS, CORNER_RADIUS));

        // 3. Dark base gradient
        Color darkBase = new Color(
            (int)(neon.getRed()   * 0.08),
            (int)(neon.getGreen() * 0.08),
            (int)(neon.getBlue()  * 0.08)
        );
        Color midTone = new Color(
            (int)(neon.getRed()   * 0.25),
            (int)(neon.getGreen() * 0.25),
            (int)(neon.getBlue()  * 0.25)
        );
        g.setPaint(new GradientPaint(x, y, darkBase, x, y + h, midTone));
        g.fill(block);

        // 4. Neon top stripe
        g.setPaint(new GradientPaint(
            x, y,           new Color(neon.getRed(), neon.getGreen(), neon.getBlue(), 190),
            x, y + h * 0.4f, new Color(neon.getRed(), neon.getGreen(), neon.getBlue(), 0)
        ));
        g.fill(block);

        // 5. Gloss sheen
        g.setPaint(new GradientPaint(
            x, y,              new Color(255, 255, 255, 55),
            x, y + h * 0.30f,  new Color(255, 255, 255, 0)
        ));
        g.fill(new RoundRectangle2D.Float(x + 3, y + 2, w - 6, (int)(h * 0.45), CORNER_RADIUS, CORNER_RADIUS));

        // 6. Neon border
        g.setPaint(null);
        g.setColor(new Color(neon.getRed(), neon.getGreen(), neon.getBlue(), 180));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(block);

        // 7. Inner top highlight
        g.setColor(new Color(255, 255, 255, 65));
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(x + CORNER_RADIUS, y + 2, x + w - CORNER_RADIUS, y + 2);

        g.dispose();
        return img;
    }

    public void draw(Graphics2D g) {
        // Minimal hints — just anti-aliasing; no quality/interpolation overhead
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = 8;
        for (int i = 0; i < map.length; i++) {
            BufferedImage tile = tileCache[i % tileCache.length];
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    int x = j * bubbleWidth  + 80 + INSET - pad;
                    int y = i * bubbleHeight + 50 + INSET - pad;
                    // drawImage is a single GPU-accelerated blit — extremely fast
                    g.drawImage(tile, x, y, null);
                }
            }
        }
    }

    public void setBubble(int value, int row, int col) {
        map[row][col] = value;
    }
}