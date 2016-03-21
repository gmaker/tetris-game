package com.know.know.tetris.level;

import com.know.know.tetris.Input;
import com.know.know.tetris.ai.Bot;
import com.know.know.tetris.figure.Figure;
import com.know.know.tetris.gfx.Art;
import com.know.know.tetris.screen.GameScreen;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * mr.gmaker@yandex.ru
 */
public class Level {
    public static final Random random = new Random(123);
    public double speed = 1;
    public static int W = 10;
    public static int H = 20;
    public Figure currentFigure;
    public Figure nextFigure;
    public final int[] data;
    public int turn = 0;
    private Bot bot = null;
    private int tickTime = 0;
    private static final int DIFFICULT_STEP = 5;
    private final int xOffset;
    private final int yOffset;
    private Map<Integer, Boolean> linesToBeRemoved = new HashMap<Integer, Boolean>();
    private int removeAnimationTime = 0;
    private GameScreen gameScreen;
    private int score = 0;
    private Font smallFont = new Font(null, Font.PLAIN, 10);

    public Level(GameScreen gameScreen, int xOffset, int yOffset, Bot bot) {
        this.gameScreen = gameScreen;
        this.bot = bot;
        if (bot != null) {
            this.bot.init(this);
        }
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.data = new int[W * H];
        for (int i = 0; i < W * H; i++) {
            this.data[i] = -1;
        }
    }

    public void generateNextFigure() {
        this.currentFigure = this.nextFigure;

        this.nextFigure = Figure.createFigure(10 * 4, 0, Art.figuresData[random.nextInt(Art.figuresData.length)]);
        this.nextFigure.init(this);
        this.nextFigure.rotate(random.nextInt(4));
        this.turn++;
    }

    public void tick(Input input) {
        this.speed = 0;
        int difficult = this.score / 1000;

        if (++this.tickTime % (Math.max(1, (DIFFICULT_STEP - difficult))) == 0) {
            this.speed = 1 + difficult / DIFFICULT_STEP;
        }

        if (this.removeAnimationTime > 0) {
            this.removeAnimationTime--;
            if (this.removeAnimationTime == 0) {
                this.removeAnimationTimeEnded();
            }
        }

        if (this.currentFigure != null) {
            if (this.bot != null) {
                this.bot.tick();
            } else {
                if (input.rotate.clicked) this.currentFigure.rotate(this.currentFigure.rot + 1);
                if (input.left.clicked) this.currentFigure.xa -= Art.BLOCK_SIZE;
                if (input.right.clicked) this.currentFigure.xa += Art.BLOCK_SIZE;
                this.currentFigure.down = input.down.down;
            }

            this.currentFigure.tick();
        }
    }

    public void render(Graphics2D g2d) {
        AffineTransform at = g2d.getTransform();

        g2d.translate(this.xOffset, this.yOffset);
        g2d.drawImage(Art.back, 0, 0, null);
        g2d.drawImage(Art.verticalLine, -this.xOffset, -this.yOffset, null);

        g2d.drawImage(Art.verticalLine, Level.W * Art.BLOCK_SIZE + 1, 0, null);

        if (this.currentFigure != null) {
            this.currentFigure.render(g2d);
        }

        if (this.nextFigure != null) {
            int xOffs = Level.W * Art.BLOCK_SIZE + 1 + Art.BLOCK_SIZE + (Art.BLOCK_SIZE * 5 - this.nextFigure.w * Art.BLOCK_SIZE) / 2;
            int yOffs = Art.BLOCK_SIZE;
            this.nextFigure.render(g2d, xOffs, yOffs);
        }

        int border = 1;

        for (int y = 0; y < Level.H; y++) {
            for (int x = 0; x < Level.W; x++) {
                int color = this.data[x + y * Level.W];
                if (color != -1) {
                    if (this.removeAnimationTime > 0) {
                        Boolean isLineRemoved = this.linesToBeRemoved.get(y);
                        if (isLineRemoved != null && isLineRemoved && (this.tickTime / 4) % 2 == 0) continue;
                    }
                    //color = 0xdfdfdf;
                    int x0 = x * Art.BLOCK_SIZE;
                    int x1 = x0 + Art.BLOCK_SIZE;
                    int y0 = y * Art.BLOCK_SIZE;
                    int y1 = y0 + Art.BLOCK_SIZE;

                    g2d.setColor(new Color(color));
                    g2d.fillRect(x0 + border, y0 + border, Art.BLOCK_SIZE - border, Art.BLOCK_SIZE - border);

                    g2d.setColor(new Color(color | 0x7f7f7f));
                    g2d.fillRect(x0 + border, y0 + border, Art.BLOCK_SIZE - border - border, border);
                    g2d.fillRect(x0 + border, y0 + border, border, Art.BLOCK_SIZE - border - border);

                    g2d.setColor(new Color(color & 0x7f7f7f));
                    g2d.fillRect(x1 - border, y0 + border + border, border, Art.BLOCK_SIZE - border - border);
                    g2d.fillRect(x0 + border + border, y1 - border, Art.BLOCK_SIZE - border - border, border);
                }
            }
        }

        g2d.setFont(this.smallFont);
        g2d.setColor(Color.WHITE);
        g2d.drawString("" + this.score, Level.W * Art.BLOCK_SIZE + 1 + Art.BLOCK_SIZE * 2, Art.BLOCK_SIZE * 8);

        g2d.setTransform(at);
    }

