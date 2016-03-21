package com.know.know.tetris.screen;

import com.know.know.tetris.Input;
import com.know.know.tetris.ai.Bot;
import com.know.know.tetris.ai.StupidBot;
import com.know.know.tetris.gfx.Art;
import com.know.know.tetris.level.Level;

import java.awt.Graphics2D;

/**
 * mr.gmaker@yandex.ru
 */
public class GameScreen extends Screen {
    private Level level;

    public GameScreen() {
        restart();
    }

    public void tick(Input input) {
        this.level.tick(input);
    }

    public void render(Graphics2D g2d) {
        this.level.render(g2d);
    }

    public void restart() {
        this.level = new Level(this, Art.BLOCK_SIZE, 0, Bot.brutForce);
        this.level.generateNextFigure();
        this.level.generateNextFigure();
    }
}
