
public class Eval {
    static final int costKing = 30000;
    static final int costQueen = 1100;
    static final int costKnight = 400;
    static final int costBishop = 410;
    static final int costRook = 600;
    static final int costPawn = 100;
    static int Pawn[] = {
            0, 0, 0, 0, 0, 0, 0, 0,
            12, 16, 24, 32, 32, 24, 16, 12,
            12, 16, 24, 32, 32, 24, 16, 12,
            8, 12, 16, 24, 24, 16, 12, 8,
            6, 8, 12, 16, 16, 12, 8, 6,
            6, 8, 2, 10, 10, 2, 8, 6,
            4, 4, 4, 0, 0, 4, 4, 4,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    static int KingStart[] = {
            0, 0, -4, -10, -10, -4, 0, 0,
            -4, -4, -8, -12, -12, -8, -4, -4,
            -12, -16, -20, -20, -20, -20, -16, -12,
            -16, -20, -24, -24, -24, -24, -20, -12,
            -16, -20, -24, -24, -24, -24, -20, -12,
            -12, -16, -20, -20, -20, -20, -16, -12,
            -4, -4, -8, -12, -12, -8, -4, -4,
            0, 0, -4, -10, -10, -4, 0, 0,
    };

    static int KingEnd[] = {
            0, 6, 12, 18, 18, 12, 6, 0,
            6, 12, 18, 24, 24, 18, 12, 6,
            12, 18, 24, 30, 30, 24, 18, 12,
            18, 24, 30, 36, 36, 30, 24, 18,
            18, 24, 30, 36, 36, 30, 24, 18,
            12, 18, 24, 30, 30, 24, 18, 12,
            6, 12, 18, 24, 24, 18, 12, 6,
            0, 6, 12, 18, 18, 12, 6, 0
    };

    static int Knight[] = {
            0, 4, 8, 10, 10, 8, 4, 0,
            4, 8, 16, 20, 20, 16, 8, 4,
            8, 16, 20, 24, 24, 20, 16, 8,
            10, 20, 28, 32, 32, 28, 20, 10,
            10, 20, 28, 32, 32, 28, 20, 10,
            8, 16, 20, 24, 24, 20, 16, 8,
            4, 8, 16, 20, 20, 16, 8, 4,
            0, 4, 8, 10, 10, 8, 4, 0
    };

    static int Bishop[] = {
            14, 14, 14, 14, 14, 14, 14, 14,
            14, 22, 18, 18, 18, 18, 22, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 22, 18, 18, 18, 18, 22, 14,
            14, 14, 14, 14, 14, 14, 14, 14,
    };

    static final int PawnDoubled = -10;
    static final int PawnIsolated = -19;

    static int evaluate(BitBoard bitBoard) {
        int score = 0;
        for (int i = 0; i < 64; i++) {
            score += bitBoard.cost(i);
            if (((bitBoard.WHITE_PAWN >> i) & 1) != 0)
                score += Pawn[63 - i];
            else if (((bitBoard.WHITE_KNIGHT >> i) & 1) != 0)
                score += Knight[63 - i];
            else if (((bitBoard.WHITE_BISHOP >> i) & 1) != 0)
                score += Bishop[63 - i];
            else if (((bitBoard.WHITE_KING >> i) & 1) != 0)
                score += KingStart[63 - i];
            else if (((bitBoard.BLACK_PAWN >> i) & 1) != 0)
                score -= Pawn[i];
            else if (((bitBoard.BLACK_KNIGHT >> i) & 1) != 0)
                score -= Knight[i];
            else if (((bitBoard.BLACK_BISHOP >> i) & 1) != 0)
                score -= Bishop[i];
            else if (((bitBoard.BLACK_KING >> i) & 1) != 0)
                score -= KingStart[i];
        }
        return score;

    }
}
