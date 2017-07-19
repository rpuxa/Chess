import java.io.IOException;

import static Util.BitUtils.*;

class Eval {
    
    static final int costKing = 30000;
    static final int costQueen = 1100;
    static final int costKnight = 400;
    static final int costBishop = 400;
    static final int costRook = 600;
    static final int costPawn = 100;

    private static int Pawn[] = {
            0, 0, 0, 0, 0, 0, 0, 0,
            12, 16, 24, 32, 32, 24, 16, 12,
            12, 16, 24, 32, 32, 24, 16, 12,
            8, 12, 16, 24, 24, 16, 12, 8,
            6, 8, 12, 16, 16, 12, 8, 6,
            6, 8, 2, 10, 10, 2, 8, 6,
            4, 4, 4, 0, 0, 4, 4, 4,
            0, 0, 0, 0, 0, 0, 0, 0
    };

    private static int KingStart[] = {
            5, 5, -4, -10, -10, -4, 5, 5,
            -4, -4, -8, -12, -12, -8, -4, -4,
            -12, -16, -20, -20, -20, -20, -16, -12,
            -16, -20, -24, -24, -24, -24, -20, -12,
            -16, -20, -24, -24, -24, -24, -20, -12,
            -12, -16, -20, -20, -20, -20, -16, -12,
            -4, -4, -8, -12, -12, -8, -4, -4,
            5, 5, -4, -10, -10, -4, 5, 5,
    };

    private static int KingEnd[] = {
            0, 6, 12, 18, 18, 12, 6, 0,
            6, 12, 18, 24, 24, 18, 12, 6,
            12, 18, 24, 30, 30, 24, 18, 12,
            18, 24, 30, 36, 36, 30, 24, 18,
            18, 24, 30, 36, 36, 30, 24, 18,
            12, 18, 24, 30, 30, 24, 18, 12,
            6, 12, 18, 24, 24, 18, 12, 6,
            0, 6, 12, 18, 18, 12, 6, 0
    };

    private static int Knight[] = {
            0, 4, 8, 10, 10, 8, 4, 0,
            4, 8, 16, 20, 20, 16, 8, 4,
            8, 16, 20, 24, 24, 20, 16, 8,
            10, 20, 28, 32, 32, 28, 20, 10,
            10, 20, 28, 32, 32, 28, 20, 10,
            8, 16, 20, 24, 24, 20, 16, 8,
            4, 8, 16, 20, 20, 16, 8, 4,
            0, 4, 8, 10, 10, 8, 4, 0
    };

    private static int Bishop[] = {
            14, 14, 14, 14, 14, 14, 14, 14,
            14, 22, 18, 18, 18, 18, 22, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 18, 22, 22, 22, 22, 18, 14,
            14, 22, 18, 18, 18, 18, 22, 14,
            14, 14, 14, 14, 14, 14, 14, 14,
    };

    private static final int PawnDoubled = -10;
    private static final int PawnIsolated = -19;
    private static final int PawnPassedBlock = 101;
    private static final int PawnPassedFree = 128;
    private static final int materialEndGame = 801;
    private static final int BishopPairEndgame = 95;
    private static final int BishopPairMidgame = 20;
    private static final int PawnPassedSquare = 50;
    private static final int KnightOutpost = 10;
    private static final int Rook7th = 24;
    private static final int RookOpen = 17;
    private static final int RookDoubled = 4;
    private static final int[] KingPawnShield = {0,50,90,120};
    private static final int[] QueenKingTropism = {99,98,94,90,85,80,79,72,65,55,44,32,15,0};
    private static final int[] KnightMobility = {-11,-3,5,9,12,15,17,18,19};
    private static final int[] BishopMobility = {-5,5,13,20,25,31,37,43,49,52,55,57,58,59,60,60,60};
    private static final int[] mask_vertical_extended = new int[64];
    private static final int[] mask_vertical = new int[64];
    private static final int[] mask_pawn_passedSquare_white = new int[64];
    private static final int[] mask_pawn_passedSquare_black = new int[64];
    private static final int[] mask_knightOutpost_white = new int[64];
    private static final int[] mask_knightOutpost_black = new int[64];
    private static final int[] mask_shield_black = new int[64];
    private static final int[] mask_shield_white = new int[64];




