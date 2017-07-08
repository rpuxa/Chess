import java.util.ArrayList;

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
    }

    void add(ArrayList<Short> moves,int pos, long mask){
        if (mask != 0)
        for (int i = 0; i < 64; i++)
            if (((mask >> i) & 1) != 0)
                moves.add((short)(64*pos+i));
    }

    ArrayList<Short> getMoves(boolean isTurnWhite){
       return getMoves(isTurnWhite,false);
    }

    ArrayList<Short> getMoves(boolean isTurnWhite, boolean capture){
        ArrayList<Short> moves = new ArrayList<>();
        long WHITE = WHITE_PAWN | WHITE_KNIGHT | WHITE_ROOK | WHITE_BISHOP | WHITE_QUEEN | WHITE_KING;
        long BLACK = BLACK_PAWN | BLACK_KNIGHT | BLACK_ROOK | BLACK_BISHOP | BLACK_QUEEN | BLACK_KING;
        long ALL = WHITE | BLACK;
        for (int i = 0; i < 64; i++) {
            if (((WHITE >> i) & 1) != 0 && isTurnWhite) {
                if (((WHITE_PAWN >> i) & 1) != 0){
                    if (((WHITE_PAWN & ~ALL >> i+8) & 1) == 0){
                        add(moves,i,Attack.white_pawn_move[i] & ~ALL & ((capture) ? BLACK : -1));
                    }
                    add(moves,i,Attack.white_pawn_attack[i] & BLACK);
                }
                else if (((WHITE_KNIGHT >> i) & 1) != 0)
                    add(moves,i,Attack.knight[i] & ~WHITE & ((capture) ? BLACK : -1));
                else if (((WHITE_KING >> i) & 1) != 0){
                    add(moves,i,Attack.king[i] & ~WHITE & ((capture) ? BLACK : -1));
                }
                else if (((WHITE_ROOK >> i) & 1) != 0 || ((WHITE_QUEEN >> i) & 1) != 0)
                    for (int d = 0; d < 4; d++){
                    int dir[] = {-1,1,1,-1};
                    long mask = Attack.rook[i][d] & ALL;
                    if (mask != 0)
                    for (int j = 63*(1-dir[d])/2; true; j+=dir[d])
                        if (((mask >> j) & 1) != 0) {
                            add(moves,i,Attack.rook_block[i][j][d] & ~WHITE & ((capture) ? BLACK : -1));
                            break;
                        }
                }
                if (((WHITE_BISHOP >> i) & 1) != 0 || ((WHITE_QUEEN >> i) & 1) != 0)
                    for (int d = 0; d < 4; d++){
                        int dir[] = {1,1,-1,-1};
                        long mask = Attack.bishop[i][d] & ALL;
                        if (mask != 0)
                            for (int j = 63*(1-dir[d])/2; true; j+=dir[d])
                                if (((mask >> j) & 1) != 0) {
                                    add(moves,i,Attack.bishop_block[i][j][d] & ~WHITE & ((capture) ? BLACK : -1));
                                    break;
                                }
                    }
            }
            if  (((BLACK >> i) & 1)!=0 && !isTurnWhite) {

            }
        }
        return moves;
    }
}

class Attack {
    static long[] white_pawn_attack = new long[64];
    static long[] white_pawn_move= new long[64];
    static long[] black_pawn_attack= new long[64];
    static long[] black_pawn_move= new long[64];
    static long[][] rook= new long[64][4];
    static long[] knight= new long[64];
    static long[][] bishop= new long[64][4];
    static long[] king= new long[64];
    static long[][][] bishop_block= new long[64][64][4];
    static long[][][] rook_block= new long[64][64][4];

    static boolean B(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    public static void main(String[] args) {
        calculating();
        BitBoard bitBoard = new BitBoard();
        bitBoard.getMoves(true);
    }

    static void calculating() {
        int i = 0;
        for (int y = 0; y < 8; y++)
            for (int x = 0; x < 8; x++) {
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
                    for (int j = 0; j < 4; j++) {
                        int dir[][] = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1])) {
                            rook[i][j] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                    }
                    //bishop
                    for (int j = 0; j < 4; j++) {
                        int dir[][] = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
                        int k = 1;
                        while (B(x + k * dir[j][0], y + k * dir[j][1])) {
                            bishop[i][j] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                            k++;
                        }
                    }
                    //rook
                    for (int block = 0; block < 64; block++)
                        for (int j = 0; j < 4; j++) {
                            int dir[][] = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
                            int k = 1;
                            while (B(x + k * dir[j][0], y + k * dir[j][1]) && (8 * (y + k * dir[j][1]) + x + k * dir[j][0]) != block) {
                                rook_block[i][block][j] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                                k++;
                            }
                            if ((8 * (y + k * dir[j][1]) + x + k * dir[j][0]) == block)
                                rook_block[i][block][j] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                        }
                    //bishop
                    for (int block = 0; block < 64; block++)
                        for (int j = 0; j < 4; j++) {
                            int dir[][] = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};
                            int k = 1;
                            while (B(x + k * dir[j][0], y + k * dir[j][1]) && (8 * (y + k * dir[j][1]) + x + k * dir[j][0]) != block) {
                                bishop_block[i][block][j] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                                k++;
                            }
                            if ((8 * (y + k * dir[j][1]) + x + k * dir[j][0]) == block)
                                bishop_block[i][block][j] |= 1L << (8 * (y + k * dir[j][1]) + x + k * dir[j][0]);
                        }

                    i++;
            }
    }
}
