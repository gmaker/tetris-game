package com.know.know.tetris;

import com.know.know.tetris.screen.GameScreen;
import com.know.know.tetris.screen.Screen;

import java.awt.Graphics2D;

/**
 * mr.gmaker@yandex.ru
 */
public class Game {
    private Screen screen;

    public Game() {
        this.setScreen(new GameScreen());
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
        this.screen.init(this);
    }

    public void tick(Input input) {
        this.screen.tick(input);
    }

    public void render(Graphics2D g2d) {
        this.screen.render(g2d);
    }
}
