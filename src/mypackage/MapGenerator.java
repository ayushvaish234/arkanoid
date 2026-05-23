package mypackage;

import java.awt.*;

public class MapGenerator {
    public int map[][];
    public int bubbleWidth;
    public int bubbleHeight;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                map[i][j] = 1; // Initialize all blocks as visible
            }
        }

        bubbleWidth = 540 / col;
        bubbleHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    Color fillColor = getColorForRow(i);
                    g.setColor(fillColor);
                    int x = j * bubbleWidth + 80;
                    int y = i * bubbleHeight + 50;

                    // Draw filled rectangle
                    g.fillRect(x, y, bubbleWidth, bubbleHeight);

                    // Draw border around rectangle
                    g.setColor(Color.BLACK); // Set border color
                    g.drawRect(x, y, bubbleWidth, bubbleHeight);
                }
            }
        }
    }

    private Color getColorForRow(int rowIndex) {
        Color[] colors = {
                new Color(249, 123, 34),
                new Color(34, 123, 249),
                new Color(123, 249, 34),
                new Color(249, 34, 123),
                new Color(34, 249, 123),
                new Color(123, 34, 249)
        };
        return colors[rowIndex % colors.length];
    }

    public void setBubble(int value, int row, int col) {
        map[row][col] = value;
    }
}
