import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BitBoard {
    long WHITE_PAWN;
    long WHITE_KNIGHT;
    long WHITE_ROOK;
    long WHITE_BISHOP;
    long WHITE_QUEEN;
    long WHITE_KING;
    long BLACK_PAWN;
    long BLACK_KNIGHT;
    long BLACK_ROOK;
    long BLACK_BISHOP;
    long BLACK_QUEEN;
    long BLACK_KING;
    long PASS;
    byte CASTLE;
    long ALL;
    long ALL_ROTATED_90;
    long ALL_ROTATED_45_LEFT;
    long ALL_ROTATED_45_RIGHT;


    BitBoard(boolean empty){
        WHITE_PAWN = 0;
        WHITE_KNIGHT = 0;
        WHITE_ROOK = 0;
        WHITE_BISHOP = 0;
        WHITE_QUEEN = 0;
        WHITE_KING = 0;
        BLACK_PAWN = 0;
        BLACK_KNIGHT = 0;
        BLACK_ROOK = 0;
        BLACK_BISHOP = 0;
        BLACK_QUEEN = 0;
        BLACK_KING = 0;
        PASS = 0;
        CASTLE = 0b1111;
        ALL = 0;
        ALL_ROTATED_90 = 0;
        ALL_ROTATED_45_LEFT = 0;
        ALL_ROTATED_45_RIGHT = 0;
    }

    BitBoard(){
        WHITE_PAWN = 0b1111111100000000;
        WHITE_KNIGHT = 0b1000010;
        WHITE_ROOK = 0b10000001;
        WHITE_BISHOP = 0b100100;
        WHITE_QUEEN = 0b10000;
        WHITE_KING = 0b1000;
        BLACK_PAWN = WHITE_PAWN<<40;
        BLACK_KNIGHT = WHITE_KNIGHT<<56;
        BLACK_ROOK = WHITE_ROOK<<56;
        BLACK_BISHOP = WHITE_BISHOP<<56;
        BLACK_QUEEN = WHITE_QUEEN<<56;
        BLACK_KING = WHITE_KING<<56;
        PASS = 0;
        CASTLE = 0b1111;
        rotate();
    }
    BitBoard(BitBoard bitBoard) {
        WHITE_PAWN = bitBoard.WHITE_PAWN;
        WHITE_KNIGHT = bitBoard.WHITE_KNIGHT;
        WHITE_ROOK = bitBoard.WHITE_ROOK;
        WHITE_BISHOP = bitBoard.WHITE_BISHOP;
        WHITE_QUEEN = bitBoard.WHITE_QUEEN;
        WHITE_KING = bitBoard.WHITE_KING;
        BLACK_PAWN = bitBoard.BLACK_PAWN;
        BLACK_KNIGHT = bitBoard.BLACK_KNIGHT;
        BLACK_ROOK = bitBoard.BLACK_ROOK;
        BLACK_BISHOP = bitBoard.BLACK_BISHOP;
        BLACK_QUEEN = bitBoard.BLACK_QUEEN;
        BLACK_KING = bitBoard.BLACK_KING;
        PASS = bitBoard.PASS;
        CASTLE = bitBoard.CASTLE;
        rotate();
    }
    BitBoard(BitBoard bitBoard, boolean nullMove) {
        WHITE_PAWN = bitBoard.WHITE_PAWN;
        WHITE_KNIGHT = bitBoard.WHITE_KNIGHT;
        WHITE_ROOK = bitBoard.WHITE_ROOK;
        WHITE_BISHOP = bitBoard.WHITE_BISHOP;
        WHITE_QUEEN = bitBoard.WHITE_QUEEN;
        WHITE_KING = bitBoard.WHITE_KING;
        BLACK_PAWN = bitBoard.BLACK_PAWN;
        BLACK_KNIGHT = bitBoard.BLACK_KNIGHT;
        BLACK_ROOK = bitBoard.BLACK_ROOK;
        BLACK_BISHOP = bitBoard.BLACK_BISHOP;
        BLACK_QUEEN = bitBoard.BLACK_QUEEN;
        BLACK_KING = bitBoard.BLACK_KING;
        CASTLE = bitBoard.CASTLE;
        rotate();
    }

    void rotate(){
        ALL = WHITE_PAWN | WHITE_KNIGHT | WHITE_ROOK | WHITE_BISHOP | WHITE_QUEEN | WHITE_KING | BLACK_PAWN | BLACK_KNIGHT | BLACK_ROOK | BLACK_BISHOP | BLACK_QUEEN | BLACK_KING;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (((ALL >> (8*y+x)) & 1) != 0) {
                    ALL_ROTATED_90 |= 1L << 8 * (7 - x) + y;
                }
        int n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++){
                if (((ALL >> j+8*m) & 1) != 0)
                    ALL_ROTATED_45_LEFT |= 1L << n;
                m++;
                n++;
            }
        }
        for (int i = 8; i < 16; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++){
                if (j+8*m>-1 && j+8*m<64 && (j+8*m)/8>(j+8*m)%8) {
                    if (((ALL >> j+8*m) & 1) != 0)
                        ALL_ROTATED_45_LEFT |= 1L << n;
                    n++;
                }
                m++;
            }
        }


        n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 8*i;
            for (int j = 0; j <= i; j++){
                if (((ALL >> j+m) & 1) != 0)
                    ALL_ROTATED_45_RIGHT |= 1L << n;
                m-=8;
                n++;
            }
        }
        label: for (int i = 8; i < 16; i++) {
            int m = 8*i;
            for (int j = 0; j <= i; j++){
                if (j+m>-1 && j+m<64 && (j+m)/8+(j+m)%8>7) {
                    if (((ALL >> j+m) & 1) != 0)
                        ALL_ROTATED_45_RIGHT |= 1L << n;
                    n++;
                    if (j+m == 63)
                        break label;
                }
                m-=8;
            }
        }
    }

    long getKey() {
        long WHITE = WHITE_PAWN | WHITE_KNIGHT | WHITE_ROOK | WHITE_BISHOP | WHITE_QUEEN | WHITE_KING;
        long BLACK = BLACK_PAWN | BLACK_KNIGHT | BLACK_ROOK | BLACK_BISHOP | BLACK_QUEEN | BLACK_KING;
        long hash = 0L;
            for (int i = 0; i < 64; i++)
                if ((((WHITE | BLACK) >> i) & 1) != 0){
                if (((WHITE_PAWN >> i) & 1) != 0)
                    hash ^= Game.zKeys[0][i];
                else if (((WHITE_ROOK >> i) & 1) != 0)
                    hash ^= Game.zKeys[1][i];
                else if (((WHITE_BISHOP >> i) & 1) != 0)
                    hash ^= Game.zKeys[2][i];
                else if (((WHITE_KNIGHT >> i) & 1) != 0)
                    hash ^= Game.zKeys[3][i];
                else if (((WHITE_QUEEN >> i) & 1) != 0)
                    hash ^= Game.zKeys[4][i];
                else if (((WHITE_KING >> i) & 1) != 0)
                    hash ^= Game.zKeys[5][i];

                else if (((BLACK_PAWN >> i) & 1) != 0)
                    hash ^= Game.zKeys[6][i];
                else if (((BLACK_ROOK >> i) & 1) != 0)
                    hash ^= Game.zKeys[7][i];
                else if (((BLACK_BISHOP >> i) & 1) != 0)
                    hash ^= Game.zKeys[8][i];
                else if (((BLACK_KNIGHT >> i) & 1) != 0)
                    hash ^= Game.zKeys[9][i];
                else if (((BLACK_QUEEN >> i) & 1) != 0)
                    hash ^= Game.zKeys[10][i];
                else if (((BLACK_KING >> i) & 1) != 0)
                    hash ^= Game.zKeys[11][i];
            }
            return hash;
    }

    void add(ArrayList<Integer> moves,int pos, long mask,int figureMove) {
        add(moves,pos,mask,figureMove,false);
    }
    void add(ArrayList<Integer> moves,int pos, long mask,int figureMove,boolean promotion) {
        if (mask != 0)
            for (int i = 0; i < 64; i++)
                if (((mask >> i) & 1) != 0)
                    if (!promotion) {
                        moves.add(((((pos << 6) + i) << 4) + figureMove) << 3);
                    } else {
                        final short figures[] = {1,2,3,4};
                        for (short figure :figures)
                            moves.add((((((pos << 6) + i) << 4) + figureMove) << 3) + figure);
                    }
    }

    ArrayList<Integer> getMoves(boolean isTurnWhite, int[] kill){
       return getMoves(isTurnWhite,kill,false);
    }

    ArrayList<Integer> getMoves(boolean isTurnWhite,int[] kill, boolean capture) {
        ArrayList<Integer> moves = new ArrayList<>();
        long WHITE = WHITE_PAWN | WHITE_KNIGHT | WHITE_ROOK | WHITE_BISHOP | WHITE_QUEEN | WHITE_KING;
        long BLACK = BLACK_PAWN | BLACK_KNIGHT | BLACK_ROOK | BLACK_BISHOP | BLACK_QUEEN | BLACK_KING;
        for (int i = 0; i < 64; i++) {
            boolean queen = ((((isTurnWhite) ? WHITE_QUEEN : BLACK_QUEEN) >> i) & 1) != 0;
            if (((WHITE >> i) & 1) != 0 && isTurnWhite || ((BLACK >> i) & 1) != 0 && !isTurnWhite) {
                if (((((isTurnWhite) ? WHITE_PAWN : BLACK_PAWN) >> i) & 1) != 0) {
                    if (((ALL >> i + 8 * ((isTurnWhite) ? 1 : -1)) & 1) == 0) {
                        add(moves, i, ((isTurnWhite) ? Mask.white_pawn_move[i] : Mask.black_pawn_move[i]) & ~ALL & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),1+((isTurnWhite) ? 0:6), i / 8 == ((isTurnWhite) ? 6 : 1));
                    }
                    add(moves, i, ((isTurnWhite) ? Mask.white_pawn_Mask[i] : Mask.black_pawn_Mask[i]) & ((isTurnWhite) ? BLACK : WHITE) | PASS,1+((isTurnWhite) ? 0:6), i / 8 == ((isTurnWhite) ? 6 : 1));
                } else if (((((isTurnWhite) ? WHITE_KNIGHT : BLACK_KNIGHT) >> i) & 1) != 0)
                    add(moves, i, Mask.knight[i] & ((isTurnWhite) ? ~WHITE : ~BLACK) & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),3+((isTurnWhite) ? 0:6));
                else if (((((isTurnWhite) ? WHITE_KING : BLACK_KING) >> i) & 1) != 0) {
                    add(moves, i, Mask.king[i] & ((isTurnWhite) ? ~WHITE : ~BLACK) & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),6+((isTurnWhite) ? 0:6));
                    if (isTurnWhite) {
                        if ((ALL & Mask.castle_white[0]) == 0 && (WHITE_ROOK & 1) != 0 && ((CASTLE >> 3) & 1) != 0)
                            moves.add(((193<<4) + 6) << 3);
                        else if ((ALL & Mask.castle_white[1]) == 0 && ((WHITE_ROOK >> 7) & 1) != 0 && ((CASTLE >> 2) & 1) != 0)
                            moves.add(((197<<4) + 6) << 3);
                    } else {
                        if ((ALL & Mask.castle_black[0]) == 0 && ((BLACK_ROOK >> 56) & 1) != 0 && ((CASTLE >> 1) & 1) != 0)
                            moves.add(((3833<<4) + 12) << 3);
                        else if ((ALL & Mask.castle_black[1]) == 0 && ((BLACK_ROOK >> 63) & 1) != 0 && (CASTLE & 1) != 0)
                            moves.add((((3837<<4) + 12) << 3));
                    }
                } else if (((((isTurnWhite) ? WHITE_ROOK : BLACK_ROOK) >> i) & 1) != 0 || queen) {
                    int nd = i / 8;
                    long mask = (ALL >> 8 * nd) & 255;
                    add(moves, i, Mask.rook_G[i][(int) mask] & ~((isTurnWhite) ? WHITE : BLACK) & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),((queen) ? 5:2)+((isTurnWhite) ? 0:6));
                    nd = 7 - i % 8;
                    mask = (ALL_ROTATED_90 >> 8 * nd) & 255;
                    add(moves, i, Mask.rook_V[i][(int) mask] & ~((isTurnWhite) ? WHITE : BLACK) & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),((queen) ? 5:2)+((isTurnWhite) ? 0:6));
                }
                if (((((isTurnWhite) ? WHITE_BISHOP : BLACK_BISHOP) >> i) & 1) != 0 || queen) {
                    int nd = i / 8 - i % 8 + 7;
                    long mask = ALL_ROTATED_45_LEFT >> ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2)) & 255;
                    add(moves, i, Mask.bishop_L[i][(int) mask] & ~((isTurnWhite) ? WHITE : BLACK) & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),((queen) ? 5:4)+((isTurnWhite) ? 0:6));
                    nd = i / 8 + i % 8;
                    mask = ALL_ROTATED_45_RIGHT >> ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2)) & 255;
                    add(moves, i, Mask.bishop_R[i][(int) mask] & ~((isTurnWhite) ? WHITE : BLACK) & ((capture) ? ((isTurnWhite) ? BLACK : WHITE) : -1),((queen) ? 5:4)+((isTurnWhite) ? 0:6));
                }
            }
        }
            return sortMoves(moves, kill);
    }

    ArrayList<Integer> sortMoves(ArrayList<Integer> moves, int kill[]) {
        if (kill != null)
            for (int i = 1; i >= 0; i--)
                if (moves.contains(kill[i])) {
                    moves.remove(new Integer(kill[i]));
                    moves.add(0, kill[i]);
                }


        int[][] k = new int[moves.size()][2];

        int count = 0;
        for (int moveNum :
                moves) {
            int promotion = moveNum & 7;
           // int figure =  15 & (moveNum >> 3);
            int move[] = {moveNum>>13,(moveNum>>7) & 63};
            if (promotion != 0) {
                int cost = 0;
                if (promotion == 1)
                    cost = Eval.costQueen;
                if (promotion == 2)
                    cost = Eval.costRook;
                if (promotion == 3)
                    cost = Eval.costKnight;
                if (promotion == 4)
                    cost = Eval.costBishop;
                k[count][0] = 400*cost;
                k[count][1] = count;
                count++;
                continue;
            }
            if (((WHITE_KING >> move[1]) & 1) != 0 || ((BLACK_KING >> move[1]) & 1) != 0) {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(moveNum);
                return list;
            }
            k[count][0] = 400 * Math.abs(cost(move[1])) - ((cost(move[1]) == 0) ? -1:1)*Math.abs(cost(move[0]));
            k[count][1] = count;
            count++;
        }
        Arrays.sort(k, Comparator.comparingInt(a -> -a[0]));

        ArrayList<Integer> sort = new ArrayList<>();

        for (int[] num : k) {
            sort.add(moves.get(num[1]));
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

        if ((((WHITE_PAWN | WHITE_KNIGHT | WHITE_ROOK | WHITE_BISHOP | WHITE_QUEEN | WHITE_KING | BLACK_PAWN | BLACK_KNIGHT | BLACK_ROOK | BLACK_BISHOP | BLACK_QUEEN | BLACK_KING) >> to) & 1) == 0)
            return 0;
        if (((WHITE_PAWN >> to) & 1) != 0)
            return Eval.costPawn;
        else if (((WHITE_ROOK >> to) & 1) != 0)
            return Eval.costRook;
        else if (((WHITE_BISHOP >> to) & 1) != 0)
            return Eval.costBishop;
        else if (((WHITE_KNIGHT >> to) & 1) != 0)
            return Eval.costKnight;
        else if (((WHITE_QUEEN >> to) & 1) != 0)
            return Eval.costQueen;
        else if (((WHITE_KING >> to) & 1) != 0)
            return Eval.costKing;
        else if (((BLACK_PAWN >> to) & 1) != 0)
            return -Eval.costPawn;
        else if (((BLACK_ROOK >> to) & 1) != 0)
            return -Eval.costRook;
        else if (((BLACK_BISHOP >> to) & 1) != 0)
            return -Eval.costBishop;
        else if (((BLACK_KNIGHT >> to) & 1) != 0)
            return -Eval.costKnight;
        else if (((BLACK_QUEEN >> to) & 1) != 0)
            return -Eval.costQueen;
        else if (((BLACK_KING >> to) & 1) != 0)
            return -Eval.costKing;
            return 0;
    }

    boolean isCheckTo(boolean white){
        ArrayList<Integer> move = getMoves(!white,null,true);
        try {
            if (white) {
                if (move.get(0) % 64 == Math.round(Math.log(WHITE_KING) / Mask.log2))
                    return true;
            } else {
                if (move.get(0) % 64 == Math.round(Math.log(BLACK_KING) / Mask.log2))
                    return true;
            }
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }

    boolean isPasteTo(boolean white){
        return isCheckMateTo(white,true);
    }

    boolean isCheckMateTo(boolean white) {
        return isCheckMateTo(white,false);
    }

    boolean isCheckMateTo(boolean white, boolean paste){
        try {
            if (paste != isCheckTo(white)) {
                for (int move :
                        getMoves(white, null)) {
                    BitBoard bitBoard = Game.makeMove(new BitBoard(this), move);
                    int move2 = bitBoard.getMoves(!white, null, true).get(0);
                    bitBoard = Game.makeMove(bitBoard, move2);
                    if (bitBoard.WHITE_KING != 0 && bitBoard.BLACK_KING != 0)
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
            if (((WHITE_PAWN >> from) & 1) != 0)
                return 1;

            if (((WHITE_ROOK >> from) & 1) != 0)
                return 2;

            if (((WHITE_KNIGHT >> from) & 1) != 0)
                return 3;

            if (((WHITE_BISHOP >> from) & 1) != 0)
                return 4;

            if (((WHITE_QUEEN >> from) & 1) != 0)
                return 5;

            if (((WHITE_KING >> from) & 1) != 0)
                return 6;

            if (((BLACK_PAWN >> from) & 1) != 0)
                return 7;

            if (((BLACK_ROOK >> from) & 1) != 0)
                return 8;

            if (((BLACK_KNIGHT >> from) & 1) != 0)
                return 9;

            if (((BLACK_BISHOP >> from) & 1) != 0)
                return 10;

            if (((BLACK_QUEEN >> from) & 1) != 0)
                return 11;

            if (((BLACK_KING >> from) & 1) != 0)
                return 12;
        return 0;
    }
}

class Mask {
    static long[] white_pawn_Mask = new long[64];
    static long[] white_pawn_move= new long[64];
    static long[] black_pawn_Mask= new long[64];
    static long[] black_pawn_move= new long[64];
    static long[] knight= new long[64];
    static long[] king= new long[64];
    static long[][] bishop_R= new long[64][256];
    static long[][] bishop_L= new long[64][256];
    static long[][] rook_G= new long[64][256];
    static long[][] rook_V= new long[64][256];
    static long[] castle_white = {0b110,0b1110000};
    static long[] castle_black = new long[2];
    static double log2 = Math.log(2);
    static long[] cell_default = new long[64];
    static long[] cell_rotated_90 = new long[64];
    static long[] cell_rotated_45_left = new long[64];
    static long[] cell_rotated_45_right = new long[64];

    public static void main(String[] args) {
        calculating();
    }

    static boolean B(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    static long rotate_90(long mask){
        long rotate = 0;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (((mask >> (8 * (7 - x) + y)) & 1) != 0) {
                    rotate |= 1L << (8*y+x);
                }
                return rotate;
    }

    static long rotate_R(long mask){
        long rotated = 0;
        int n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 8*i;
            for (int j = 0; j <= i; j++){
                if (((mask >> n) & 1) != 0)
                    rotated |= 1L << j+m;
                m-=8;
                n++;
            }
        }
        label: for (int i = 8; i < 16; i++) {
            int m = 8*i;
            for (int j = 0; j <= i; j++){
                if (j+m>-1 && j+m<64 && (j+m)/8+(j+m)%8>7) {
                    if (((mask >> n) & 1) != 0)
                        rotated |= 1L << j+m;
                    n++;
                    if (j+m == 63)
                        break label;
                }
                m-=8;
            }
        }
        return rotated;
    }

    static long rotate_L(long mask){
        long rotated = 0;
        int n = 0;
        for (int i = 0; i < 8; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++){
                if (((mask >> n) & 1) != 0)
                    rotated |= 1L << j+8*m;
                m++;
                n++;
            }
        }
        for (int i = 8; i < 16; i++) {
            int m = 0;
            for (int j = 7 - i; j <= 7; j++){
                if (j+8*m>-1 && j+8*m<64 && (j+8*m)/8>(j+8*m)%8) {
                    if (((mask >> n) & 1) != 0)
                        rotated |= 1L << j+8*m;
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
                 cell_default[8*y+x] |= 1L << 8*y+x;
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
                    white_pawn_Mask[i] |= 1L << (8 * (y + 1) + x + 1);
                if (B(x - 1, y + 1))
                    white_pawn_Mask[i] |= 1L << (8 * (y + 1) + x - 1);
                if (B(x, y + 1))
                    white_pawn_move[i] |= 1L << (8 * (y + 1) + x);
                if (i >= 8 && i <= 15)
                    white_pawn_move[i] |= 1L << (i + 8 * 2);
                //black pawn
                if (B(x + 1, y - 1))
                    black_pawn_Mask[i] |= 1L << (8 * (y - 1) + x + 1);
                if (B(x - 1, y - 1))
                    black_pawn_Mask[i] |= 1L << (8 * (y - 1) + x - 1);
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
                    long mask = block << 8*nd;
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{1, 0}, {-1, 0}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            rook_G[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            rook_G[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }

                    nd = 7-x;
                    mask = rotate_90(block << 8*nd);
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{0, 1}, {0, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            rook_V[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            rook_V[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }

                }
                    //bishop
                for (long block = 0; block < 256; block++) {
                    int nd = y-x+7;
                    long mask = rotate_L(block << ((nd >= 9) ? ((21-nd)*(nd-8)/2+36):(nd*(nd+1)/2)));
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{1, 1}, {-1, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            bishop_L[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            bishop_L[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }

                    nd = y+x;
                    mask = rotate_R(block << ((nd >= 9) ? ((21-nd)*(nd-8)/2+36):(nd*(nd+1)/2)));
                    for (int j = 0; j < 2; j++) {
                        int dir[][] = {{-1, 1}, {1, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1]) && ((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) == 0) {
                            bishop_R[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                        if (((mask >> (8 * (y + k * dir[j][1]) + x + k * dir[j][0])) & 1) != 0 && B(x + k * dir[j][0], y + k * dir[j][1]))
                            bishop_R[i][(int)block] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                    }
                }
                    i++;
            }

        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                    cell_rotated_90[8*y+x] |= 1L << 8 * (7 - x) + y;
                }
        int n = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            int m = 0;
            for (int j = 7 - i2; j <= 7; j++){
                cell_rotated_45_left[n] |= 1L << j + 8*m;
                m++;
                n++;
            }
        }
        for (int i2 = 8; i2 < 16; i2++) {
            int m = 0;
            for (int j = 7 - i2; j <= 7; j++){
                if (j+8*m>-1 && j+8*m<64 && (j+8*m)/8>(j+8*m)%8) {
                    cell_rotated_45_left[n] |= 1L << j + 8*m;
                    n++;
                }
                m++;
            }
        }


        n = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            int m = 8*i2;
            for (int j = 0; j <= i2; j++){
                cell_rotated_45_right[n] |= 1L << j + m;
                m-=8;
                n++;
            }
        }
        label: for (int i2 = 8; i2 < 16; i2++) {
            int m = 8*i2;
            for (int j = 0; j <= i2; j++){
                if (j+m>-1 && j+m<64 && (j+m)/8+(j+m)%8>7) {
                    cell_rotated_45_right[n] |= 1L << j + m;
                    n++;
                    if (j+m == 63)
                        break label;
                }
                m-=8;
            }
        }
    }
}
