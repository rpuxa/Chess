import Util.BitUtils;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

class Move {
    static Integer from;
    static Integer to;
    static Integer move;
    static BitBoard bitBoard;
    static JButton[] buttons = new JButton[64];
    static boolean block = false;

    static void change(int i) {
        if (!block)
            if (from == null)
                from = i;
            else {
                if (bitBoard.getFigure(from) == 1 && from / 8 == 6 && i / 8 == 7 && Game.makeLegalMove(BitBoard.make_bitboard_from(bitBoard), move_init(from, i, bitBoard.getFigure(from), 1))) {
                    to = i;
                    new Chose().setVisible(true);
                } else if (Game.makeLegalMove(BitBoard.make_bitboard_from(bitBoard), move_init(from, i, bitBoard.getFigure(from), 0))) {
                    move = move_init(from, i, bitBoard.getFigure(from), 0);
                    clear();
                    block = true;
                } else
                    from = i;
            }

    }

    static void clear() {
        from = null;
        to = null;
    }

    private static int move_init(int from, int to, int figure, int promotion) {
        return (((((from << 6) + to) << 4) + figure) << 3) + promotion;
    }

    static void updatePosition() {
        String color;
        for (int i = 0; i < 64; i++) {
            if ((i / 8 + i % 8) % 2 == 0)
                color = "_W";
            else
                color = "_B";
            String[] symbols = {"P", "R", "N", "B", "Q", "K"};
            for (int j = 0; j < 6; j++) {
                if (BitUtils.getBit(bitBoard.white[j], i)) {
                    buttons[i].setIcon(new ImageIcon("Icons/W" + symbols[j] + color + ".png"));
                    break;
                } else if (BitUtils.getBit(bitBoard.black[j], i)) {
                    buttons[i].setIcon(new ImageIcon("Icons/B" + symbols[j] + color + ".png"));
                    break;
                }
                buttons[i].setIcon(new ImageIcon("Icons/" + color + ".png"));
            }
        }
    }

    static void sounds(String sound) {
        try {
            File soundFile = new File(sound);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setFramePosition(0);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
            exc.printStackTrace();
        }
    }
}

