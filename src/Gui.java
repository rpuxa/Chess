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
        setLocation(centerX - width / 2-100, centerY - height / 2-100);

        GridLayout layout = new GridLayout(8,8);
        setLayout(layout);

        for (int i = 63; i >= 0; i--) {
                final int i1 = i;
                Move.buttons[i] = new JButton();
                    Move.buttons[i].setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final int i2 = i1;
                                Move.change(i2);
                        }
                    });
                add(Move.buttons[i]);
            }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Move.updatePosition();
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
        setSize(640, 640+2*80);

        final int centerX = screenSize.width / 2, centerY = screenSize.height / 2;
        setLocation(centerX - width / 2-100, centerY - height / 2-100);

        GridLayout layout = new GridLayout(10,8);
        setLayout(layout);

        for (int i = 63; i >= 0; i--) {
                final int i1 = i;
                Edit.buttons[i] = new JButton();
                    Edit.buttons[i].setAction(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ImageIcon icon;
                            final int i2 = i1;
                            if ((i2/8 + i2%8) % 2 == 1)
                                icon = new ImageIcon("Icons/"+chose+"B.png");
                            else
                                icon = new ImageIcon("Icons/"+chose+"W.png");
                                Edit.buttons[i2].setIcon(icon);
                        }
                    });
                add(Edit.buttons[i]);
                if ((i/8 + i%8) % 2 == 1){
                    ImageIcon icon = new ImageIcon("Icons/_B.png");
                    Edit.buttons[i].setIcon(icon);
                }
                else {
                    ImageIcon icon = new ImageIcon("Icons/_W.png");
                    Edit.buttons[i].setIcon(icon);
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
                for (int i = 0; i < 64; i++) {
                    ImageIcon icon;
                        if ((i/8 + i%8) % 2 == 1)
                            icon = new ImageIcon("Icons/_B.png");
                        else
                            icon = new ImageIcon("Icons/_W.png");
                        Edit.buttons[i].setIcon(icon);
                    }
            }
        });
        clear.setText("Clear");
        add(clear);

        JButton accept = new JButton("Accept");
        accept.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BitBoard bitBoard = new BitBoard(true);
                for (int i = 0; i < 64; i++) {
                        Icon icon = Edit.buttons[i].getIcon();
                        String name = icon.toString();
                        if (!Objects.equals(name, "Icons/_W.png") && !Objects.equals(name, "Icons/_B.png")) {

                            if (name.toCharArray()[6] == 'W') {
                                if (name.toCharArray()[7] == 'P')
                                    bitBoard.WHITE_PAWN |= 1L << i;
                                if (name.toCharArray()[7] == 'R')
                                    bitBoard.WHITE_ROOK |= 1L << i;
                                if (name.toCharArray()[7] == 'N')
                                    bitBoard.WHITE_KNIGHT |= 1L << i;
                                if (name.toCharArray()[7] == 'B')
                                    bitBoard.WHITE_BISHOP |= 1L << i;
                                if (name.toCharArray()[7] == 'Q')
                                    bitBoard.WHITE_QUEEN |= 1L << i;
                                if (name.toCharArray()[7] == 'K')
                                    bitBoard.WHITE_KING |= 1L << i;
                            }
                            else {
                                if (name.toCharArray()[7] == 'P')
                                    bitBoard.BLACK_PAWN |= 1L << i;
                                if (name.toCharArray()[7] == 'R')
                                    bitBoard.BLACK_ROOK |= 1L << i;
                                if (name.toCharArray()[7] == 'N')
                                    bitBoard.BLACK_KNIGHT |= 1L << i;
                                if (name.toCharArray()[7] == 'B')
                                    bitBoard.BLACK_BISHOP |= 1L << i;
                                if (name.toCharArray()[7] == 'Q')
                                    bitBoard.BLACK_QUEEN |= 1L << i;
                                if (name.toCharArray()[7] == 'K')
                                    bitBoard.BLACK_KING |= 1L << i;
                            }
                        }

                    }

                Game.bitBoard = new BitBoard(bitBoard);
                Move.bitBoard = new BitBoard(bitBoard);
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
                Move.move = ((((((Move.from << 6) + Move.to) << 4) + Move.bitBoard.getFigure(Move.from)) << 3) + 1);
                Move.block = true;
                Move.clear();
                setVisible(false);
            }
        });
        rook.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.move = ((((((Move.from << 6) + Move.to) << 4) + Move.bitBoard.getFigure(Move.from)) << 3) + 2);
                Move.block = true;
                Move.clear();
                setVisible(false);
            }
        });
        knight.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.move = ((((((Move.from << 6) + Move.to) << 4) + Move.bitBoard.getFigure(Move.from)) << 3) + 3);
                Move.block = true;
                Move.clear();
                setVisible(false);
            }
        });
        bishop.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.move = ((((((Move.from << 6) + Move.to) << 4) + Move.bitBoard.getFigure(Move.from)) << 3) + 4);
                Move.block = true;
                Move.clear();
                setVisible(false);
            }
        });

        queen.setIcon(new ImageIcon("Icons/WQ_B.png"));
        knight.setIcon(new ImageIcon("Icons/WN_B.png"));
        bishop.setIcon(new ImageIcon("Icons/WB_B.png"));
        rook.setIcon(new ImageIcon("Icons/WR_B.png"));

        add(queen);
        add(rook);
        add(knight);
        add(bishop);
    }

    public static void main(String[] args) {
    }
}

class Edit{
    static boolean start = false;
    static JButton[] buttons = new JButton[64];
}