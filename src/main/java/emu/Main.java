package emu;

import chip.Chip;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;

public class Main extends Application {

    private Stage stage;
    private Timeline loop;

    private Screen screen;
    private Chip chip;

    private int countTest = 0;


    private void initialize() {
        stage.setTitle("Chip-8 Emulator");

        chip = new Chip();
        chip.init();

        StackPane stackPane = new StackPane();
        screen = new Screen(chip.getDisplay());
        stackPane.getChildren().add(screen);
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);



        loop = new Timeline();
        loop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.003), actionEvent -> {
            try {
                //chip.run();
            } catch (RuntimeException e) {
                loop.stop();
            }

            if (countTest < 2048) {
                chip.test();
                screen.refresh();
                countTest++;
            } else {
                loop.stop();
            }
        });

        loop.getKeyFrames().add(keyFrame);
        loop.play();


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
