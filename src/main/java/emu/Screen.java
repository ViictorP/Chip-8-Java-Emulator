package emu;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Screen extends Canvas {

    private static final int PIXEL_SIZE = 10;
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;

    private GraphicsContext graphicsContext;
    private byte[][] display;

    public Screen(byte[][] display) {
        super(640, 320);
        this.display = display;

        graphicsContext = this.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 640, 320);
        clearDisplay();
    }

    public void clearDisplay() {
        for(int x = 0; x < WIDTH; x++) {

            for(int y = 0; y < HEIGHT; y++) {
                display[x][y] = 0;
            }
        }
    }

    public void refresh() {
        for(int x = 0; x < display.length; x++) {
            for(int y = 0; y < display[y].length; y++) {
                if (display[x][y] == 1) {
                    graphicsContext.setFill(Color.WHITE);
                } else {
                    graphicsContext.setFill(Color.BLACK);
                }

                graphicsContext.fillRect(x*PIXEL_SIZE, (y*PIXEL_SIZE), PIXEL_SIZE, PIXEL_SIZE);
            }
        }
    }
}
