package com.know.know.tetris.figure;

import java.util.Arrays;

/**
 * mr.gmaker@yandex.ru
 */
public class FigureData {
    public int color;
    public int w;
    public int h;
    public int[] data;
    public int[] sourceData;
    public int rot;

    public FigureData(int color, int w, int h, int[] sourceData) {
        this.color = color;
        this.w = w;
        this.h = h;
        this.sourceData = sourceData;
        this.rot = 0;
        this.data = Arrays.copyOf(sourceData, sourceData.length);
    }

    public FigureData(FigureData figureData) {
        importData(figureData);
    }

    public void importData(FigureData figureData) {
        this.color = figureData.color;
        this.w = figureData.w;
        this.h = figureData.h;
        this.sourceData = figureData.sourceData;
        this.rot = figureData.rot;
        this.data = Arrays.copyOf(figureData.data, figureData.data.length);
    }

    public static FigureData rotate(FigureData figureData, int toRot) {
        FigureData result = new FigureData(figureData);
        toRot &= 3;
        while (result.rot != toRot) {
            result.rot = (result.rot + 1) & 3;
            int temp = result.w;
            result.w = result.h;
            result.h = temp;
            int[] tempData = Arrays.copyOf(result.data, result.data.length);
            for (int x = 0; x < result.w; x++) {
                for (int y = 0; y < result.h; y++) {
                    result.data[x + y * result.w] = tempData[y + (result.w - x - 1) * result.h];
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "FigureData{" +
                "color=" + color +
                ", w=" + w +
                ", h=" + h +
                ", data=" + Arrays.toString(data) +
                ", sourceData=" + Arrays.toString(sourceData) +
                ", rot=" + rot +
                '}';
    }
}
