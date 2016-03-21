package com.know.know.tetris.ai;

import com.know.know.tetris.figure.Figure;
import com.know.know.tetris.figure.FigureData;
import com.know.know.tetris.level.Level;

import java.util.Random;

/**
 * mr.gmaker@yandex.ru
 */
public class StupidBot extends Bot {
    private static final Random random = new Random();

    public StupidBot(String name) {
        super(name);
    }

    protected CalculationResult doCalculateTurn(FigureData currentFigureData, FigureData nextFigureData, Level level) {
        return new CalculationResult(random.nextInt(11 - currentFigureData.w), random.nextInt(3));
    }
}
