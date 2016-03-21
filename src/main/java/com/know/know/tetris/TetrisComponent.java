package com.know.know.tetris;

import com.know.know.tetris.gfx.Art;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * ========== ItCorp v. 1.0 class library ==========
 * <p/>
 * http://www.it.ru/
 * <p/>
 * &copy; Copyright 1990-2013, by ItCorp.
 * <p/>
 * ========== TetrisComponent.java ==========
 * <p/>
 * $Revision:  $<br/>
 * $Author:  $<br/>
 * $HeadURL:  $<br/>
 * $Id:  $
 * <p/>
 * 15.08.14 16:11: Original version (ASTarasov)<br/>
 */
public class TetrisComponent extends Canvas implements Runnable {
    public static final boolean DEBUG = false;
    public static final int WIDTH = 10 * Art.BLOCK_SIZE + Art.BLOCK_SIZE * 7;
    public static final int HEIGHT = 20 * Art.BLOCK_SIZE;
    public static final int SCALE = 4;
    private Thread thread;
    private boolean running = false;
    private BufferedImage screenImage;
    private Input input;
    private Game game;

    public synchronized void start() {
        if (this.running) return;
        this.running = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public synchronized void stop() {
        if (!this.running) return;
        this.running = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            //ignore
        }
    }

    public void run() {
        this.init();
        long lastTime = System.nanoTime();
        double iNsPerSec = 60.0 / 1000000000.0;
        double unprocessed = 0.0;
        int ticks = 0;
        int frames = 0;
        long lastFPSTime = System.currentTimeMillis();
        while (this.running) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) * iNsPerSec;
            lastTime = now;
            while (unprocessed >= 1.0) {
                unprocessed -= 1.0;
                this.tick();
                ticks++;
            }

            this.render();
            frames++;

            if (System.currentTimeMillis() - lastFPSTime > 1000) {
                lastFPSTime += 1000;
                System.out.println("frames: " + frames + " ticks: " + ticks);
                frames = ticks = 0;
            }

            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    public void init() {
        Art.init();
        this.input = new Input(this);
        this.screenImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.game = new Game();
    }

    public void tick() {
        this.input.tick();
        this.game.tick(this.input);
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            requestFocus();
            return;
        }

        Graphics2D g2d = this.screenImage.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        this.game.render(g2d);
        g2d.dispose();

        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int xOffset = (getWidth() - WIDTH * SCALE) / 2;
        int yOffset = (getHeight() - HEIGHT * SCALE) / 2;

        g.drawImage(this.screenImage, xOffset, yOffset, WIDTH * SCALE, HEIGHT * SCALE, null);
        g.dispose();

        bs.show();
    }

    public static void main(String[] args) {
        TetrisComponent game = new TetrisComponent();
        Dimension d = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
        game.setMinimumSize(d);
        game.setMaximumSize(d);
        game.setPreferredSize(d);

        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        game.start();
    }
}
