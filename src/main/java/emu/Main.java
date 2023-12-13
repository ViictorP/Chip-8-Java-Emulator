package emu;

import chip.Chip;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class Main extends Application {

    private Stage stage;
    private static Timeline loop;
    private Screen screen;
    private Chip chip;
    private Keyboard keyboard;


    private void initialize() {
        stage.setTitle("Chip-8 Emulator");

        screen = new Screen();
        keyboard = new Keyboard();
        chip = new Chip(screen, keyboard);
        chip.init();

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem loadRom = new MenuItem("Load ROM");
        loadRom.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select ROM");
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                chip.gameLoader(file.getPath());
            }
        });

        menu.getItems().add(loadRom);
        menuBar.getMenus().add(menu);

        VBox vBox = new VBox();
        vBox.getChildren().add(menuBar);
        vBox.getChildren().add(screen);

        Scene scene = new Scene(vBox);
        scene.setOnKeyPressed(e -> keyboard.setKeysPressed(e.getCode()));
        scene.setOnKeyReleased(e -> keyboard.setKeysUnpressed(e.getCode()));
        stage.setScene(scene);

        loop = new Timeline();
        loop.setCycleCount(Timeline.INDEFINITE);

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.006), actionEvent -> {
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

    public static Timeline getLoop() {
        return loop;
    }
}
