import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {

    static BitBoard bitBoard = new BitBoard();
    static long[][] zKeys = new long[16][64];

    public static void main(String[] args) throws InterruptedException {
        genZObristKeys();
        Mask.calculating();
       bitBoard = new BitBoard();
       Move.bitBoard = new BitBoard(bitBoard);
       new Gui().setVisible(true);
       new Editor().setVisible(true);
       Move.updatePosition();
       while (!Edit.start)
           Thread.sleep(100);
       start();
    }

    static void start() throws InterruptedException {
        while (true){
            System.out.println();
            int move;
            Move.block = false;
            while (!Move.block) {
                Thread.sleep(100);
            }
            move = Move.move;
            Move.move = null;
            bitBoard = makeMove(new BitBoard(bitBoard),move);

            if (checkEnd(false))
                break;

            long st = System.currentTimeMillis();

            int num = AI.bfs(new BitBoard(bitBoard,false),6);

            long secondTime = System.currentTimeMillis() - st;
            System.out.println("Время на ход: " + secondTime);

            bitBoard = makeMove(new BitBoard(bitBoard),num);

            if (checkEnd(true))
                break;

            System.out.println("Оценка: " + (double) AI.scoreNow/100);
        }

    }

    static boolean checkEnd(boolean white){
        Move.sounds("Sounds/whiteTurn.wav");
        Move.bitBoard = new BitBoard(bitBoard);
        Move.updatePosition();
        if (bitBoard.isCheckMateTo(false)){
            JOptionPane.showMessageDialog(null, "Мат! Вы победили!");
            return true;
        }
        if (bitBoard.isCheckMateTo(true)){
            JOptionPane.showMessageDialog(null, "Мат! Компьютер победил!");
            return true;
        }
        if (bitBoard.isPasteTo(white)){
            JOptionPane.showMessageDialog(null, "Пат! Ничья!");
            return true;
        }
        Byte numP = AI.mDraw.get(bitBoard.getKey());
        AI.mDraw.put(bitBoard.getKey(), (byte) ((numP == null) ? 1 : numP + 1));
        if (AI.isDraw(AI.mDraw)){
            JOptionPane.showMessageDialog(null, "3 кратное повторение позиции! Ничья!");
            return true;
        }
        return false;
    }

    static void genZObristKeys(){
        Random rand = new Random();
        for (int i = 0; i < 12; i++)
            for (int j = 0; j < 64; j++)
            zKeys[i][j] = rand.nextLong();
    }

    static boolean makeLegalMove(BitBoard bitBoard,int move2){
        try {
            for (int move : bitBoard.getMoves(true, null))
                if (move == move2) {
                    makeMove(bitBoard, move);
                    boolean have = bitBoard.WHITE_KING != 0;
                    ArrayList<Integer> validMove = bitBoard.getMoves(false, null);
                    makeMove(bitBoard, validMove.get(0));
                    return have && bitBoard.WHITE_KING != 0;
                }
        }
        catch (IndexOutOfBoundsException e){
            return true;
        }
        return false;
    }

    static void kill(BitBoard bitBoard, boolean white, int to){
        if (white){
            bitBoard.WHITE_PAWN &= ~(1L << to);
            bitBoard.WHITE_ROOK &= ~(1L << to);
            bitBoard.WHITE_KNIGHT &= ~(1L << to);
            bitBoard.WHITE_QUEEN &= ~(1L << to);
            bitBoard.WHITE_KING &= ~(1L << to);
            bitBoard.WHITE_BISHOP &= ~(1L << to);
        }
        else {
            bitBoard.BLACK_PAWN &= ~(1L << to);
            bitBoard.BLACK_ROOK &= ~(1L << to);
            bitBoard.BLACK_KNIGHT &= ~(1L << to);
            bitBoard.BLACK_QUEEN &= ~(1L << to);
            bitBoard.BLACK_KING &= ~(1L << to);
            bitBoard.BLACK_BISHOP &= ~(1L << to);
        }
    }

    static void update_rotated_bitboards(BitBoard bitBoard,int from, int to, Integer zeroing){
        bitBoard.ALL ^= Mask.cell_default[from];
        bitBoard.ALL_ROTATED_45_LEFT ^= Mask.cell_rotated_45_left[from];
        bitBoard.ALL_ROTATED_45_RIGHT ^= Mask.cell_rotated_45_right[from];
        bitBoard.ALL_ROTATED_90 ^= Mask.cell_rotated_90[from];
        bitBoard.ALL |= Mask.cell_default[to];
        bitBoard.ALL_ROTATED_45_LEFT |= Mask.cell_rotated_45_left[to];
        bitBoard.ALL_ROTATED_45_RIGHT |= Mask.cell_rotated_45_right[to];
        bitBoard.ALL_ROTATED_90 |= Mask.cell_rotated_90[to];
        if (zeroing!=null) {
            bitBoard.ALL &= ~(1L << Mask.cell_default[zeroing]);
            bitBoard.ALL_ROTATED_45_LEFT &= ~(1L << Mask.cell_rotated_45_left[zeroing]);
            bitBoard.ALL_ROTATED_45_RIGHT &= ~(1L << Mask.cell_rotated_45_right[zeroing]);
            bitBoard.ALL_ROTATED_90 &= ~(1L << Mask.cell_rotated_90[zeroing]);
        }
    }

    static void update_rotated_bitboards_castling(BitBoard bitBoard,int n){
        int castle[][] = {{3, 0},{3, 7},{59, 56},{59, 63}};
            bitBoard.ALL ^= Mask.cell_default[castle[n][1]] | Mask.cell_default[castle[n][0]];
            bitBoard.ALL_ROTATED_45_LEFT ^= Mask.cell_rotated_45_left[castle[n][1]]|  Mask.cell_rotated_45_left[castle[n][0]];
            bitBoard.ALL_ROTATED_45_RIGHT ^= Mask.cell_rotated_45_right[castle[n][1]] | Mask.cell_rotated_45_right[castle[n][0]];
            bitBoard.ALL_ROTATED_90 ^= Mask.cell_rotated_90[castle[n][1]] | Mask.cell_rotated_90[castle[n][0]];
    }


    static BitBoard makeMove(BitBoard bitBoard,int move){
        int promotion = move & 7;
        int figure =  15 & (move >> 3);
        int from = move>>13, to = (move>>7) & 63;
        boolean isTurnWhite = figure < 7;
        //превращение пешки
        if (promotion != 0) {
            if (isTurnWhite) {
                bitBoard.WHITE_PAWN &= ~(1L << from);
                if (to / 8 == 3)
                    bitBoard.WHITE_ROOK |= 1L << 56 + to % 8;
                if (to / 8 == 4)
                    bitBoard.WHITE_KNIGHT |= 1L << 56 + to % 8;
                if (to / 8 == 5)
                    bitBoard.WHITE_BISHOP |= 1L << 56 + to % 8;
                if (to / 8 == 6)
                    bitBoard.WHITE_QUEEN |= 1L << 56 + to % 8;
                kill(bitBoard, false, 56 + to % 8);
            } else {
                bitBoard.BLACK_PAWN &= ~(1L << from);
                if (to / 8 == 3)
                    bitBoard.BLACK_ROOK |= 1L << to % 8;
                if (to / 8 == 4)
                    bitBoard.BLACK_KNIGHT |= 1L << to % 8;
                if (to / 8 == 5)
                    bitBoard.BLACK_BISHOP |= 1L << to % 8;
                if (to / 8 == 6)
                    bitBoard.BLACK_QUEEN |= 1L << to % 8;
                kill(bitBoard, true, to % 8);
            }
            bitBoard.PASS = 0;
            update_rotated_bitboards(bitBoard,from,to,null);
            return bitBoard;
        }
        //рокировка
        if (from==3 && figure == 6 && Math.abs(from-to)==2){
            bitBoard.WHITE_KING &= ~(1L << from);
            bitBoard.WHITE_KING |= 1L << to;
            if (to==1) {
                bitBoard.WHITE_ROOK ^= 0b101;
                update_rotated_bitboards_castling(bitBoard, 0);
            }
            else {
                bitBoard.WHITE_ROOK ^= 0b10010000;
                update_rotated_bitboards_castling(bitBoard, 1);
            }
            bitBoard.PASS = 0;
            bitBoard.CASTLE &= ~(1L << 3);
            bitBoard.CASTLE &= ~(1L << 2);
            return bitBoard;
        }
        if (from==59 && figure == 12 && Math.abs(from-to)==2){
            bitBoard.BLACK_KING &= ~(1L << from);
            bitBoard.BLACK_KING |= 1L << to;
            if (to==57) {
                bitBoard.BLACK_ROOK ^= 0b101 << 56;
                update_rotated_bitboards_castling(bitBoard, 2);
            }
            else {
                bitBoard.BLACK_ROOK ^= 0b10010000 << 56;
                update_rotated_bitboards_castling(bitBoard, 3);
            }
            bitBoard.PASS = 0;
            bitBoard.CASTLE &= ~(1L << 1);
            bitBoard.CASTLE &= ~1L;
            return bitBoard;
        }

        //взятие на проходе
        if (figure == 1 && ((bitBoard.PASS >> to) & 1) != 0){
            bitBoard.WHITE_PAWN &= ~(1L << from);
            bitBoard.WHITE_PAWN |= 1L << to;
            bitBoard.BLACK_PAWN &= (~1L << to+8);
            bitBoard.PASS = 0;
            update_rotated_bitboards(bitBoard,from,to,to+8);
            return bitBoard;
        }
        if (figure == 7 && ((bitBoard.PASS >> to) & 1) != 0){
            bitBoard.BLACK_PAWN &= ~(1L << from);
            bitBoard.BLACK_PAWN |= 1L << to;
            bitBoard.WHITE_PAWN &= ~(1L << to-8);
            bitBoard.PASS = 0;
            update_rotated_bitboards(bitBoard,from,to,to-8);
            return bitBoard;
        }

        //обычный ход
        if (figure == 2){
            if (from == 0)
                bitBoard.CASTLE &= ~(1L << 3);
            else if (from == 7)
                bitBoard.CASTLE &= ~(1L << 2);
        }
        if (figure == 6){
            bitBoard.CASTLE &= ~(1L << 3);
            bitBoard.CASTLE &= ~(1L << 2);
        }

        if (figure == 8){
            if (from == 56)
                bitBoard.CASTLE &= ~(1L << 1);
            else if (from == 63)
                bitBoard.CASTLE &= ~1L;
        }
        if (figure == 12){
            bitBoard.CASTLE &= ~(1L << 1);
            bitBoard.CASTLE &= ~1L;
        }

        bitBoard.PASS = 0;

        if (Math.abs(from-to)==16 && (from/8 == 6 && ((bitBoard.BLACK_PAWN >> from) & 1) != 0 || from/8 == 1 && (((bitBoard.WHITE_PAWN >> from) & 1) != 0)))
            bitBoard.PASS |= 1L << (to - ((from/8 == 1) ? 8:-8));

        if (isTurnWhite){
            if (figure == 1){
                bitBoard.WHITE_PAWN &= ~(1L << from);
                bitBoard.WHITE_PAWN |= 1L << to;
            }
            else if (figure == 2){
                bitBoard.WHITE_ROOK &= ~(1L << from);
                bitBoard.WHITE_ROOK |= 1L << to;
            }
            else if (figure == 3){
                bitBoard.WHITE_KNIGHT &= ~(1L << from);
                bitBoard.WHITE_KNIGHT |= 1L << to;
            }
            else if (figure == 4){
                bitBoard.WHITE_BISHOP &= ~(1L << from);
                bitBoard.WHITE_BISHOP |= 1L << to;
            }
            else if (figure == 5){
                bitBoard.WHITE_QUEEN &= ~(1L << from);
                bitBoard.WHITE_QUEEN |= 1L << to;
            }
            else if (figure == 6){
                bitBoard.WHITE_KING &= ~(1L << from);
                bitBoard.WHITE_KING |= 1L << to;
            }
            kill(bitBoard,false,to);
        } else {
            if (figure == 7){
                bitBoard.BLACK_PAWN &= ~(1L << from);
                bitBoard.BLACK_PAWN |= 1L << to;
            }
            else if (figure == 8){
                bitBoard.BLACK_ROOK &= ~(1L << from);
                bitBoard.BLACK_ROOK |= 1L << to;
            }
            else if (figure == 9){
                bitBoard.BLACK_KNIGHT &= ~(1L << from);
                bitBoard.BLACK_KNIGHT |= 1L << to;
            }
            else if (figure == 10){
                bitBoard.BLACK_BISHOP &= ~(1L << from);
                bitBoard.BLACK_BISHOP |= 1L << to;
            }
            else if (figure == 11){
                bitBoard.BLACK_QUEEN &= ~(1L << from);
                bitBoard.BLACK_QUEEN |= 1L << to;
            }
            else if (figure == 12){
                bitBoard.BLACK_KING &= ~(1L << from);
                bitBoard.BLACK_KING |= 1L << to;
            }
            kill(bitBoard,true,to);
        }
        update_rotated_bitboards(bitBoard,from,to,null);
        return bitBoard;
    }
}