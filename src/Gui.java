import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Objects;

public class Gui extends JFrame {

    @Override
    protected void frameInit() {
        super.frameInit();

        final Dimension screenSize = getToolkit().getScreenSize();

        setResizable(false);

        final int width = screenSize.width / 3, height = screenSize.height / 3;
        setSize(640, 640);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - width / 2, centerY - height / 2);

        GridLayout layout = new GridLayout(8,8);
        setLayout(layout);

        for (int i = 7; i >= 0; i--)
            for (int j = 0; j < 8; ++j) {
                final int i1 = i,j2 =j;
                Move.buttons[j][i] = new JButton();
                    Move.buttons[j][i].setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final int x = i1,y = j2;
                                Move.change(x,y);
                        }
                    });
                add(Move.buttons[j][i]);
            }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Move.updatePosition();
    }

    public static void main(String[] args) {
    }
}

class Editor extends JFrame {

    String chose = "_";

    @Override
    protected void frameInit() {
        super.frameInit();

        final Dimension screenSize = getToolkit().getScreenSize();

        setResizable(false);

        final int width = screenSize.width / 3, height = screenSize.height / 3;
        setSize(width, width+80);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - width / 2, centerY - height / 2);

        GridLayout layout = new GridLayout(10,8);
        setLayout(layout);

        for (int i = 7; i >= 0; i--)
            for (int j = 0; j < 8; ++j) {
                final int i1 = i,j2 =j;
                Edit.buttons[i][j] = new JButton();
                    Edit.buttons[i][j].setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ImageIcon icon;
                            final int x = i1,y = j2;
                            if ((x+y) % 2 == 0)
                                icon = new ImageIcon("Icons/"+chose+"B.png");
                            else
                                icon = new ImageIcon("Icons/"+chose+"W.png");
                                Edit.buttons[x][y].setIcon(icon);
                        }
                    });
                add(Edit.buttons[i][j]);
                if ((i+j) % 2 == 0){
                    ImageIcon icon = new ImageIcon("Icons/_B.png");
                    Edit.buttons[i][j].setIcon(icon);
                }
                else {
                    ImageIcon icon = new ImageIcon("Icons/_W.png");
                    Edit.buttons[i][j].setIcon(icon);
                }
            }

        String[] editorButtons = {"WK_","WQ_","WB_","WN_","WR_","WP_"};

        for (int i = 0; i < 6; i++) {
            final int index = i;
            JButton button = new JButton();
            button.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chose = editorButtons[index];
                }
            });
            ImageIcon icon = new ImageIcon("Icons/"+editorButtons[index]+"B.png");
            button.setIcon(icon);
            add(button);
        }

        JButton clear = new JButton("Clear");
        clear.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++) {
                    ImageIcon icon;
                        if ((i + j) % 2 == 0)
                            icon = new ImageIcon("Icons/_B.png");
                        else
                            icon = new ImageIcon("Icons/_W.png");
                        Edit.buttons[i][j].setIcon(icon);
                    }
            }
        });
        clear.setText("Clear");
        add(clear);

        JButton accept = new JButton("Accept");
        accept.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                byte[][] pos = new byte[8][8];
                for (int i = 0; i < 8; i++)
                    for (int j = 7; j >= 0; j--) {
                        Icon icon = Edit.buttons[i][j].getIcon();
                        String name = icon.toString();
                        if (!Objects.equals(name, "Icons/_W.png") && !Objects.equals(name, "Icons/_B.png")) {
                            byte black = 0;
                            if (name.toCharArray()[6] == 'B')
                                black = 8;
                            if (name.toCharArray()[7] == 'P')
                                pos[j][i] = (byte) (1 + black);
                            if (name.toCharArray()[7] == 'R')
                                pos[j][i] = (byte) (2 + black);
                            if (name.toCharArray()[7] == 'N')
                                pos[j][i] = (byte) (4 + black);
                            if (name.toCharArray()[7] == 'B')
                                pos[j][i] = (byte) (5 + black);
                            if (name.toCharArray()[7] == 'Q')
                                pos[j][i] = (byte) (6 + black);
                            if (name.toCharArray()[7] == 'K')
                                pos[j][i] = (byte) (7 + black);
                        }
                    }
                Board board = new Board(pos,(byte)-1);
                Game.board = new Board(board,false);
                Move.board = new Board(board,false);
                Move.updatePosition();
            }
        });
        accept.setText("Accept");
        add(accept);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Edit.start = true;
            }
        });

        final String[] editorButtons2 = new String[]{"BK_","BQ_","BB_","BN_","BR_","BP_","_"};

        for (int i = 0; i < 7; i++) {
            final int index = i;
            JButton button = new JButton();
            button.setAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    chose = editorButtons2[index];
                }
            });
            ImageIcon icon = new ImageIcon("Icons/"+editorButtons2[index]+"W.png");
            button.setIcon(icon);
            add(button);
        }

    }

}



class Chose extends JFrame {

    @Override
    protected void frameInit() {
        super.frameInit();

        final Dimension screenSize = getToolkit().getScreenSize();

        setResizable(false);

        final int width = 320, height = 100;
        setSize(width, height);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - width / 2, centerY - height / 2);

        GridLayout layout = new GridLayout(1, 4);
        setLayout(layout);

        JButton queen = new JButton();
        JButton rook = new JButton();
        JButton knight = new JButton();
        JButton bishop = new JButton();

        queen.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(6,9);
                setVisible(false);
            }
        });
        rook.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(3,9);
                setVisible(false);
            }
        });
        knight.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(4,9);
                setVisible(false);
            }
        });
        bishop.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(5,9);
                setVisible(false);
            }
        });

        queen.setIcon(new ImageIcon("Icons/WQ_B.png"));
        knight.setIcon(new ImageIcon("Icons/WN_B.png"));
        bishop.setIcon(new ImageIcon("Icons/WB_B.png"));
        rook.setIcon(new ImageIcon("Icons/WR_B.png"));

        add(queen);
        add(knight);
        add(rook);
        add(bishop);
    }

    public static void main(String[] args) {
    }
}

class Edit{
    static boolean start = false;
    static JButton[][] buttons = new JButton[8][8];
}