package chip;

import emu.Keyboard;
import emu.Screen;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Chip {

    // char[] memory representa o memória do chip, 4kB / 4096 de memória 8-bits.
    private char[] memory;
    // char[] V representa os registers, são 16 registers de 8-bit nomeados em base hexadecimal de V0 até VF.
    private char[] V;
    // char I representa o endereço dos registers, 16-bit (só 12-bits são utilizados).
    private char I;

    private char pc;


    // char[] stack representa a callstack ou pilha de execução, permite até 16 niveis de nesting.
    private char[] stack;
    // Aponta para o próximo da pilha de execução.
    private int stackPointer;

    // Timer usado para delay eventos em programas e jogos.
    private int delay_timer;
    // Timer usado para fazer sons.
    private int sound_timer;

    private Screen screen;

    private Keyboard keyboard;

    private boolean needRedraw;

    public Chip(Screen screen, Keyboard keyboard) {
        this.screen = screen;
        this.keyboard = keyboard;
    }

    // Reseta a memória e os ponteiros do Chip-8
    public void init() {
        memory = new char[4096];
        V = new char[16];
        I = 0x0;
        pc = 0x200;

        stack = new char[16];
        stackPointer = 0;

        delay_timer = 0;
        sound_timer = 0;

        needRedraw = false;

        fontLoader();
    }

    public void run() {
        //fetch Opcode
        char opcode = (char)((memory[pc] << 8) | memory[pc + 1]);
        System.out.print(Integer.toHexString(opcode).toUpperCase() + ": ");

        //decode Opcode
        switch (opcode & 0xF000) {

            case 0x0000: {

                switch (opcode & 0x00FF) {

                    case 0x00E0: // Opcode: 00E0, Type: Display, Clears the screen.

                        System.err.println("Opcode não suportado");
                        System.exit(0);

                        break;

                    case 0x00EE: // Opcode: 00EE, Type: Flow, Returns from a subroutine.
                        stackPointer--;
                        pc = (char) (stack[stackPointer] + 2);
                        System.out.println("Return from a subroutine to " + Integer.toHexString(pc).toUpperCase());
                        break;

                    default: // Opcode: 0NNN, Type: Call, Calls machine code routine (RCA 1802 for COSMAC VIP) at address NNN. Not necessary for most ROMs.
                        System.err.println("Opcode não suportado");
                        System.exit(0);
                        break;

                }

                break;
            }

            case 0x1000: { // Opcode: 1NNN, Type: Flow, Jumps to address NNN.
                pc = (char) (opcode & 0x0FFF);
                System.out.println("Jump to address " + Integer.toHexString(pc).toUpperCase());
                break;
            }

            case 0x2000: { // Opcode: 2NNN, Type: Call, Calls subroutine at NNN.
                stack[stackPointer] = pc;
                stackPointer++;
                pc = (char) (opcode & 0x0FFF);
                System.out.println("Calling " + Integer.toHexString(pc).toUpperCase());
                break;
            }

            case 0x3000: { // Opcode: 3XNN, Type: Cond, Skips the next instruction if VX equals NN (usually the next instruction is a jump to skip a code block).
                int x = (opcode & 0x0F00) >> 8;
                int NN = (opcode & 0x00FF);
                if (V[x] == NN) {
                    pc += 4;
                    System.out.println("Skipping instruction (V[" + x + "] == " + NN + ")");
                } else {
                    pc += 2;
                    System.out.println("Not skipping instruction (V[" + x + "] != " + NN + ")");
                }
                break;
            }

            case 0x4000: { // Opcode: 4XNN, Type: Cond, Skips the next instruction if VX does not equal NN (usually the next instruction is a jump to skip a code block).

                int x = (opcode & 0x0F00) >> 8;
                int nn = opcode & 0x00FF;

                if (V[x] != nn) {
                    pc += 4;
                } else {
                    pc += 2;
                }

                break;
            }

            case 0x6000: { // Opcode: 6XNN, Type: Const, Sets VX to NN.
                int x = (opcode & 0x0F00) >> 8;
                int NN = (char)(opcode & 0x00FF);
                V[x] = (char) NN;
                pc += 2;
                System.out.println("Setting V[" + x + "] to " + (int)V[x]);
                break;
            }


            case 0x7000: { // Opcode: 7XNN, Type: Const, Adds NN to VX (carry flag is not changed).
                int x = (opcode & 0x0F00) >> 8;
                int NN = (opcode & 0x00FF);
                V[x] = (char) ((V[x] + NN) & 0xFF);
                pc += 2;
                System.out.println("Add " + NN + " to V[" + x + "] = " + (int)V[x]);
                break;
            }

            case 0x8000:

                switch (opcode & 0x000F) {

                    case 0x0002: { // Opcode: 8XY2, Type: BitOp, Sets VX to VX and VY. (bitwise AND operation)
                        int x = (opcode & 0x0F00) >> 8;
                        int y = (opcode & 0x00F0) >> 4;
                        System.out.println("Set V[" + x + "] to the bitwise AND operation between V[" + x + "] = " + (int)V[x] + " & V[" + y + "] = " + (int)V[y] + ", which is " + (V[x] & V[y]));
                        V[x] = (char) (V[x] & V[y]);

                        pc += 2;

                        break;
                    }

                    case 0x0004: { // Opcode: 8XY4, Type: Math, Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there is not.
                        int x = (opcode & 0x0F00) >> 8;
                        int y = (opcode & 0x00F0) >> 4;
                        System.out.print("Adding V[" + x + "] = " + (int)V[x]  + " to V[" + y + "] = " + (int)V[y]  + ", which is " + ((V[x] + V[y]) & 0xFF));
                        if (V[y] > 255 - V[x]) {
                            V[0xF] = 1;
                        } else {
                            V[0xF] = 0;
                        }
                        System.out.println(", is Carry needed? " + (int)V[0xF]);

                        V[x] = (char) ((V[x] + V[y]) & 0xFF);
                        pc += 2;
                        break;
                    }

                    default:
                        System.err.println("Opcode não suportado");
                        System.exit(0);
                        break;
                }

                break;

            case 0xA000: { // Opcode: ANNN, Type: MEM, Sets I to the address NNN.
                I = (char)(opcode & 0x0FFF);
                pc += 2;
                System.out.println("Set I to " + Integer.toHexString(I).toUpperCase());
                break;
            }

            case 0xC000: { // Opcode: CXNN, Type: Rand, Sets VX to the result of a bitwise and operation on a random number (Typically: 0 to 255) and NN.
                int x = (opcode & 0x0F00) >> 8;
                int nn = (opcode & 0x00FF);
                int random = (int)(Math.random() * 256);  // 0 to 255
                V[x] = (char) (random & nn);
                pc += 2;
                System.out.println("V[" + x + "] has been set to a random number = " + random);
                break;
            }

            case 0xD000: { // Opcode: DXYN, Type: Display, Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.
                byte[] display = screen.getDisplay();
                int x = V[(opcode & 0x0F00) >> 8];
                int y = V[(opcode & 0x00F0) >> 4];
                int height = (opcode & 0x000F);

                // collisionFlag
                V[0xF] = 0;

                for (int _Y = 0; _Y < height; _Y++) {
                    int line = memory[I + _Y];

                    for (int _X = 0; _X < 8; _X++) {
                        int pixel = line & (0x80 >> _X);
                        if (pixel != 0) {

                            int totalX = x + _X;
                            int totalY = y + _Y;
                            int index = totalY * 64 + totalX;

                            if (display[index] == 1) V[0xF] = 1;

                            display[index] ^= 1;
                        }
                    }
                }
                pc += 2;
                needRedraw = true;
                System.out.println("Drawing at V[" + ((opcode & 0x0F00) >> 8) + "] = "
                        + x + ", V[" + ((opcode & 0x00F0) >> 4) + "] = " + y);
                break;
            }

            case 0xE000:
                switch (opcode & 0x00FF) {

                    case 0x009E: { // Opcode: EX9E, Type: KeyOp, Skips the next instruction if the key stored in VX is pressed (usually the next instruction is a jump to skip a code block).

                        int x = (opcode & 0x0F00) >> 8;
                        if (keyboard.isPressed(V[x])) {
                            pc += 4;
                        } else {
                            pc += 2;
                        }
                        System.out.println("Key " + (int)V[x]);
                        break;
                    }

                    case 0x00A1: { // Opcode: EXA1, Type: KeyOp, Skips the next instruction if the key stored in VX is not pressed (usually the next instruction is a jump to skip a code block).

                        int x = (opcode & 0x0F00) >> 8;
                        if (!keyboard.isPressed(V[x])) {
                            pc += 4;
                        } else {
                            pc += 2;
                        }
                        System.out.println("Key " + (int)V[x]);
                        break;
                    }

                    default: {
                        System.err.println("Opcode não suportado");
                        System.exit(0);
                    }
                }
                break;

            case 0xF000: {

                switch (opcode & 0x00FF) {

                    case 0x0007: { // Opcode: FX15, Timer: MEM, Sets the delay timer to VX.
                        int x = (opcode & 0x0F00) >> 8;
                        V[x] = (char) delay_timer;
                        pc += 2;
                        System.out.println("Setting V[" + (int)V[x] + "]" + " to delay_timer");
                        //break;
                    }

                    case 0x0015: { // Opcode: FX15, Timer: MEM, Sets the delay timer to VX.
                        int x = (opcode & 0x0F00) >> 8;
                        delay_timer = V[x];
                        pc += 2;
                        System.out.println("Setting delay_timer to V[" + (int)V[x] + "]");
                        //break;
                    }

                    case 0x0029: { // Opcode: FX29, Type: MEM, Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
                        int x = (opcode & 0x0F00) >> 8;
                        int character = V[x];
                        I = (char) (0x050 + (character * 5));
                        System.out.println("Setting I to Character V[" + x + "] = " + (int)V[x] + " Offset to 0x" + Integer.toHexString(I).toUpperCase());
                        pc += 2;
                        break;
                    }

                    case 0x0033: { // Opcode: FX33, Type: BCD, Stores the binary-coded decimal representation of VX, with the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.
                        int address = (opcode & 0x0F00) >> 8;
                        int x = V[address];

                        int hundred = (x / 100);
                        int ten = ((x % 100) / 10);
                        int unit = ((x % 100) % 10);

                        memory[I] = (char) hundred;
                        memory[I + 1] = (char) ten;
                        memory[I + 2] = (char) unit;

                        System.out.println("Storing Binary-Coded Decimal V[" + address + "] = " + (int)(V[(opcode & 0x0F00) >> 8]) + " as {" + hundred + ", " + ten + ", " + unit + "}");
                        pc += 2;
                        break;
                    }

                    case 0x0065: { // Opcode: FX65, Type: MEM, Fills from V0 to VX (including VX) with values from memory, starting at address I. The offset from I is increased by 1 for each value read, but I itself is left unmodified.
                        int x = (opcode & 0x0F00) >> 8;
                        for (int i = 0; i < x ; i++) {
                            V[i] = memory[I + i];
                        }
                        System.out.println("Setting V[0] to V[" + x + "] to the values of merory[0x" + Integer.toHexString(I & 0xFFFF).toUpperCase() + "]");
                        pc += 2;
                        break;
                    }

                    default: {
                        System.err.println("Opcode não suportado");
                        System.exit(0);
                    }
                    break;
                }
                break;
            }

            default: {
                System.err.println("Opcode não suportado");
                System.exit(0);
            }
        }
    }

    public void gameLoader(String file) {
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new FileInputStream(file));

            int offset = 0;
            while(dataInputStream.available() > 0) {
                memory[0x200 + offset] = (char) (dataInputStream.readByte() & 0xFF);
                offset++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException exception) { }
            }
        }
    }

    public void fontLoader() {
        int offset = 0;
        while(offset < ChipFonts.fontset.length) {
            memory[0x50 + offset] = (char)(ChipFonts.fontset[offset] & 0xFF);
            offset++;
        }
    }

    public boolean needsRedraw() {
        return needRedraw;
    }

    public void removeDrawFlag() {
        needRedraw = false;
    }
}
