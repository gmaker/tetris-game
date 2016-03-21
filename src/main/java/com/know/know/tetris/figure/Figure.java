package com.know.know.tetris.figure;

import com.know.know.tetris.gfx.Art;
import com.know.know.tetris.level.Level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;

/**
 * mr.gmaker@yandex.ru
 */
public class Figure extends FigureData {
    public int x;
    public int y;
    public double xa = 0;
    public double ya = 0;
    public Level level;
    public boolean onGround = false;
    public boolean removed = false;
    public boolean down = false;

    public Figure(int x, int y, int color, int w, int h, int[] sourceData) {
        super(color, w, h, sourceData);
        this.x = x;
        this.y = y;
        this.x = this.x / Art.BLOCK_SIZE * Art.BLOCK_SIZE;
        this.y = this.y / Art.BLOCK_SIZE * Art.BLOCK_SIZE;
    }

    public static Figure createFigure(int x, int y, int[] fullData) {
        int w = fullData[0];
        int h = fullData[1];
        int c = fullData[2];
        int[] data = Arrays.copyOfRange(fullData, 3, fullData.length);
        return new Figure(x, y, c, w, h, data);
    }

    public void rotate(int rots) {
        FigureData tempFigure = rotate(this, rots);
        if (this.canMove(0, 0, tempFigure)) {
            this.importData(tempFigure);
        }
    }

    public boolean canMove(double xxa, double yya) {
        return canMove(xxa, yya, this);
    }

    public boolean canMove(double xxa, double yya, FigureData figureData) {
        int x0 = (int) (this.x + xxa) / Art.BLOCK_SIZE;
        int y0 = (int) (this.y + yya) / Art.BLOCK_SIZE;
        int x1 = x0 + figureData.w;
        int y1 = y0 + figureData.h;

        if (x0 < 0 || x1 > Level.W || y1 > Level.H) {
            return false;
        }

        if (x0 < 0) x0 = 0;
        if (y0 < 0) y0 = 0;
        if (x1 > Level.W) x1 = Level.W;
        if (y1 > Level.H) y1 = Level.H;

        for (int x = x0; x < x1; x++) {
            int lx = x - x0;
            for (int y = y0; y < y1; y++) {
                int ly = y - y0;
                int lp = lx + ly * figureData.w;
                if (figureData.data[lp] == 1 && Level.getData(x, y, this.level.data) != -1) {
                    return false;
                }
            }
        }

        return true;
    }

    public void init(Level level) {
        this.level = level;
    }

    private void attemptMove() {
        this.onGround = false;
        int xSteps = (int) Math.abs(xa * 100) + 1;
        double xxa = this.xa;
        for (int i = xSteps; i > 0; i--) {
            if (canMove(xxa * i / xSteps, 0)) {
                this.x += xxa * i / xSteps;
                break;
            }
        }

        this.xa = 0;

        int ySteps = (int) Math.abs(ya * 100) + 1;
        double yya = this.ya;
        for (int i = ySteps; i > 0; i--) {
            if (canMove(0, yya * i / ySteps)) {
                this.y += yya * i / ySteps;
                break;
            } else {
                this.onGround = true;
                this.ya = 0;
            }
        }
    }

    public void tick() {
        if (this.removed) return;
        this.ya = this.level.speed + (this.down ? 5 : 0);
        attemptMove();
        if (this.onGround) {
            this.level.dropFigure(this);
        }
    }

    public void render(Graphics2D g2d, int xPos, int yPos) {
        if (this.removed) return;

        int border = 1;

        for (int y = 0; y < this.h; y++) {
            for (int x = 0; x < this.w; x++) {
                if (this.data[x + y * this.w] != 0) {
                    int x0 = xPos + x * Art.BLOCK_SIZE;
                    int x1 = x0 + Art.BLOCK_SIZE;
                    int y0 = yPos + y * Art.BLOCK_SIZE;
                    int y1 = y0 + Art.BLOCK_SIZE;

                    g2d.setColor(new Color(this.color));
                    g2d.fillRect(x0 + border, y0 + border, Art.BLOCK_SIZE - border, Art.BLOCK_SIZE - border);

                    g2d.setColor(new Color(this.color | 0x7f7f7f));
                    g2d.fillRect(x0 + border, y0 + border, Art.BLOCK_SIZE - border - border, border);
                    g2d.fillRect(x0 + border, y0 + border, border, Art.BLOCK_SIZE - border - border);

                    g2d.setColor(new Color(this.color & 0x7f7f7f));
                    g2d.fillRect(x1 - border, y0 + border + border, border, Art.BLOCK_SIZE - border - border);
                    g2d.fillRect(x0 + border + border, y1 - border, Art.BLOCK_SIZE - border - border, border);
                }
            }
        }
    }

    public void render(Graphics2D g2d) {
        int xx = this.x / Art.BLOCK_SIZE * Art.BLOCK_SIZE;
        int yy = this.y / Art.BLOCK_SIZE * Art.BLOCK_SIZE;
        this.render(g2d, xx, yy);
    }

    public void die() {
        this.removed = true;
    }
}
