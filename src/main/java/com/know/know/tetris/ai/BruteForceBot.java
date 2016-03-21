package com.know.know.tetris.ai;

import com.know.know.tetris.figure.FigureData;
import com.know.know.tetris.level.Level;

import java.util.Arrays;

/**
 * mr.gmaker@yandex.ru
 */
public class BruteForceBot extends Bot {

    public BruteForceBot(String name) {
        super(name);
    }

    private synchronized static void printLevel(int[] levelData) {
        for (int y = 0; y < Level.H; y++) {
            String line = "";
            for (int x = 0; x < Level.W; x++) {
                line += " " + (levelData[x + y * Level.W] == -1 ? 0 : "x");
            }
            System.out.println(line);
        }
    }

    protected CalculationResult doCalculateTurn(FigureData currentFigureData, FigureData nextFigureData, Level level) {
        CalculationResult result = null;

        double minCost = -1;
        for (int x = 0; x < Level.W; x++) {
            for (int r = 0; r < 4; r++) {
                int[] levelData = Arrays.copyOf(level.data, level.data.length);
                double cost4First = calcCost(currentFigureData, levelData, x, r);
                if (cost4First < 0) continue;

                double minCost4Second = -1;
                for (int xx = 0; xx < Level.W; xx++) {
                    for (int rr = 0; rr < 4; rr++) {
                        double cost4Second = calcCost(nextFigureData, Arrays.copyOf(levelData, levelData.length), xx, rr);
                        if (cost4Second == -1) continue;
                        if (minCost4Second == -1 || cost4Second < minCost4Second) {
                            minCost4Second = cost4Second;
                        }
                    }
                }
                if (minCost4Second < 0) continue;

                double cost = cost4First + minCost4Second;
                if (minCost == -1 || cost < minCost) {
                    result = new CalculationResult(x, r);
                    minCost = cost;
                }
            }
        }
        return result;
    }

    protected double calcCost(FigureData figureData, int[] levelData, int x, int r) {
        figureData = FigureData.rotate(figureData, r);
        if (figureData.w + x > Level.W) return -1;
        int[] newLevelData = dropFigureData(figureData, levelData, x);
        double perimeter = (Level.calcPerimeter(newLevelData) / 200.0);
        double height = (getMaxHeight(newLevelData) / 20.0);
        double holes = (getHoles(newLevelData) / 100.0);
        return height * 1.5 + perimeter * 0.5 +(holes + .5) * 5.5;
    }

    private static int getHoles(int[] levelData) {
        int result = 0;
        for (int x = 0; x < Level.W; x++) {
            for (int yLine = 1; yLine < Level.H; yLine++) {
                boolean isEmpty = levelData[x + yLine * Level.W] == -1;
                isEmpty &= levelData[x + (yLine - 1) * Level.W] != -1;
                result += isEmpty ? 1 : 0;
            }
        }
        return result;
    }

    private static int getMaxHeight(int[] levelData) {
        for (int y = 0; y < Level.H; y++) {
            for (int x = 0; x < Level.W; x++) {

                if (levelData[x + y * Level.W] != -1) {
                    return Level.H - y;
                }
            }
        }
        return Level.H;
    }

    protected static int[] dropFigureData(FigureData figureData, int[] levelData, int xx) {
        int x0 = xx;
        int x1 = x0 + figureData.w;

        //get y coord
        int yResult = 0;
        final int empty = -1;
        searchYLine:
        for (int yLine = 0; yLine <= Level.H - figureData.h; yLine++) {
            for (int x = x0; x < x1; x++) {
                for (int y = 0; y < figureData.h; y++) {
                    int pf = (x - x0) + y * figureData.w;
                    int pl = x + (y + yLine) * Level.W;
                    if (figureData.data[pf] != 0 && levelData[pl] != empty) {
                        break searchYLine;
                    }

                }
            }
            yResult = yLine;
        }

        //fill figure
        for (int x = 0; x < figureData.w; x++) {
            for (int y = 0; y < figureData.h; y++) {
                if (figureData.data[x + y * figureData.w] != 0) {
                    levelData[(x + xx) + (y + yResult) * Level.W] = figureData.data[x + y * figureData.w];
                }
            }
        }

        //check and remove lines
        for (int yLine = 0; yLine < Level.H; yLine++) {
            boolean filled = true;
            for (int xScan = 0; xScan < Level.W; xScan++) {
                if (levelData[xScan + yLine * Level.W] == empty) {
                    filled = false;
                    break;
                }
            }
            if (filled) {
                for (int xScan = 0; xScan < Level.W; xScan++) {
                    levelData[xScan + yLine * Level.W] = empty;
                }
                int[] ups = Arrays.copyOfRange(levelData, 0, Level.W + (yLine - 1) * Level.W);
                for (int yy = yLine; yy >= 0; yy--) {
                    for (int xxx = 0; xxx < Level.W; xxx++) {
                        levelData[xxx + yy * Level.W] = empty;
                        if (yy - 1 >= 0) {
                            levelData[xxx + yy * Level.W] = ups[xxx + (yy - 1) * Level.W];
                        }
                    }
                }
            }
        }

        return levelData;
    }

    public static void main(String[] args) {
        int[] testData = {
                -1, -1, -1, -1,
                -1, -1, -1, -1,
                -1, -1, -1, -1,
                -1, -1, -1, -1,
                -1, -1, -1, -1,
                -1, -1, -1, -1,
                -1, 1, -1, -1,
                -1, -1, -1, -1,
        };

        Level.W = 4;
        Level.H = 8;
        FigureData figureData = new FigureData(200, 2, 2,
                new int[]{
                        1, 1,
                        1, 1
                }
        );

        System.out.println("figure: " + figureData);

        testData = dropFigureData(figureData, testData, 0);

        printLevel(testData);
    }
}
