package emu;

import chip.Chip;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        ChipStage chipStage = new ChipStage(new Chip());
    }
}
