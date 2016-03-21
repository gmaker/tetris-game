package com.know.know.tetris.tools;

/**
 * mr.gmaker@yandex.ru
 */
public class Mth {
    public static int lerpColor(int c0, int c1, double t) {
        int c0r = (c0 >> 16) & 0xff;
        int c0g = (c0 >> 8) & 0xff;
        int c0b = (c0 >> 0) & 0xff;
        int c1r = (c1 >> 16) & 0xff;
        int c1g = (c1 >> 8) & 0xff;
        int c1b = (c1 >> 0) & 0xff;

        int r = (int) (c0r + (c1r - c0r) * t);
        int g = (int) (c0g + (c1g - c0g) * t);
        int b = (int) (c0b + (c1b - c0b) * t);
        return (r << 16) | (g << 8) + b;
    }

}
