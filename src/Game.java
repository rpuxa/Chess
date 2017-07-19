import javax.swing.*;
import java.util.ArrayList;
import static Util.BitUtils.*;

public class Game {

    static BitBoard bitBoard = BitBoard.make_bitboard_start();

    public static void main(String[] args) throws InterruptedException {
        BitBoard.genZObristKeys();
        Mask.calculating();
        Eval.calculate();
       bitBoard = BitBoard.make_bitboard_start();
       Move.bitBoard = BitBoard.make_bitboard_from(bitBoard);
       new Gui().setVisible(true);
       new Editor().setVisible(true);
       Move.updatePosition();
       while (!Edit.start)
           Thread.sleep(100);
       start();
    }

    private static void start() throws InterruptedException {
        while (true){
            System.out.println();
            int move;
            Move.block = false;
            while (!Move.block) {
                Thread.sleep(100);
            }
            move = Move.move;
            Move.move = null;
            bitBoard = makeMove(BitBoard.make_bitboard_from(bitBoard),move);

            if (checkEnd(false))
                break;

            long st = System.currentTimeMillis();

            int num = AI.bfs(BitBoard.make_bitboard_from(bitBoard),6);

            long secondTime = System.currentTimeMillis() - st;
            System.out.println("Время на ход: " + secondTime);

            bitBoard = makeMove(BitBoard.make_bitboard_from(bitBoard),num);

            if (checkEnd(true))
                break;

            System.out.println("Оценка: " + (double) AI.scoreNow/100);
        }

    }

