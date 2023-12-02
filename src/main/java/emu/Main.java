package emu;

import chip.Chip;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;

    private Screen screen;

    private Chip chip;

    private void initialize() {
        stage.setTitle("Chip-8 Emulator");
        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);

        chip = new Chip();
        chip.init();

        screen = new Screen(scene, gridPane, chip.getDisplay());
        screen.paint();

        stage.sizeToScene();
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        initialize();
    }
}
