package emu;

import chip.Chip;
import javafx.scene.input.KeyCode;

import java.util.Arrays;

public class Keyboard {

    // byte[] keys resenta os teclas do teclado.
    private boolean[] keys;

    public Keyboard() {
        keys = new boolean[16];

        Arrays.fill(keys, false);
    }

    public void setKeysPressed(KeyCode key) {
        switch (key) {
            case DIGIT0:
                keys[0] = true;
                System.out.println("0");
                break;

            case DIGIT1:
                keys[1] = true;
                break;

            case DIGIT2:
                keys[2] = true;
                break;

            case DIGIT3:
                keys[3] = true;
                break;

            case DIGIT4:
                keys[4] = true;
                break;

            case DIGIT5:
                keys[5] = true;
                break;

            case DIGIT6:
                keys[6] = true;
                break;

            case DIGIT7:
                keys[7] = true;
                break;

            case DIGIT8:
                keys[8] = true;
                break;

            case DIGIT9:
                keys[9] = true;
                break;

            case A:
                keys[10] = true;
                break;

            case B:
                keys[11] = true;
                break;

            case C:
                keys[12] = true;
                break;

            case D:
                keys[13] = true;
                break;

            case E:
                keys[14] = true;
                break;

            case F:
                keys[15] = true;
                break;
        }
    }

    public void setKeysUnpressed(KeyCode key) {
        switch (key) {
            case DIGIT0:
                keys[0] = false;
                break;

            case DIGIT1:
                keys[1] = false;
                break;

            case DIGIT2:
                keys[2] = false;
                break;

            case DIGIT3:
                keys[3] = false;
                break;

            case DIGIT4:
                keys[4] = false;
                break;

            case DIGIT5:
                keys[5] = false;
                break;

            case DIGIT6:
                keys[6] = false;
                break;

            case DIGIT7:
                keys[7] = false;
                break;

            case DIGIT8:
                keys[8] = false;
                break;

            case DIGIT9:
                keys[9] = false;
                break;

            case A:
                keys[10] = false;
                break;

            case B:
                keys[11] = false;
                break;

            case C:
                keys[12] = false;
                break;

            case D:
                keys[13] = false;
                break;

            case E:
                keys[14] = false;
                break;

            case F:
                keys[15] = false;
                break;
        }
    }

    public boolean isPressed(int i) {
        return keys[i];
    }
}
