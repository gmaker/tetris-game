package com.know.know.tetris.screen;

import com.know.know.tetris.Game;
import com.know.know.tetris.Input;

import java.awt.Graphics2D;
import java.util.Random;

/**
 * mr.gmaker@yandex.ru
 */
public abstract class Screen {
    protected static final Random random = new Random();
    protected Game game;

    public void setScreen(Screen screen) {
        this.game.setScreen(screen);
    }

    public void init(Game game) {
        this.game = game;
    }

    public abstract void tick(Input input);

    public abstract void render(Graphics2D g2d);
}
