package emu;

import chip.Chip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ChipStage extends Stage {

    private ChipScene scene;
    public ChipStage(Chip chip) {

        scene = new ChipScene(new GridPane(), chip);
        setScene(scene);
        sizeToScene();
        setResizable(false);
        show();
    }
}
