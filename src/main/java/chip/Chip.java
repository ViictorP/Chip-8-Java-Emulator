package chip;

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
    }

    public void run() {
        //fetch Opcode
        char opcode = (char)((memory[pc] << 8) | memory[pc + 1]);
        System.out.print(Integer.toBinaryString(opcode) + ": ");

        //decode Opcode

        switch (opcode & 0xF000) {

            case 0x8000:    // contém mais informação nos 4 ultimos bits ou nibble

                switch (opcode & 0x000F) {

                    case 0x0000: // 8XY0 Sets VX to the value of VY.

                        default:
                            System.err.println("OPCODE Não suportado");
                            System.exit(0);
                        break;
                }

                break;

            default:
                System.err.println("OPCODE Não suportado");
                System.exit(0);
                break;
        }

    }
}
