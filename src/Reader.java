import java.io.*;
import java.util.*;

public class Reader {

    static byte[][] pos = {
            {2, 1, 0, 0, 0, 0, 9, 10},
            {4, 1, 0, 0, 0, 0, 9, 12},
            {5, 1, 0, 0, 0, 0, 9, 13},
            {6, 1, 0, 0, 0, 0, 9, 14},
            {7, 1, 0, 0, 0, 0, 9, 15},
            {5, 1, 0, 0, 0, 0, 9, 13},
            {4, 1, 0, 0, 0, 0, 9, 12},
            {2, 1, 0, 0, 0, 0, 9, 10}
    };
    static boolean turnWhite = true;
    static boolean end = false;
    static Board board = new Board(pos, (byte) -1);

    public static void main(String[] args) {
        read();
    }

    static void read() {

        File dir = new File("Gam");
        for (String name : dir.list()) {
            try (FileReader reader = new FileReader("Gam/" + name)) {
                Character bracket = null;
                Deque<Character> stack = new ArrayDeque<>();
                // читаем посимвольно
                int c;
                int countBracket = 0;
                while ((c = reader.read()) != -1) {
                    char character = (char) c;
                    if (bracket != null) {
                        if (character == '[' && bracket == ']')
                            countBracket++;
                        if (character == '{' && bracket == '}')
                            countBracket++;
                        if (character == '(' && bracket == ')')
                            countBracket++;
                    }
                    if (bracket == null) {
                        if (character == '[')
                            bracket = ']';
                        if (character == '{')
                            bracket = '}';
                        if (character == '(')
                            bracket = ')';
                        if (character == '$')
                            bracket = ' ';
                    }
                    if (bracket != null && character == bracket) {
                        countBracket--;
                        if (countBracket == -1) {
                            countBracket = 0;
                            bracket = null;
                        }
                        continue;
                    }
                    if (bracket == null) {
                        if (character == '=')
                            end = true;
                        if (character != ' ' && character != '\n' && character != '\r' && character != '+' && character != '#' && character != 'x') {
                            stack.addLast(character);
                        }
                        if (character == '.')
                            stack.clear();
                        if (character == ' ' || character == '\n' || character == '\r') {
                            if (character == ' ' || stack.size() != 0)
                            refactor(stack);
                            stack.clear();
                        }
                    }
                }
            } catch (IOException ig) {
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void refactor(Deque<Character> move) throws InterruptedException {
        if (move.size() == 0) {
            System.out.println(board);
            board = new Board(pos, (byte) -1);
            turnWhite = true;
            end = false;
            return;
        }
        if (move.getFirst()=='O')
        if (turnWhite){
            if (move.size()==5){
                make((short)4020);
                turnWhite = !turnWhite;
                return;
            }
            else {
                make((short)4060);
                turnWhite = !turnWhite;
                return;
            }
        }
        else {
            if (move.size()==5){
                make((short)4727);
                turnWhite = !turnWhite;
                return;
            }
            else {
                make((short)4767);
                turnWhite = !turnWhite;
                return;
            }
        }
        Character[] Chars = move.toArray(new Character[move.size()]);
        char[] chars = new char[Chars.length];
        for (int i = 0; i < Chars.length; i++)
            chars[i] = Chars[i];
        String m =new String(chars);
        if (!end) {
            ArrayList<Short> moves = board.getMoves(turnWhite, false);
            for (short mov : moves) {
                byte[] mb = board.numToMove(mov);
                String to = String.valueOf((char)('a'+mb[2]))+String.valueOf((char)('1'+mb[3]));
                String from = String.valueOf((char)('a'+mb[0]))+String.valueOf((char)('1'+mb[1]));
                if (Objects.equals(letter(board.pos[mb[0]][mb[1]])+ from + to, m)){
                    make(mov);
                    turnWhite = !turnWhite;
                    return;
                }
            }
            for (short mov : moves) {
                byte[] mb = board.numToMove(mov);
                String to = String.valueOf((char)('a'+mb[2]))+String.valueOf((char)('1'+mb[3]));
                String from = String.valueOf((char)('a'+mb[0]))+String.valueOf((char)('1'+mb[1]));
                if (Objects.equals(letter(board.pos[mb[0]][mb[1]])+ from.substring(1,2) + to, m)){
                    make(mov);
                    turnWhite = !turnWhite;
                    return;
                }
            }
            for (short mov : moves) {
                byte[] mb = board.numToMove(mov);
                String to = String.valueOf((char)('a'+mb[2]))+String.valueOf((char)('1'+mb[3]));
                String from = String.valueOf((char)('a'+mb[0]))+String.valueOf((char)('1'+mb[1]));
                if (Objects.equals(letter(board.pos[mb[0]][mb[1]])+ from.substring(0,1) + to, m)){
                    make(mov);
                    turnWhite = !turnWhite;
                    return;
                }
            }
            for (short mov : moves) {
                byte[] mb = board.numToMove(mov);
                String to = String.valueOf((char)('a'+mb[2]))+String.valueOf((char)('1'+mb[3]));
                if (Objects.equals(letter(board.pos[mb[0]][mb[1]]) + to, m)){
                    make(mov);
                    turnWhite = !turnWhite;
                    return;
                }
            }
        }
    }

    static void make(short move) throws InterruptedException {
        String name = "";
        boolean create = true;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                name += board.pos[i][j];
        name += turnWhite;
        ArrayList<short[]> b = new ArrayList<>();
        File file = new File("Debut/"+name);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Debut/"+name))) {
                b = (ArrayList<short[]>) ois.readObject();
            } catch (Exception ex) {}
        }
        for (short[] mov : b)
            if (mov[0]==move) {
                mov[1]++;
                create = false;
                break;
            }
        if (create)
            b.add(new short[]{move,1});

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Debut/"+name))) {
            oos.writeObject(b);
        } catch (Exception ignore) {
        }

        board = Game.makeMove(new Board(board,false),move);
    }

    static String letter(byte n) {
        switch (n) {
            case 1:
                return "";
            case 2:
            case 3:
                return "R";
            case 4:
                return "N";
            case 5:
                return "B";
            case 6:
                return "Q";
            case 7:
            case 8:
                return "K";
            case 9:
                return "";
            case 10:
            case 11:
                return "R";
            case 12:
                return "N";
            case 13:
                return "B";
            case 14:
                return "Q";
            case 15:
            case 16:
                return "K";
        }
        return "";
    }

}
