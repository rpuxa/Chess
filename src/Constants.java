public interface Constants {
    int pawn = 0;
    int rook = 1;
    int knight = 2;
    int bishop = 3;
    int queen = 4;
    int king = 5;
    int white_pawn = 1;
    int white_rook = 2;
    int white_knight = 3;
    int white_bishop = 4;
    int white_queen = 5;
    int white_king = 6;
    int black_pawn = 7;
    int black_rook = 8;
    int black_knight = 9;
    int black_bishop = 10;
    int black_queen = 11;
    int black_king = 12;
    int white_short_castle_move = ((193 << 4) + 6) << 3;
    int white_long_castle_move = ((197 << 4) + 6) << 3;
    int black_long_castle_move = (((3837 << 4) + 12) << 3);
    int black_short_castle_move = ((3833 << 4) + 12) << 3;
    int H1 = 0;
    int A1 = 7;
    int H8 = 56;
    int A8 = 63;
    int E1 = 3;
    int E8 = 59;
}