    private void validate() {
        this.linesToBeRemoved.clear();

        for (int y = 0; y < Level.H; y++) {
            boolean filled = true;
            for (int x = 0; x < Level.W; x++) {
                if (this.data[x + y * Level.W] == -1) {
                    filled = false;
                    break;
                }
            }
            if (filled) this.linesToBeRemoved.put(y, true);
        }
        if (!this.linesToBeRemoved.isEmpty()) {
            this.removeAnimationTime = 30;
        }
    }

    private void removeAnimationTimeEnded() {
        for (int y = 0; y < Level.H; y++) {
            Boolean isLinesRemoved = this.linesToBeRemoved.get(y);
            if (isLinesRemoved == null || !isLinesRemoved) continue;
            for (int x = 0; x < Level.W; x++) {
                this.data[x + y * Level.W] = -1;
            }
            int[] ups = Arrays.copyOfRange(this.data, 0, Level.W + (y - 1) * Level.W);
            for (int yy = y; yy >= 0; yy--) {
                for (int xx = 0; xx < Level.W; xx++) {
                    this.data[xx + yy * Level.W] = -1;
                    if (yy - 1 >= 0) {
                        this.data[xx + yy * Level.W] = ups[xx + (yy - 1) * Level.W];
                    }
                }
            }
        }
        this.score += this.linesToBeRemoved.size() * 100;
        this.linesToBeRemoved.clear();
        this.generateNextFigure();
    }

    public void dropFigure(Figure figure) {
        if (figure.removed) return;

        if (figure.y / Art.BLOCK_SIZE <= 2) {
            System.out.println("Scored: " + this.score);
            this.gameScreen.restart();
            return;
        }
        int x0 = figure.x / Art.BLOCK_SIZE;
        int y0 = figure.y / Art.BLOCK_SIZE;
        int x1 = x0 + figure.w;
        int y1 = y0 + figure.h;
        if (x0 < 0) x0 = 0;
        if (y0 < 0) y0 = 0;
        if (x1 > Level.W) x1 = Level.W;
        if (y1 > Level.H) y1 = Level.H;

        for (int y = y0; y < y1; y++) {
            int yf = y - y0;
            for (int x = x0; x < x1; x++) {
                int xf = x - x0;
                int pf = yf * figure.w + xf;
                int p = y * Level.W + x;
                if (figure.data[pf] != 0) {
                    this.data[p] = figure.color;
                }
            }
        }
        figure.die();
        this.validate();
        if (this.linesToBeRemoved.isEmpty()) {
            this.generateNextFigure();
        }
    }

    public static int calcPerimeter(int[] levelData) {
        int result = 0;
        /*for (int y = 0; y < Level.H; y++) {
            for (int x = 0; x < Level.W; x++) {
                if (Level.getData(x, y, levelData) == -1) continue;
                result += Level.getData(x - 1, y, levelData) == -1 ? 1 : 0;
                result += Level.getData(x + 1, y, levelData) == -1 ? 1 : 0;
                result += Level.getData(x, y - 1, levelData) == -1 ? 1 : 0;
                result += Level.getData(x, y + 1, levelData) == -1 ? 1 : 0;
            }
        }*/
        for (int y = 0; y < Level.H; y++) {
            for (int x = 0; x < Level.W; x++) {
                if (Level.getData(x, y, levelData) != -1) result++;
            }
        }
        return result;
    }

    public static int getData(int x, int y, int[] levelData) {
        if (x < 0 || y < 0 || x >= Level.W || y >= Level.H) return -1;
        return levelData[x + y * Level.W];
    }
}
