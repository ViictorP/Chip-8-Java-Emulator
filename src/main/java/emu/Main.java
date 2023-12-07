package emu;

import chip.Chip;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private Stage stage;
    private Timeline loop;

    private Screen screen;
    private Chip chip;
    private Keyboard keyboard;


    private void initialize() {
        screen = new Screen();
        keyboard = new Keyboard();
        chip = new Chip(screen, keyboard);
        chip.init();
        chip.gameLoader("src/main/resources/games/pong2.c8");

        stage.setTitle("Chip-8 Emulator");
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(screen);
        Scene scene = new Scene(stackPane);

        scene.setOnKeyPressed(e -> keyboard.setKeysPressed(e.getCode()));
        scene.setOnKeyReleased(e -> keyboard.setKeysUnpressed(e.getCode()));

        stage.setScene(scene);

        loop = new Timeline();
        loop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.003), actionEvent -> {
            try {
                chip.run();
                if (chip.needsRedraw()) {
                    screen.refresh();
                    chip.removeDrawFlag();
                }
            } catch (RuntimeException e) {
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