    private static boolean checkEnd(boolean white){
        Move.sounds("Sounds/whiteTurn.wav");
        Move.bitBoard = BitBoard.make_bitboard_from(bitBoard);
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

    static boolean makeLegalMove(BitBoard bitBoard,int move2){
        try {
            for (int move : bitBoard.getMoves(true))
                if (move == move2) {
                    makeMove(bitBoard, move);
                    boolean have = bitBoard.white[king] != 0;
                    ArrayList<Integer> validMove = bitBoard.getMoves(false);
                    makeMove(bitBoard, validMove.get(0));
                    return have && bitBoard.white[king] != 0;
                }
        }
        catch (IndexOutOfBoundsException e){
            return true;
        }
        return false;
    }

    private static void kill(BitBoard bitBoard, boolean white, int to){
        if (white)
            for (int i = 0; i < 6; i++)
                zeroBit(bitBoard.white[i],to);
        else
            for (int i = 0; i < 6; i++)
                zeroBit(bitBoard.black[i],to);
    }

    private static void update_rotated_bitboards(BitBoard bitBoard, int from, int to, Integer zeroing){
        bitBoard.all[0] ^= Mask.cell_default[from];
        bitBoard.all[1] ^= Mask.cell_rotated_45_left[from];
        bitBoard.all[2] ^= Mask.cell_rotated_45_right[from];
        bitBoard.all[3] ^= Mask.cell_rotated_90[from];
        bitBoard.all[0] |= Mask.cell_default[to];
        bitBoard.all[1] |= Mask.cell_rotated_45_left[to];
        bitBoard.all[2] |= Mask.cell_rotated_45_right[to];
        bitBoard.all[3] |= Mask.cell_rotated_90[to];
        if (zeroing!=null) {
            bitBoard.all[0] &= ~Mask.cell_default[zeroing];
            bitBoard.all[1] &= ~Mask.cell_rotated_45_left[zeroing];
            bitBoard.all[2] &= ~Mask.cell_rotated_45_right[zeroing];
            bitBoard.all[3] &= ~Mask.cell_rotated_90[zeroing];
        }
    }

    private static int castle[][] = {{3, 0},{3, 7},{59, 56},{59, 63}};

    private static void update_rotated_bitboards_castling(BitBoard bitBoard, int n){
            bitBoard.all[0] ^= Mask.cell_default[castle[n][1]] | Mask.cell_default[castle[n][0]];
            bitBoard.all[1] ^= Mask.cell_rotated_45_left[castle[n][1]]|  Mask.cell_rotated_45_left[castle[n][0]];
            bitBoard.all[2] ^= Mask.cell_rotated_45_right[castle[n][1]] | Mask.cell_rotated_45_right[castle[n][0]];
            bitBoard.all[3] ^= Mask.cell_rotated_90[castle[n][1]] | Mask.cell_rotated_90[castle[n][0]];
    }




    static BitBoard makeMove(BitBoard bitBoard,int move){
        int promotion = move & 7;
        int figure =  15 & (move >> 3);
        int from = move>>13, to = (move>>7) & 63;
        boolean isTurnWhite = figure < 7;
        bitBoard.lastMove = (byte)to;
        //превращение пешки
        if (promotion != 0) {
            if (isTurnWhite) {
                bitBoard.white[pawn] = zeroBit(bitBoard.white[pawn],from);
                if (promotion == 1)
                    bitBoard.white[queen] = setBit(bitBoard.white[queen],56 + to % 8);
                else if (promotion == 2)
                    bitBoard.white[rook] = setBit(bitBoard.white[rook],56 + to % 8);
                else if (promotion == 3)
                    bitBoard.white[knight] = setBit(bitBoard.white[knight],56 + to % 8);
                else if (promotion == 4)
                    bitBoard.white[bishop] = setBit(bitBoard.white[bishop],56 + to % 8);
                kill(bitBoard, false, 56 + to % 8);
            } else {
                bitBoard.black[pawn] = zeroBit(bitBoard.black[pawn],from);
                if (promotion == 1)
                    bitBoard.black[queen] = setBit(bitBoard.black[queen],to % 8);
                else if (promotion == 2)
                    bitBoard.black[rook] = setBit(bitBoard.black[rook],to % 8);
                else if (promotion == 3)
                    bitBoard.black[knight] = setBit(bitBoard.black[knight],to % 8);
                else if (promotion == 4)
                    bitBoard.black[bishop] = setBit(bitBoard.black[bishop],to % 8);
                kill(bitBoard, true, to % 8);
            }
            bitBoard.pass = 0;
            update_rotated_bitboards(bitBoard,from,to,null);
            return bitBoard;
        }
        //рокировка
        if (from==3 && figure == white_king && Math.abs(from-to)==2){
            bitBoard.white[king] = zeroBit(bitBoard.white[king],from);
            bitBoard.white[king] = setBit(bitBoard.white[king],to);
            if (to==1) {
                bitBoard.white[rook] ^= 0b101;
                update_rotated_bitboards_castling(bitBoard, 0);
            }
            else {
                bitBoard.white[rook] ^= 0b10010000;
                update_rotated_bitboards_castling(bitBoard, 1);
            }
            bitBoard.pass = 0;
            bitBoard.castle = zeroBit(bitBoard.castle,3);
            bitBoard.castle = zeroBit(bitBoard.castle,2);
            return bitBoard;
        }
        if (from==59 && figure == black_king && Math.abs(from-to)==2){
            bitBoard.black[king] = zeroBit(bitBoard.black[king],from);
            bitBoard.black[king] = setBit(bitBoard.black[king],to);
            if (to==57) {
                bitBoard.black[rook] ^= 0b101L << 56;
                update_rotated_bitboards_castling(bitBoard, 2);
            }
            else {
                bitBoard.black[rook] ^= 0b10010000L << 56;
                update_rotated_bitboards_castling(bitBoard, 3);
            }
            bitBoard.pass = 0;
            bitBoard.castle = zeroBit(bitBoard.castle,1);
            bitBoard.castle = zeroBit(bitBoard.castle,0);
            return bitBoard;
        }

        //взятие на проходе
        if (figure == white_pawn && ((bitBoard.pass >> to) & 1) != 0){
            bitBoard.white[pawn] = zeroBit(bitBoard.white[pawn],from);
            bitBoard.white[pawn] = setBit(bitBoard.white[pawn],to);
            bitBoard.black[pawn] = zeroBit(bitBoard.black[pawn],to + 8);
            bitBoard.pass = 0;
            update_rotated_bitboards(bitBoard,from,to,to+8);
            return bitBoard;
        }
        if (figure == black_pawn && ((bitBoard.pass >> to) & 1) != 0){
            bitBoard.black[pawn] = zeroBit(bitBoard.black[pawn],from);
            bitBoard.black[pawn] = setBit(bitBoard.black[pawn],to);
            bitBoard.white[pawn] = zeroBit(bitBoard.white[pawn],to - 8);
            bitBoard.pass = 0;
            update_rotated_bitboards(bitBoard,from,to,to-8);
            return bitBoard;
        }

        //обычный ход
        if (figure == white_rook && from == 0 || figure == white_king)
            bitBoard.castle = zeroBit(bitBoard.castle,3);
        if (figure == white_rook && from == 7 || figure == white_king)
            bitBoard.castle = zeroBit(bitBoard.castle,2);

        if (figure == black_rook && from == 56 || figure == black_king)
            bitBoard.castle = zeroBit(bitBoard.castle,1);
        if (figure == black_rook && from == 63 || figure == black_king)
            bitBoard.castle = zeroBit(bitBoard.castle,0);

        bitBoard.pass = 0;

        if (Math.abs(from-to)==16 && (from/8 == 6 && getBit(bitBoard.black[pawn],from) || from/8 == 1 && getBit(bitBoard.white[pawn],from)))
            bitBoard.pass = setBit(bitBoard.pass,to - ((from/8 == 1) ? 8:-8));

        if (isTurnWhite){
            bitBoard.white[figure-1] = zeroBit(bitBoard.white[figure-1],from);
            bitBoard.white[figure-1] = setBit(bitBoard.white[figure-1],to);
            kill(bitBoard,false,to);
        } else {
            bitBoard.black[figure-7] = zeroBit(bitBoard.black[figure-7],from);
            bitBoard.black[figure-7] = setBit(bitBoard.black[figure-7],to);
            kill(bitBoard,true,to);
        }
        update_rotated_bitboards(bitBoard,from,to,null);
        return bitBoard;
    }
}