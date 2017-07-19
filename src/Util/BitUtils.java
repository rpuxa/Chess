package Util;

public class BitUtils {
    public final static int pawn = 0;
    public final static int rook = 1;
    public final static int knight = 2;
    public final static int bishop = 3;
    public final static int queen = 4;
    public final static int king = 5;
    public final static int white_pawn = 1;
    public final static int white_rook = 2;
    public final static int white_knight = 3;
    public final static int white_bishop = 4;
    public final static int white_queen = 5;
    public final static int white_king = 6;
    public final static int black_pawn = 7;
    public final static int black_rook = 8;
    public final static int black_knight = 9;
    public final static int black_bishop = 10;
    public final static int black_queen = 11;
    public final static int black_king = 12;

    public static boolean getBit(long mask, int to) {
        return ((mask >> to) & 1) != 0;
    }

    public static long setBit(long mask, int to) {
        return mask | (1L << to);
    }

    public static long zeroBit(long mask, int to) {
        return mask & ~(1L << to);
    }

    public static byte zeroBit(byte mask, int to) {
        return (byte) (mask & ~(1 << to));
    }

    public static long swapBit(long mask, int to) {
        return mask ^ (1L << to);
    }
}
