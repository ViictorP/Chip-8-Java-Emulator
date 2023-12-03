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
    private byte[][] display;

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

        display = new byte[64][32];

        needRedraw = false;
    }

    public void run() {
        //fetch Opcode
        char opcode = (char)((memory[pc] << 8) | memory[pc + 1]);
        System.out.print(Integer.toHexString(opcode) + ": ");

        //decode Opcode
        switch (opcode & 0xF000) {

            case 0x1000:
                break;

            case 0x2000: // Opcode: 2NNN, Type: Call, Calls subroutine at NNN.
                char address = (char)(opcode & 0x0FFF);
                stack[stackPointer] = pc;
                stackPointer++;
                pc = address;
                break;

            case 0x3000:
                break;

            case 0x6000:
                int x = (opcode & 0x0F00);
                V[x] = (char)(opcode & 0x00FF);
                pc += 2;
                break;


            case 0x7000:
                break;

            case 0x8000:

                switch (opcode & 0x000F) {

                    case 0x0000:
                        break;

                    default:
                        System.err.println("Opcode não suportado");
                        System.exit(0);
                        break;
                }

                break;

            default:
                System.err.println("Opcode não suportado");
                System.exit(0);
                break;
        }

    }

    public byte[][] getDisplay() {
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

    public boolean needsRedraw() {
        return needRedraw;
    }

    public void removeDrawFlag() {
        needRedraw = false;
    }
}
