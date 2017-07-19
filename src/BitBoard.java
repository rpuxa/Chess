import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static Util.BitUtils.*;

class BitBoard implements Constants{
    long[] white;
    long[] black;
    long pass;
    byte castle;
    long[] all;
    byte lastMove;


    BitBoard(long[] white,long[] black, long pass, byte castle, long[] all, byte lastMove){
        this.white = white;
        this.black = black;
        this.pass= pass;
        this.castle = castle;
        this.all = all;
        this.lastMove = lastMove;
    }

    private static long[][] zKeys = new long[12][64];

    static void genZObristKeys(){
        Random rand = new Random();
        for (int i = 0; i < 12; i++)
            for (int j = 0; j < 64; j++)
                zKeys[i][j] = rand.nextLong();
    }

    static BitBoard make_bitboard_empty(){
        return new BitBoard(new long[6],new long[6],0,(byte)0b1111,new long[4],(byte)0);
    }

    private static final long[] WHITE_BASE = {0b1111111100000000,0b10000001,0b1000010,0b100100,0b10000,0b1000};
    private static final long[] BLACK_BASE = {0b1111111100000000L << 40,0b10000001L << 56,0b1000010L << 56,0b100100L << 56,0b10000L << 56,0b1000L << 56};

    static BitBoard make_bitboard_start(){
        long[] all = all_calculate(WHITE_BASE,BLACK_BASE);
        return new BitBoard(WHITE_BASE,BLACK_BASE,0,(byte)0b1111,all,(byte)0);
    }

    static BitBoard make_bitboard_from(BitBoard bitBoard){
        return new BitBoard(bitBoard.white.clone(),bitBoard.black.clone(),bitBoard.pass,bitBoard.castle,bitBoard.all.clone(),bitBoard.lastMove);
    }

    static BitBoard make_bitboard_nullMove_from(BitBoard bitBoard){
        return new BitBoard(bitBoard.white.clone(),bitBoard.black.clone(),0,bitBoard.castle,bitBoard.all.clone(),bitBoard.lastMove);
    }

