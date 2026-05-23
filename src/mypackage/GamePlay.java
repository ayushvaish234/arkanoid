package mypackage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

public class GamePlay extends JPanel implements ActionListener, KeyListener, MouseListener {
    private boolean play = false;
    private boolean inWelcomeScreen = true;
    private boolean inLevelSelectionScreen = false;
    private int selectedLevel = 1;
    private boolean levelComplete = false;
    private boolean nameEntered = false;
    private int score = 0;
    private int totalBubble;
    private Timer timer;
    private int delay = 8;
    private int ballposX = 120;
    private int ballposY = 350;
    private int ballXdir = -2;
    private int ballYdir = -4;
    private double playerX = 350;
    private double playerVelocityX = 0;
    private double playerSpeed = 5.0;
    private MapGenerator map;
    private int lvl = 1;

    private static final int PADDLE_W = 100;
    private static final int PADDLE_H = 15;
    private static final int PADDLE_Y = 550;
    private static final int BALL_SIZE = 20;

    // Pre-baked images — rendered once, stamped every frame
    private final BufferedImage paddleImg = bakePaddle();
    private final BufferedImage ballImg   = bakeBall();

    private final int[][] levelSizes = {
            {5, 3}, {5, 6}, {6, 3}, {5, 4}, {7, 3}, {6, 8}
    };

