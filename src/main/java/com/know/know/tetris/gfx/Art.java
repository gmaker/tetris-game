package com.know.know.tetris.gfx;

import com.know.know.tetris.level.Level;
import com.know.know.tetris.tools.Mth;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * mr.gmaker@yandex.ru
 */
public class Art {

    public static final int BLOCK_SIZE = 8;

    public static int[][] figuresData = {
            {
                    4, 1,        //w, h
                    0x00F0F0,    //color
                    1, 1, 1, 1   // data
            },

            {
                    3, 2,
                    0x0000F0,
                    1, 0, 0,
                    1, 1, 1
            },

            {
                    3, 2,
                    0xF0A000,
                    0, 0, 1,
                    1, 1, 1
            },

            {
                    2, 2,
                    0xF0F000,
                    1, 1,
                    1, 1
            },

            {
                    3, 2,
                    0x00F000,
                    0, 1, 1,
                    1, 1, 0
            },

            {
                    3, 2,
                    0xA000F0,
                    0, 1, 0,
                    1, 1, 1
            },

            {
                    3, 2,
                    0xF00000,
                    1, 1, 0,
                    0, 1, 1
            },
    };

    public static BufferedImage back = createGradient(Level.W * Art.BLOCK_SIZE + 1, Level.H * Art.BLOCK_SIZE, 0x4f4f4f, 0x200000);
    public static BufferedImage verticalLine = createGradientBar(Art.BLOCK_SIZE, Level.H * Art.BLOCK_SIZE, 0x7f7f7f, 0);

    public static BufferedImage createGradientBar(int w, int h, int from, int to) {
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double xp = (x / (double) (w - 1)) - .5;
                double yp = (y / (double) (h - 1));
                double p = xp + (yp - xp) * (x + y * w) / (double) (w * h);
                pixels[x + y * w] = Mth.lerpColor(from, to, Math.abs(xp));
            }
        }
        return result;
    }

    public static BufferedImage createGradient(int w, int h, int from, int to) {
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                double p = (y / (double) h);
                p = (Math.sqrt(p) + p) * 0.5;
                pixels[x + y * w] = Mth.lerpColor(from, to, p);
            }
        }
        return result;
    }

    public static void init() {
    }
}
