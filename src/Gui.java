import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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
            }
        });
        rook.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(3,9);
            }
        });
        knight.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(4,9);
            }
        });
        bishop.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Move.change(5,9);
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