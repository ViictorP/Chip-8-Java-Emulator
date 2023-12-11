package emu;

import chip.Chip;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Screen extends Canvas {

    private static final int PIXEL_SIZE = 10;

    private GraphicsContext graphicsContext;

    // Representa os pixels da dela monocromatica.
    private byte[] display;

    public Screen() {
        super(640, 320);
        display = new byte[64 * 32];

        graphicsContext = this.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 640, 320);
    }

    public void refresh() {
        for(int i = 0; i < display.length; i++) {
            if(display[i] == 1) {
                graphicsContext.setFill(Color.WHITE);
            } else {
                graphicsContext.setFill(Color.BLACK);
            }

            int x = (i % 64);
            int y = (int)Math.floor(i / 64);

            graphicsContext.fillRect(x*PIXEL_SIZE, (y*PIXEL_SIZE), PIXEL_SIZE, PIXEL_SIZE);
        }
    }

    public void clearDisplay() {
        for (int i = 0; i < display.length; i++) {
            display[i] = 0;
        }
    }

    public byte[] getDisplay() {
        return display;
    }
}
