import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

class Move {
    static int[] from;
    static Integer[] to;
    static Board board;
    static JButton[][] buttons = new JButton[8][8];
    static boolean block = false;

    static void change(int y, int x) {
        if (!block)
            if (x==9){
            to[1] = y;
            block = true;
            }
            else if (from == null)
                from = new int[]{x,y};
            else {
                if (board.pos[from[0]][from[1]]==1 && from[1]==6 && Math.abs(from[0]-x)<=1 && Game.makeLegalMove(new Board(board),from[0],from[1],x,6)) {
                    new Chose().setVisible(true);
                    to = new Integer[]{x,null};
                }
                else if (Game.makeLegalMove(new Board(board),from[0],from[1],x,y)) {
                    to = new Integer[]{x,y};
                    block =true;
                }
                else
                    from = new int[]{x,y};
            }

    }
    static void updatePosition(){
        String color;
        for (int j = 7; j >= 0; j--)
            for (int i = 0; i < 8; ++i) {
                if ((i + j) % 2 == 1)
                    color = "_W";
                else
                    color = "_B";
                switch (board.pos[j][i]){
                    case 0:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/"+color+".png"));
                        break;
                    }
                    case 1:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/WP"+color+".png"));
                        break;
                    }
                    case 2:
                    case 3:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/WR"+color+".png"));
                        break;
                    }
                    case 4:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/WN"+color+".png"));
                        break;
                    }
                    case 5:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/WB"+color+".png"));
                        break;
                    }
                    case 6:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/WQ"+color+".png"));
                        break;
                    }
                    case 7:
                    case 8:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/WK"+color+".png"));
                        break;
                    }
                    case 9:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/BP"+color+".png"));
                        break;
                    }
                    case 10:
                    case 11:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/BR"+color+".png"));
                        break;
                    }
                    case 12:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/BN"+color+".png"));
                        break;
                    }
                    case 13:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/BB"+color+".png"));
                        break;
                    }
                    case 14:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/BQ"+color+".png"));
                        break;
                    }
                    case 15:
                    case 16:{
                        buttons[j][i].setIcon(new ImageIcon("Icons/BK"+color+".png"));
                        break;
                    }
                }
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

