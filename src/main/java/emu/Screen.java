package emu;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Screen {

    private static final int PIXEL_SIZE = 10;
    private static final int WIDTH = 64;
    private static final int HEIGHT = 32;

    private Scene scene;
    private GridPane gridPane;
    private byte[] display;

    public Screen(Scene scene, GridPane gridPane, byte[] display) {
        this.scene = scene;
        this.gridPane = gridPane;
        this.display = display;
    }

    public void paint() {
        int index = 0;

        for (int y = 0; y < WIDTH; y++) {

            for (int x = 0; x < HEIGHT; x++) {
                Rectangle pixel = new Rectangle(PIXEL_SIZE, PIXEL_SIZE);
                if (display[index] == 0) {
                    pixel.setFill(Color.BLACK);
                } else {
                    pixel.setFill(Color.WHITE);
                }

                gridPane.add(pixel, y, x);
                index++;
            }
        }
    }
}
