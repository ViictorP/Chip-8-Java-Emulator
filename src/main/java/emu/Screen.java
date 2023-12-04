package emu;

import chip.Chip;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Screen extends Canvas {

    private static final int PIXEL_SIZE = 10;

    private GraphicsContext graphicsContext;
    private Chip chip;
    private byte[] display;

    public Screen(Chip chip) {
        super(640, 320);
        this.chip = chip;
        this.display = chip.getDisplay();

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
}
