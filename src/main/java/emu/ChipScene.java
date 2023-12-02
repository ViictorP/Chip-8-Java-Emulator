package emu;

import chip.Chip;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ChipScene extends Scene {
    private static final int PIXEL_SIZE = 10;
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;
    private Chip chip;
    private GridPane pane;

    public ChipScene(GridPane pane, Chip chip) {
        super(pane);
        this.chip = chip;
        this.pane = pane;
        chip.init();
        paint();
    }

    public void paint() {
        byte[] display = chip.getDisplay();
        int index = 0;

        for (int y = 0; y < WIDTH; y++) {

            for (int x = 0; x < HEIGHT; x++) {
                Rectangle pixel = new Rectangle(PIXEL_SIZE, PIXEL_SIZE);
                if (display[index] == 0) {
                    pixel.setFill(Color.BLACK);
                } else {
                    pixel.setFill(Color.WHITE);
                }

                pane.add(pixel, y, x);
            }
        }
    }
}
