package chip;

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

    // byte[] keys resenta os teclas do teclado.
    private byte[] keys;

    // Representa os pixels da dela monocromatica.
    private byte[] display;

    private boolean needRedraw;

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

        keys = new byte[16];

        display = new byte[64 * 32];

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

                    case 0x0000:
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

            case 0xD000: { // Opcode: DXYN, Type: Display, Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.

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

            case 0xF000: {

                switch (opcode & 0x00FF) {

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

    public byte[] getDisplay() {
        return display;
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