    // ── Bake paddle ───────────────────────────────────────────────────────────
    private BufferedImage bakePaddle() {
        int pad = 10, w = PADDLE_W, h = PADDLE_H, cr = 8;
        BufferedImage img = new BufferedImage(w + pad*2, h + pad*2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = pad, y = pad;
        RoundRectangle2D r = new RoundRectangle2D.Float(x, y, w, h, cr, cr);

        g.setColor(new Color(0, 210, 255, 40)); g.fill(new RoundRectangle2D.Float(x-5,y-5,w+10,h+10,cr+5,cr+5));
        g.setColor(new Color(0, 210, 255, 18)); g.fill(new RoundRectangle2D.Float(x-9,y-9,w+18,h+18,cr+9,cr+9));
        g.setColor(new Color(0,0,0,100));       g.fill(new RoundRectangle2D.Float(x+2,y+3,w,h,cr,cr));
        g.setPaint(new GradientPaint(x,y,new Color(0,30,50),x,y+h,new Color(0,60,90))); g.fill(r);
        g.setPaint(new GradientPaint(x,y,new Color(0,210,255,200),x,y+h,new Color(0,210,255,0))); g.fill(r);
        g.setPaint(new GradientPaint(x,y,new Color(255,255,255,70),x,y+h*0.45f,new Color(255,255,255,0)));
        g.fill(new RoundRectangle2D.Float(x+4,y+1,w-8,(int)(h*.55),cr,cr));
        g.setPaint(null); g.setColor(new Color(0,220,255,190)); g.setStroke(new BasicStroke(1.3f)); g.draw(r);
        g.setColor(new Color(255,255,255,80)); g.setStroke(new BasicStroke(1f));
        g.drawLine(x+cr,y+2,x+w-cr,y+2);
        g.dispose();
        return img;
    }

    // ── Bake ball ─────────────────────────────────────────────────────────────
    private BufferedImage bakeBall() {
        int pad = 10, s = BALL_SIZE, r = s/2;
        BufferedImage img = new BufferedImage(s+pad*2, s+pad*2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cx = pad+r, cy = pad+r;

        g.setColor(new Color(255,80,160,45)); g.fillOval(cx-r-5,cy-r-5,(r+5)*2,(r+5)*2);
        g.setColor(new Color(255,80,160,18)); g.fillOval(cx-r-9,cy-r-9,(r+9)*2,(r+9)*2);
        g.setColor(new Color(0,0,0,90));      g.fillOval(cx-r+2,cy-r+3,s,s);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx,cy),r,new float[]{0f,1f},
            new Color[]{new Color(60,0,30),new Color(200,30,100)})); g.fillOval(cx-r,cy-r,s,s);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx,cy),r,new float[]{0f,1f},
            new Color[]{new Color(255,80,160,130),new Color(255,80,160,0)})); g.fillOval(cx-r,cy-r,s,s);
        g.setPaint(new RadialGradientPaint(new Point2D.Float(cx-r*.3f,cy-r*.35f),r*.45f,new float[]{0f,1f},
            new Color[]{new Color(255,255,255,210),new Color(255,255,255,0)})); g.fillOval(cx-r,cy-r,s,s);
        g.setPaint(null); g.setColor(new Color(255,120,190,200)); g.setStroke(new BasicStroke(1.2f));
        g.drawOval(cx-r,cy-r,s-1,s-1);
        g.dispose();
        return img;
    }

    public GamePlay() {
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
        initializeLevel(lvl);
        initializeWelcomeScreenBubbles();
    }

    private void initializeLevel(int level) {
        int rows = levelSizes[level - 1][0];
        int cols = levelSizes[level - 1][1];
        totalBubble = rows * cols;
        map = new MapGenerator(rows, cols);
        ballposX = 120; ballposY = 350;
        ballXdir = -2;  ballYdir = -4;
    }

    public void paint(Graphics g) {
        if (inWelcomeScreen) drawWelcomeScreen(g);
        else                  drawGameScreen(g);
        g.dispose();
    }

    private MapGenerator welcomeScreenMap;

    private void initializeWelcomeScreenBubbles() {
        welcomeScreenMap = new MapGenerator(3, 5);
    }

    private void drawWelcomeScreen(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 692, 592);
        if (welcomeScreenMap != null) welcomeScreenMap.draw((Graphics2D) g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("serif", Font.BOLD, 30));
        g.drawString("       ARKANOID", 200, 250);
        g.setColor(Color.RED);
        g.fillOval(295, 310, 100, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("PLAY", 315, 345);
        g.setColor(Color.BLACK);
        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Leaderboard", 290, 390);
        g.drawString("Use Arrow keys to move", 260, 480);
    }

    private void drawGameScreen(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(8, 8, 20));
        g2.fillRect(1, 1, 692, 592);

        g2.setColor(new Color(0, 210, 255, 180));
        g2.fillRect(0, 0, 692, 3);
        g2.fillRect(0, 0, 3, 592);
        g2.fillRect(683, 3, 3, 592);

        // Paddle & ball — single drawImage call each
        int pad = 10;
        g2.drawImage(paddleImg, (int)playerX - pad, PADDLE_Y - pad, null);
        g2.drawImage(ballImg,   ballposX - pad,      ballposY - pad, null);

        map.draw(g2);

        g2.setColor(new Color(0, 210, 255));
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString("SCORE " + score, 535, 28);
        g2.drawString("LVL " + lvl, 12, 28);

        if (ballposY > 570) {
            play = false; ballXdir = 0; ballYdir = 0;
            g2.setColor(new Color(255, 80, 160));
            g2.setFont(new Font("Monospaced", Font.BOLD, 30));
            g2.drawString("GAME OVER  SCORE: " + score, 180, 300);
            g2.setColor(new Color(0, 210, 255));
            g2.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2.drawString("PRESS ENTER TO RESTART", 220, 340);
        }
        if (totalBubble == 0) {
            play = false; ballXdir = 0; ballYdir = 0;
            levelComplete = true;
        }
        if (levelComplete) {
            g2.setColor(new Color(0, 255, 140));
            g2.setFont(new Font("Monospaced", Font.BOLD, 30));
            g2.drawString("LEVEL " + lvl + " CLEAR!", 220, 300);
            g2.setColor(new Color(0, 210, 255));
            g2.setFont(new Font("Monospaced", Font.BOLD, 18));
            g2.drawString("PRESS ENTER FOR NEXT LEVEL", 195, 340);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            if (ballposX <= 0 || ballposX >= 670) ballXdir = -ballXdir;
            if (ballposY <= 0)                    ballYdir = -ballYdir;
            if (new Rectangle(ballposX, ballposY, BALL_SIZE, BALL_SIZE)
                    .intersects(new Rectangle((int)playerX, PADDLE_Y, PADDLE_W, PADDLE_H)))
                ballYdir = -ballYdir;

            A: for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int bx = j * map.bubbleWidth + 80;
                        int by = i * map.bubbleHeight + 50;
                        Rectangle bubbleRect = new Rectangle(bx, by, map.bubbleWidth, map.bubbleHeight);
                        Rectangle ballRect   = new Rectangle(ballposX, ballposY, BALL_SIZE, BALL_SIZE);
                        if (ballRect.intersects(bubbleRect)) {
                            map.setBubble(0, i, j);
                            totalBubble--;
                            score += 5;
                            if (ballposX + 19 <= bubbleRect.x || ballposX + 1 >= bubbleRect.x + bubbleRect.width)
                                ballXdir = -ballXdir;
                            else
                                ballYdir = -ballYdir;
                            break A;
                        }
                    }
                }
            }

            playerX += playerVelocityX;
            if (playerX < 0)   playerX = 0;
            if (playerX > 600) playerX = 600;
            ballposX += ballXdir;
            ballposY += ballYdir;
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getX() >= 295 && e.getX() <= 395 && e.getY() >= 310 && e.getY() <= 360) {
            inWelcomeScreen = false;
            initializeLevel(lvl);
            play = true;
        }
    }

    private void moveLeft()   { play = true; playerVelocityX = -playerSpeed; }
    private void moveRight()  { play = true; playerVelocityX =  playerSpeed; }
    private void stopMoving() { playerVelocityX = 0; }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)  moveLeft();
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight();
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play && !levelComplete) {
                lvl = 1; score = 0;
                ballposX = 120; ballposY = 350;
                ballXdir = -2;  ballYdir = -4;
                playerX = 310;
                initializeLevel(lvl);
                play = true;
                inWelcomeScreen = true;
                inLevelSelectionScreen = false;
            } else if (levelComplete) {
                levelComplete = false;
                if (lvl < levelSizes.length) lvl++;
                else                          lvl = 1;
                initializeLevel(lvl);
                play = true;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) stopMoving();
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
}