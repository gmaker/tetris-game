package com.know.know.tetris;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * mr.gmaker@yandex.ru
 */
public class Input implements KeyListener {
    private static final List<Key> keys = new ArrayList<Key>();
    public Key left = new Key();
    public Key right = new Key();
    public Key down = new Key();
    public Key rotate = new Key();

    public Input(Canvas canvas) {
        canvas.addKeyListener(this);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        toggle(e, true);
    }

    public void keyReleased(KeyEvent e) {
        toggle(e, false);
    }

    public void toggle(KeyEvent e, boolean pressed) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) this.down.toggle(pressed);
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) this.left.toggle(pressed);
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) this.right.toggle(pressed);
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W
                || e.getKeyCode() == KeyEvent.VK_SPACE) this.rotate.toggle(pressed);
    }

    public static class Key {
        private int presses, absorbs;
        public boolean down, clicked;

        public Key() {
            keys.add(this);
        }

        public void tick() {
            if (this.absorbs < this.presses) {
                this.absorbs++;
                this.clicked = true;
            } else {
                this.clicked = false;
            }
        }

        public void toggle(boolean pressed) {
            if (pressed != this.down) {
                this.down = pressed;
            }

            if (pressed) {
                this.presses++;
            }
        }
    }

    public void tick() {
        for (Key key : keys) {
            key.tick();
        }
    }
}