    static int evaluate(BitBoard bitBoard) {
        int material_white = 0,material_black = 0;

        int whiteBishops = 0,blackBishops = 0;

        int ShieldBlack = 0, ShieldWhite = 0;

        int KingWhite_g=0,KingWhite_v=0;
        int KingBlack_g=0,KingBlack_v=0;

        long white = bitBoard.white[pawn] | bitBoard.white[rook] | bitBoard.white[knight] | bitBoard.white[bishop] | bitBoard.white[queen] | bitBoard.white[king];
        long black = bitBoard.black[pawn] | bitBoard.black[rook] | bitBoard.black[knight] | bitBoard.black[bishop] | bitBoard.black[queen] | bitBoard.black[king];

        for (int i = 0; i < 64; i++) {
            if (getBit(bitBoard.white[king],i)) {
                KingWhite_g = i/8;
                KingWhite_v = i%8;
                ShieldWhite = Long.bitCount(mask_shield_white[i] & bitBoard.white[pawn]);
            }
            else if (getBit(bitBoard.black[king],i)) {
                KingBlack_g = i/8;
                KingBlack_v = i%8;
                ShieldBlack = Long.bitCount(mask_shield_black[i] & bitBoard.black[pawn]);
            }

            if (getBit(white,i) && !getBit(bitBoard.white[pawn],i) && !getBit(bitBoard.white[king],i)) {
                if (getBit(bitBoard.white[bishop],i))
                    whiteBishops++;
                material_white += bitBoard.cost(i);
                continue;
            }
            if (getBit(black,i) && !getBit(bitBoard.black[pawn],i) && !getBit(bitBoard.black[king],i)) {
                if (getBit(bitBoard.black[bishop],i))
                    blackBishops++;
                material_black -= bitBoard.cost(i);
            }
        }

        boolean endGameForWhite = material_black < materialEndGame;
        boolean endGameForBlack = material_white < materialEndGame;
        boolean endGame = endGameForBlack && endGameForWhite;

        int score = 0;

        if (whiteBishops >= 2)
            if (endGameForWhite)
                score += BishopPairEndgame;
            else
                score += BishopPairMidgame;

        if (blackBishops >= 2)
            if (endGameForBlack)
                score -= BishopPairEndgame;
            else
                score -= BishopPairMidgame;


        for (int i = 0; i < 64; i++) {
            score += bitBoard.cost(i);
            if (getBit(bitBoard.white[pawn],i)) {

                score += Pawn[63 - i];
                if ((mask_vertical_extended[i] & bitBoard.white[pawn]) == 0)
                    score += PawnIsolated;
                if ((mask_vertical_extended[i] & bitBoard.black[pawn]) == 0) {
                    if (((black >> i + 8) & 1) != 0)
                        score += PawnPassedBlock;
                    else
                        score += PawnPassedFree;
                    if (endGame && (mask_pawn_passedSquare_white[i] & bitBoard.black[king]) == 0)
                        score += PawnPassedSquare;
                }
                if ((mask_vertical[i] & bitBoard.white[pawn]) != 0)
                    score += PawnDoubled;

            } else if (getBit(bitBoard.white[rook],i)) {

                if (i/8 == 7)
                    score += Rook7th;
                if ((mask_vertical[i] & (bitBoard.white[pawn] | bitBoard.black[pawn])) == 0)
                    score += RookOpen;
                if ((mask_vertical[i] & bitBoard.white[rook]) != 0)
                    score += RookDoubled;

            } else if (getBit(bitBoard.white[knight],i)) {

                score += KnightMobility[Long.bitCount(Mask.knight[i] & ~white)];
                if ((mask_vertical_extended[i] & bitBoard.black[pawn]) == 0 && (mask_knightOutpost_white[i] & bitBoard.white[pawn]) != 0)
                    score += KnightOutpost;
                score += Knight[63 - i];

            }
            else if (getBit(bitBoard.white[bishop],i)) {
                int nd = i / 8 - i % 8 + 7;
                long mask = (bitBoard.all[2] >> ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2))) & 255;
                long moves = Mask.bishop_L[i][(int) mask] & ~white;
                nd = i / 8 + i % 8;
                mask = (bitBoard.all[3] >> ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2))) & 255;
                score += BishopMobility[Long.bitCount((Mask.bishop_R[i][(int) mask] & ~white) | moves)];
                score += Bishop[63 - i];
            } else if (getBit(bitBoard.white[queen],i)) {
                try {
                    score += QueenKingTropism[Math.abs(i / 8 - KingBlack_g) + Math.abs(i % 8 - KingBlack_v) - 1];
                }
                catch (ArrayIndexOutOfBoundsException e){
                    System.out.println();
                }
            } else if (getBit(bitBoard.white[king],i)) {

                if (!endGameForWhite) {
                    score += KingStart[63 - i];
                    score += KingPawnShield[ShieldWhite];
                }
                else
                    score += KingEnd[63 - i];

            }
            else if (getBit(bitBoard.black[pawn],i)) {

                score -= Pawn[i];
                if ((mask_vertical_extended[i] & bitBoard.black[pawn]) == 0)
                    score -= PawnIsolated;
                if ((mask_vertical_extended[i] & bitBoard.white[pawn]) == 0) {
                    if (((white >> i - 8) & 1) != 0)
                        score -= PawnPassedBlock;
                    else
                        score -= PawnPassedFree;
                    if (endGame && (mask_pawn_passedSquare_black[i] & bitBoard.white[king]) == 0)
                        score -= PawnPassedSquare;
                }
                if ((mask_vertical[i] & bitBoard.black[pawn]) != 0)
                    score -= PawnDoubled;

            } else if (getBit(bitBoard.black[rook],i)){

                if (i/8 == 1)
                    score -= Rook7th;
                if ((mask_vertical[i] & (bitBoard.white[pawn] | bitBoard.black[pawn])) == 0)
                    score -= RookOpen;
                if ((mask_vertical[i] & bitBoard.black[rook]) != 0)
                    score -= RookDoubled;

            } else if (getBit(bitBoard.black[knight],i)) {

                score -= KnightMobility[Long.bitCount(Mask.knight[i] & ~black)];
                if ((mask_vertical_extended[i] & bitBoard.white[pawn]) == 0 && (mask_knightOutpost_black[i] & bitBoard.black[pawn]) != 0)
                    score -= KnightOutpost;
                score -= Knight[i];

            }
            else if (getBit(bitBoard.black[bishop],i)){
                int nd = i / 8 - i % 8 + 7;
                long mask = (bitBoard.all[2] >> ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2))) & 255;
                long moves = Mask.bishop_L[i][(int) mask] & ~black;
                nd = i / 8 + i % 8;
                mask = (bitBoard.all[3] >> ((nd >= 9) ? ((21 - nd) * (nd - 8) / 2 + 36) : (nd * (nd + 1) / 2))) & 255;
                score -= BishopMobility[Long.bitCount((Mask.bishop_R[i][(int) mask] & ~black) | moves)];
                score -= Bishop[i];
            } else if (getBit(bitBoard.black[queen],i)) {
                score -= QueenKingTropism[Math.abs(i/8 - KingWhite_g) + Math.abs(i%8 - KingWhite_v) - 1];
            } else if (getBit(bitBoard.black[king],i)) {

                if (!endGameForBlack) {
                    score -= KingStart[i];
                    score -= KingPawnShield[ShieldBlack];
                } else
                    score -= KingEnd[i];

            }
        }
        return score;
    }

    static void calculate(){
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                if (j != i && Math.abs(i / 8 - j / 8) <= 1)
                    mask_vertical_extended[i] |= 1L << j;
                if (j != i && i / 8 == j / 8)
                    mask_vertical[i] |= 1L << j;
                if (j / 8 >= i / 8 && Math.abs(j % 8 - i % 8) <= 7 - i / 8)
                    mask_pawn_passedSquare_white[i] |= 1L << j;
                if (j / 8 <= i / 8 && Math.abs(j % 8 - i % 8) <= i / 8)
                    mask_pawn_passedSquare_black[i] |= 1L << j;
            }
            if (i/8>=2 && i!=7 && i%8!=7)
                mask_knightOutpost_white[i] |= 1L << i - 7;
            if (i/8>=2 && i!=7 && i%8!=0)
                mask_knightOutpost_white[i] |= 1L << i - 9;
            if (i/8>=5 && i!=0 && i%8!=0)
                mask_knightOutpost_black[i] |= 1L << i + 7;
            if (i/8>=5 && i!=0 && i%8!=7)
                mask_knightOutpost_black[i] |= 1L << i + 9;

            if (i/8 == 0 && i%8!=7)
                mask_shield_white[i] |= 1L << i + 9;
            if (i/8 == 0)
                mask_shield_white[i] |= 1L << i + 8;
            if (i/8 == 0 && i%8!=0)
                mask_shield_white[i] |= 1L << i + 7;

            if (i/8 == 7 && i%8!=7)
                mask_shield_black[i] |= 1L << i - 7;
            if (i/8 == 7)
                mask_shield_black[i] |= 1L << i - 8;
            if (i/8 == 7 && i%8!=0)
                mask_shield_black[i] |= 1L << i - 9;

        }
    }
}
