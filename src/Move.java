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
                if (bitBoard.getFigure(from) == 1 && from/8==6 && i/8==7 && Game.makeLegalMove(new BitBoard(bitBoard),((((((from << 6) + i) << 4) + bitBoard.getFigure(from)) << 3) + 1))) {
                    to = i;
                    new Chose().setVisible(true);
                }
                else if (Game.makeLegalMove(new BitBoard(bitBoard),(((((from << 6) + i) << 4) + bitBoard.getFigure(from)) << 3))) {
                    move = (((((from << 6) + i) << 4) + bitBoard.getFigure(from)) << 3);
                    clear();
                    block =true;
                }
                else
                  from = i;
            }

    }

    static void clear(){
        from = null;
        to = null;
    }

    static void updatePosition(){
        String color;
            for (int i = 0; i < 64; i++) {
                if ((i/8 + i%8) % 2 == 0)
                    color = "_W";
                else
                    color = "_B";
               if (((bitBoard.WHITE_PAWN >> i) & 1) != 0)
                   buttons[i].setIcon(new ImageIcon("Icons/WP"+color+".png"));
               else if (((bitBoard.WHITE_ROOK >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/WR"+color+".png"));
               else if (((bitBoard.WHITE_BISHOP >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/WB"+color+".png"));
               else if (((bitBoard.WHITE_KNIGHT >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/WN"+color+".png"));
               else if (((bitBoard.WHITE_QUEEN >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/WQ"+color+".png"));
               else if (((bitBoard.WHITE_KING >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/WK"+color+".png"));

               else if (((bitBoard.BLACK_PAWN >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/BP"+color+".png"));
               else if (((bitBoard.BLACK_ROOK >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/BR"+color+".png"));
               else if (((bitBoard.BLACK_BISHOP >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/BB"+color+".png"));
               else if (((bitBoard.BLACK_KNIGHT >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/BN"+color+".png"));
               else if (((bitBoard.BLACK_QUEEN >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/BQ"+color+".png"));
               else if (((bitBoard.BLACK_KING >> i) & 1) != 0)
                    buttons[i].setIcon(new ImageIcon("Icons/BK"+color+".png"));
               else
                   buttons[i].setIcon(new ImageIcon("Icons/"+color+".png"));
                }
            }

    static void sounds(String sound){
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