    private static long[] all_calculate(long[] white, long[] black){
        long all = 0;
        long all_rotated_90 = 0;
        long all_rotated_45_left = 0;
        long all_rotated_45_right = 0;

        for (int i = 0; i < 6; i++)
            all |= white[i] | black[i];

        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (getBit(all,8*y+x))
                    all_rotated_90 = setBit(all_rotated_90,8 * (7 - x) + y);

        int n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++){
                if (getBit(all,j+8*m))
                    all_rotated_45_left = setBit(all_rotated_45_left,n);
                m++;
                n++;
            }
        }
        for (int i = 8; i < 16; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++){
                if (j+8*m>-1 && j+8*m<64 && ((j+8*m)>>3)>((j+8*m)&7)) {
                    if (getBit(all,j+8*m))
                        all_rotated_45_left = setBit(all_rotated_45_left,n);
                    n++;
                }
                m++;
            }
        }


        n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 8*i;
            for (int j = 0; j <= i; j++){
                if (getBit(all,j+m))
                    all_rotated_45_right = setBit(all_rotated_45_right,n);
                m-=8;
                n++;
            }
        }
        label: for (int i = 8; i < 16; i++) {
            int m = 8*i;
            for (int j = 0; j <= i; j++){
                if (j+m>-1 && j+m<64 && ((j+m)>>3)+((j+m)&7)>7) {
                    if (getBit(all,j+m))
                        all_rotated_45_right = setBit(all_rotated_45_right,n);
                    n++;
                    if (j+m == 63)
                        break label;
                }
                m-=8;
            }
        }
        return new long[]{all,all_rotated_90,all_rotated_45_left,all_rotated_45_right};
    }

    long getKey() {
        long hash = 0L;
            for (int i = 0; i < 64; i++){
                for (int j = 0; j < 6; j++)
                    if (getBit(white[j],i))
                        hash ^= zKeys[j][i];
                for (int j = 6; j < 12; j++)
                    if (getBit(black[j-6],i))
                        hash ^= zKeys[j][i];
            }
            return hash;
    }

    private void add(ArrayList<Integer> moves, int pos, long mask, int figureMove) {
        add(moves,pos,mask,figureMove,false);
    }

    private static final short figures[] = {1,2,3,4};

    private void add(ArrayList<Integer> moves, int pos, long mask, int figureMove, boolean promotion) {
        if (mask != 0)
            for (int i = 0; i < 64; i++)
                if (((mask >> i) & 1) != 0)
                    if (!promotion) {
                        moves.add(((((pos << 6) + i) << 4) + figureMove) << 3);
                    } else {
                        for (short figure :figures)
                            moves.add((((((pos << 6) + i) << 4) + figureMove) << 3) + figure);
                    }
    }

    ArrayList<Integer> getMoves(boolean isTurnWhite){
       return getMoves(isTurnWhite,false);
    }

    ArrayList<Integer> getMoves(boolean isTurnWhite, boolean capture) {
        ArrayList<Integer> moves = new ArrayList<>();
        int color = 1 + ((isTurnWhite) ? 0 : 6);
        int pawn_block = 8 * ((isTurnWhite) ? 1 : -1);
        long[] ourFigures = (isTurnWhite) ? white:black;
        long[] enemyFigures = (!isTurnWhite) ? white:black;
        long ourFiguresMask = ourFigures[pawn] | ourFigures[rook] | ourFigures[knight] | ourFigures[bishop] | ourFigures[queen] | ourFigures[king];
        long enemyFiguresMask = enemyFigures[pawn] | enemyFigures[rook] | enemyFigures[knight] | enemyFigures[bishop] | enemyFigures[queen] | enemyFigures[king];
        long captureMask = (capture) ? enemyFiguresMask : -1;
        for (int i = 0; i < 64; i++) {
            boolean isQueen = getBit(ourFigures[queen],i);
            if (getBit(ourFiguresMask,i)) {
                if (getBit(ourFigures[pawn],i)) {
                    boolean promotion = (i >>3) == ((isTurnWhite) ? 6 : 1);
                    if (!getBit(all[0],i + pawn_block)) {
                        add(moves, i, ((isTurnWhite) ? Mask.white_pawn_move[i] : Mask.black_pawn_move[i]) & ~all[0] & captureMask,pawn+color, promotion);
                    }
                    add(moves, i, ((isTurnWhite) ? Mask.white_pawn_attack[i] : Mask.black_pawn_attack[i]) & (enemyFiguresMask | pass),pawn+color, promotion);
                } else if (getBit(ourFigures[knight],i))
                    add(moves, i, Mask.knight[i] & ~ourFiguresMask & captureMask,knight + color);
                else if (getBit(ourFigures[king],i)) {
                    add(moves, i, Mask.king[i] & ~ourFiguresMask & captureMask,king + color);
                    if (isTurnWhite) {
                        if (i==E1)
                        if ((all[0] & Mask.castle_white[0]) == 0 && getBit(white[rook],H1) && getBit(castle,3) && canCastle(0))
                            moves.add(white_short_castle_move);
                        else if ((all[0] & Mask.castle_white[1]) == 0 && getBit(white[rook],A1) && getBit(castle,2) && canCastle(1))
                            moves.add(white_long_castle_move);
                    } else {
                        if (i==E8)
                            if ((all[0] & Mask.castle_black[0]) == 0 && getBit(white[rook],H8) && getBit(castle,1) && canCastle(2))
                            moves.add(black_short_castle_move);
                        else if ((all[0] & Mask.castle_black[1]) == 0 && getBit(white[rook],A8) && getBit(castle,0) && canCastle(3))
                            moves.add(black_long_castle_move);
                    }
                } else if (getBit(ourFigures[rook],i) || isQueen) {
                    int figure = ((isQueen) ? queen : rook) + color;
                    int nd = i >>3;
                    long mask = (all[0] >> 8 * nd) & 255;
                    add(moves, i, Mask.rook_G[i][(int) mask] & ~ourFiguresMask & captureMask,figure);
                    nd = 7 - i & 7;
                    mask = (all[1] >> 8 * nd) & 255;
                    add(moves, i, Mask.rook_V[i][(int) mask] & ~ourFiguresMask & captureMask,figure);
                }
                if (getBit(ourFigures[bishop],i) || isQueen) {
                    int figure = ((isQueen) ? queen : bishop) + color;
                    int nd =(i >> 3) - (i & 7) + 7;
                    int shift = ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2));
                    long mask = (all[2] >> shift) & 255;
                    add(moves, i, Mask.bishop_L[i][(int) mask] & ~ourFiguresMask & captureMask,figure);
                    nd = (i >> 3) + (i & 7);
                    shift = ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2));
                    mask = (all[3] >> shift) & 255;
                    add(moves, i, Mask.bishop_R[i][(int) mask] & ~ourFiguresMask & captureMask,figure);
                }
            }
        }
            return sortMoves(moves);
    }

    private ArrayList<Integer> sortMoves(ArrayList<Integer> moves) {


        int[][] k = new int[moves.size()][2];

        int count = 0;
        for (int moveNum :
                moves) {
            int promotion = moveNum & 7;
            // int figure =  15 & (moveNum >> 3);
            int move[] = {moveNum >> 13, (moveNum >> 7) & 63};
            if (promotion != 0) {
                int costs[] = {Eval.costQueen, Eval.costRook, Eval.costKnight,Eval.costBishop};
                int cost = costs[promotion - 1];
                k[count][0] = 400 * cost;
                k[count][1] = count;
                count++;
                continue;
            }
            if (getBit(white[king],move[1]) || getBit(black[king],move[1])) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(moveNum);
                return list;
            }
            k[count][0] = 400 * Math.abs(cost(move[1])) - ((cost(move[1]) == 0) ? -1 : 1) * Math.abs(cost(move[0]));
            k[count][1] = count;
            count++;
        }
        Arrays.sort(k, Comparator.comparingInt(a -> -a[0]));

        ArrayList<Integer> sort = new ArrayList<>();

        for (int[] num : k) {
            sort.add(moves.get(num[1]));
        }

           for (int moveNum :
                moves)
            if (lastMove == ((moveNum >> 7) & 63)) {
                sort.remove(new Integer(moveNum));
                sort.add(0, moveNum);
                break;
            }

        int[] hash = AI.history.get(getKey());

        if (hash != null)
            for (int i = hash.length - 1; i >= 0; i--)
                for (int j = sort.size() - 1; j >= 0; j--)
                    if (hash[i] == sort.get(j)) {
                        sort.add(0, sort.remove(j));
                        break;
                    }



        return sort;
    }

    int cost(int to) {
        if (!getBit(all[0], to))
            return 0;
        int costs[] = {Eval.costPawn, Eval.costRook, Eval.costKnight, Eval.costBishop, Eval.costQueen, Eval.costKing};
        for (int i = 0; i < 6; i++)
            if (getBit(white[i], to))
                return costs[i];
            else if (getBit(black[i], to))
                return -costs[i];
        return 0;
    }

    private boolean attack_cell(boolean white, int cell){
        long[] enemyFigures = ((white) ? black : this.white);
        if ((Mask.knight[cell] & enemyFigures[knight]) != 0)
            return true;
        if ((Mask.king[cell] & enemyFigures[king]) != 0)
            return true;
        if ((((white) ? (black[pawn] & Mask.white_pawn_attack[cell]) : (this.white[pawn] & Mask.black_pawn_attack[cell]))) != 0)
            return true;

        long attack;
        int nd = cell >>3;
        long mask = (all[0] >> 8 * nd) & 255;
        attack = Mask.rook_G[cell][(int) mask];
        nd = 7 - cell & 7;
        mask = (all[1] >> 8 * nd) & 255;
        attack |= Mask.rook_V[cell][(int) mask];

        if ((attack & (enemyFigures[rook] | enemyFigures[queen])) != 0)
            return true;

        nd = (cell >> 3) - (cell & 7) + 7;
        int shift = ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2));
        mask = all[2] >> shift & 255;
        attack = Mask.bishop_L[cell][(int) mask];
        nd = (cell >> 3) + (cell & 7);
        shift = ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2));
        mask = all[3] >> shift & 255;
        attack |= Mask.bishop_R[cell][(int) mask];

        return  (attack & (enemyFigures[rook] | enemyFigures[queen])) != 0;
    }


    private static double log2 = Math.log(2);

    boolean isCheckTo(boolean white){
        long king = (white) ? this.white[Constants.king] : black[Constants.king];
        return attack_cell(white,(int)Math.round(Math.log(king)/log2));
    }

    private static int[][] castle_cells = {{1,2,3},{3,4,5},{57,58,59},{59,60,61}};

    private boolean canCastle(int type){
        boolean white = type <= 1;
        for (int cell : castle_cells[type])
            if (attack_cell(white,cell))
                return false;
        return true;
    }

    boolean isPasteTo(boolean white){
        return isCheckMateTo(white,true);
    }

    boolean isCheckMateTo(boolean white) {
        return isCheckMateTo(white,false);
    }

    private boolean isCheckMateTo(boolean white, boolean paste){
        try {
            if (paste != isCheckTo(white)) {
                for (int move :
                        getMoves(white)) {
                    BitBoard bitBoard = Game.makeMove(make_bitboard_from(this), move);
                    int move2 = bitBoard.getMoves(!white, true).get(0);
                    bitBoard = Game.makeMove(bitBoard, move2);
                    if (bitBoard.white[king] != 0 && bitBoard.black[king] != 0)
                        return false;
                }
            } else
                return false;
            return true;
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    int getFigure(int from){
        for (int i = 0; i < 6; i++)
            if (getBit(white[i],from))
                return i + 1;
        return 0;
    }
}

class Mask {
    static long[] white_pawn_attack = new long[64];
    static long[] white_pawn_move = new long[64];
    static long[] black_pawn_attack = new long[64];
    static long[] black_pawn_move = new long[64];
    static long[] knight = new long[64];
    static long[] king = new long[64];
    static long[][] bishop_R = new long[64][256];
    static long[][] bishop_L = new long[64][256];
    static long[][] rook_G = new long[64][256];
    static long[][] rook_V = new long[64][256];
    static long[] castle_white = {0b110, 0b1110000};
    static long[] castle_black = new long[2];
    static long[] cell_default = new long[64];
    static long[] cell_rotated_90 = new long[64];
    static long[] cell_rotated_45_left = new long[64];
    static long[] cell_rotated_45_right = new long[64];

    public static void main(String[] args) {
        calculating();

        BitBoard bitBoard = BitBoard.make_bitboard_start();
        long st = System.currentTimeMillis();
        for (int i = 0; i < 1000 * 1000; i++) {
            bitBoard.getMoves(true);
        }
        long secondTime = System.currentTimeMillis() - st;
        System.out.println("Время на ход: " + secondTime);
    }

    private static boolean B(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    private static long rotate_90(long mask) {
        long rotate = 0;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (((mask >> (8 * (7 - x) + y)) & 1) != 0) {
                    rotate |= 1L << (8 * y + x);
                }
        return rotate;
    }

    private static long rotate_R(long mask) {
        long rotated = 0;
        int n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 8 * i;
            for (int j = 0; j <= i; j++) {
                if (((mask >> n) & 1) != 0)
                    rotated |= 1L << j + m;
                m -= 8;
                n++;
            }
        }
        label:
        for (int i = 8; i < 16; i++) {
            int m = 8 * i;
            for (int j = 0; j <= i; j++) {
                if (j + m > -1 && j + m < 64 && ((j + m) >> 3) + ((j + m) & 7) > 7) {
                    if (((mask >> n) & 1) != 0)
                        rotated |= 1L << j + m;
                    n++;
                    if (j + m == 63)
                        break label;
                }
                m -= 8;
            }
        }
        return rotated;
    }

    private static long rotate_L(long mask) {
        long rotated = 0;
        int n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++) {
                if (((mask >> n) & 1) != 0)
                    rotated |= 1L << j + 8 * m;
                m++;
                n++;
            }
        }
        for (int i = 8; i < 16; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++) {
                if (j + 8 * m > -1 && j + 8 * m < 64 && (j + 8 * m) / 8 > ((j + 8 * m) & 7)) {
                    if (((mask >> n) & 1) != 0)
                        rotated |= 1L << j + 8 * m;
                    n++;
                }
                m++;
            }
        }
        return rotated;
    }

    static void calculating() {
        castle_black[0] = castle_white[0] << 56;
        castle_black[1] = castle_white[1] << 56;
        int i = 0;
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++) {
                //cell
                cell_default[8 * y + x] |= 1L << 8 * y + x;
                //knight
                for (int y2 = -2; y2 <= 2; y2 += 4)
                    for (int x2 = -1; x2 <= 1; x2 += 2)
                        if (B(y + y2, x + x2))
                            knight[i] |= 1L << (8 * (y + y2) + x + x2);
                for (int y2 = -1; y2 <= 1; y2 += 2)
                    for (int x2 = -2; x2 <= 2; x2 += 4)
                        if (B(y + y2, x + x2))
                            knight[i] |= 1L << (8 * (y + y2) + x + x2);
                //White_pawn_at
                if (B(x + 1, y + 1))
                    white_pawn_attack[i] |= 1L << (8 * (y + 1) + x + 1);
                if (B(x - 1, y + 1))
                    white_pawn_attack[i] |= 1L << (8 * (y + 1) + x - 1);
                if (B(x, y + 1))
                    white_pawn_move[i] |= 1L << (8 * (y + 1) + x);
                if (i >= 8 && i <= 15)
                    white_pawn_move[i] |= 1L << (i + 8 * 2);
                //black pawn
                if (B(x + 1, y - 1))
                    black_pawn_attack[i] |= 1L << (8 * (y - 1) + x + 1);
                if (B(x - 1, y - 1))
                    black_pawn_attack[i] |= 1L << (8 * (y - 1) + x - 1);
                if (B(x, y - 1))
                    black_pawn_move[i] |= 1L << (8 * (y - 1) + x);
                if (i >= 48 && i <= 55)
                    black_pawn_move[i] |= 1L << (i - 8 * 2);

                //king
                int direction[][] = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
                for (int[] d : direction)
                    if (B(x + d[0], y + d[1]))
                        king[i] |= 1L << (x + d[0] + 8 * (y + d[1]));

                //rook
                for (long block = 0; block < 256; block++) {
                    int nd = y;
                    long mask = block << 8 * nd;
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{1, 0}, {-1, 0}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            rook_G[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            rook_G[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }

                    nd = 7 - x;
                    mask = rotate_90(block << 8 * nd);
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{0, 1}, {0, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            rook_V[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            rook_V[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }

                }
                //bishop
                for (long block = 0; block < 256; block++) {
                    int nd = y - x + 7;
                    long mask = rotate_L(block << ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2)));
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{1, 1}, {-1, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            bishop_L[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            bishop_L[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }

                    nd = y + x;
                    mask = rotate_R(block << ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2)));
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{-1, 1}, {1, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            bishop_R[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            bishop_R[i][(int) block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }
                }
                i++;
            }

        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                cell_rotated_90[8 * y + x] |= 1L << 8 * (7 - x) + y;
            }
        int n = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            int m = 0;
            for (int j = 7 - i2; j <= 7; j++) {
                cell_rotated_45_left[j + 8 * m] |= 1L << n;
                m++;
                n++;
            }
        }
        for (int i2 = 8; i2 < 16; i2++) {
            int m = 0;
            for (int j = 7 - i2; j <= 7; j++) {
                if (j + 8 * m > -1 && j + 8 * m < 64 && ((j + 8 * m) >> 3) > ((j + 8 * m) & 7)) {
                    cell_rotated_45_left[j + 8 * m] |= 1L << n;
                    n++;
                }
                m++;
            }
        }


        n = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            int m = 8 * i2;
            for (int j = 0; j <= i2; j++) {
                cell_rotated_45_right[j + m] |= 1L << n;
                m -= 8;
                n++;
            }
        }
        label:
        for (int i2 = 8; i2 < 16; i2++) {
            int m = 8 * i2;
            for (int j = 0; j <= i2; j++) {
                if (j + m > -1 && j + m < 64 && ((j + m) >> 3) + ((j + m) & 7) > 7) {
                    cell_rotated_45_right[j + m] |= 1L << n;
                    n++;
                    if (j + m == 63)
                        break label;
                }
                m -= 8;
            }
        }
    }
}
