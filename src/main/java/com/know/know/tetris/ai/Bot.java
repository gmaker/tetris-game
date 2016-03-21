package com.know.know.tetris.ai;

import com.know.know.tetris.figure.Figure;
import com.know.know.tetris.figure.FigureData;
import com.know.know.tetris.gfx.Art;
import com.know.know.tetris.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * mr.gmaker@yandex.ru
 */
public abstract class Bot {
    public static Bot stupid = new StupidBot("av");
    public static Bot brutForce = new BruteForceBot("ig");

    private Map<Integer, CalculationResult> resultMap = new HashMap<Integer, CalculationResult>();
    public String name;
    public Level level;

    public Bot(String name) {
        this.name = name;
    }

    public void init(Level level) {
        this.level = level;
        resultMap.clear();
    }

    public void tick() {
        int turn = level.turn;
        if (!this.resultMap.containsKey(turn)) {
            this.resultMap.put(turn, doCalculateTurn(level.currentFigure, level.nextFigure, level));
        }

        CalculationResult cr = this.resultMap.get(turn);
        level.currentFigure.rotate(cr.targetRot);
        int xDiff = level.currentFigure.x / Art.BLOCK_SIZE - cr.targetX;
        if (Math.abs(xDiff) != 0) {
            int speed = 4;
            level.currentFigure.xa += xDiff > 0 ? -speed : speed;
        } else {
            level.currentFigure.down = true;
        }
    }

    protected abstract CalculationResult doCalculateTurn(FigureData currentFigureData, FigureData nextFigureData, Level level);

    protected static class CalculationResult {
        int targetX;
        int targetRot;

        public CalculationResult(int targetX, int targetRot) {
            this.targetX = targetX;
            this.targetRot = targetRot;
        }

        @Override
        public String toString() {
            return "CalculationResult{" +
                    "targetX=" + targetX +
                    ", targetRot=" + targetRot +
                    '}';
        }
    }

}
