import java.util.*;

public class AI {

    static int scoreNow;

    static int p = 0;

    static int Pawn[] =  {
            0,  0,  0,  0,  0,  0,  0,  0,
            12,16,24,32,32,24,16,12,
            12,16,24,32,32,24,16,12,
            8,12,16,24,24,16,12,  8,
            6,  8,12,16,16,12,  8,  6,
            6,  8,  2,10,10,  2,  8,  6,
            4,  4,  4,  0,  0,  4,  4,  4,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    static int KingStart[] = {
            0,0,-4,-10,-10,-4,0,0,
            -4,-4,-8,-12,-12,-8,-4,-4,
            -12,-16,-20,-20,-20,-20,-16,-12,
            -16,-20,-24,-24,-24,-24,-20,-12,
            -16,-20,-24,-24,-24,-24,-20,-12,
            -12,-16,-20,-20,-20,-20,-16,-12,
            -4,-4,-8,-12,-12,-8,-4,-4,
            0,0,-4,-10,-10,-4,0,0,
    };

    int KingEnd[] = {
            0,  6,12,18,18,12,  6,  0,
            6,12,18,24,24,18,12,  6,
            12,18,24,30,30,24,18,12,
            18,24,30,36,36,30,24,18,
            18,24,30,36,36,30,24,18,
            12,18,24,30,30,24,18,12,
            6,12,18,24,24,18,12,  6,
            0,  6,12,18,18,12,  6,  0
    };

    static int Knight[] = {
            0,  4,  8,10,10,  8,  4,  0,
            4,  8,16,20,20,16,  8,  4,
            8,16,20,24,24,20,16,  8,
            10,20,28,32,32,28,20,10,
            10,20,28,32,32,28,20,10,
            8,16,20,24,24,20,16,  8,
            4,  8,16,20,20,16,  8,  4,
            0,  4,  8,10,10,  8,  4,  0
    };

    static int Bishop[] = {
            14,14,14,14,14,14,14,14,
            14,22,18,18,18,18,22,14,
            14,18,22,22,22,22,18,14,
            14,18,22,22,22,22,18,14,
            14,18,22,22,22,22,18,14,
            14,18,22,22,22,22,18,14,
            14,22,18,18,18,18,22,14,
            14,14,14,14,14,14,14,14,
    };

    static Map<Long,short[]> history = new HashMap<>();


    //   static double bfs(Board board){


    static int alphaBeta(Board board, int depth, int maxDepth, int alpha, int beta) {
        int have = board.haveKing();
        if (have!=0)
            return have;

        if (board.isCheckTo(depth%2==1) && maxDepth<6)
            maxDepth+=2;

        if (depth >= maxDepth)
            return evaluate(new Board(board));

       ArrayList<short[]> sort = new ArrayList<>();

        if (depth == 0) {
            int best = 0;
            int count = 0;
            for (short move : board.getMoves(false)) {
                System.out.println(count);
                int result = alphaBeta(Game.makeMove(new Board(board),move), depth + 1, maxDepth, alpha, beta);
                if (result < beta) {
                    sort.add(new short[]{(short)result,move});
                    beta = result;
                    best = count;
                }
                count++;
            }
          historyHashing(-1,sort, board.getKey());
            scoreNow = beta;
            return best;
        }

        if (depth % 2 == 0) {
            for (short move : board.getMoves(false)) {
                int result = alphaBeta(Game.makeMove(new Board(board),move), depth + 1, maxDepth, alpha, beta);
                if (result < beta) {
                    sort.add(new short[]{(short)result,move});
                    beta = result;
                }
                if (alpha >= beta)
                    break;
                if (beta < -20000)
                    break;
            }
            historyHashing(1,sort, board.getKey());
            return beta;
        } else {
            for (short move : board.getMoves(true)) {
                int result = alphaBeta(Game.makeMove(new Board(board),move), depth + 1, maxDepth, alpha, beta);
                if (result > alpha) {
                    sort.add(new short[]{(short)result,move});
                    alpha = result;
                }
                if (alpha >= beta)
                    break;

                if (alpha > 20000)
                    break;
            }
            historyHashing(-1,sort, board.getKey());
            return alpha;
        }
    }

  //  static int quies() {

    //}


    static int evaluate(Board board) {
        int score = 0;

        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                    score += board.cost(x, y);
                int figure = board.pos[x][y];
                if (figure != 0) {
                    switch (figure) {
                        case 1: {
                            score += Pawn[8 * (7 - y) + x];
                            break;
                        }
                        case 4: {
                            score += Knight[8 * (7 - y) + x];
                            break;
                        }
                        case 5: {
                            score += Bishop[8 * (7 - y) + x];
                            break;
                        }
                        case 7:
                        case 8: {
                            score += KingStart[8 * (7 - y) + x];
                            break;
                        }
                        case 9: {
                            score -= Pawn[8 * y + 7 - x];
                            break;
                        }
                        case 12: {
                            score -= Knight[8 * y + 7 - x];
                            break;
                        }
                        case 13: {
                            score -= Bishop[8 * y + 7 - x];
                            break;
                        }
                        case 15:
                        case 16: {
                            score -= KingStart[8 * y + 7 - x];
                            break;
                        }
                    }

                }
            }

        return score;
    }

    static void historyHashing(int side, ArrayList<short[]> sort,long key) {
        short[][] arr = sort.toArray(new short[sort.size()][2]);
        Arrays.sort(arr, Comparator.comparingInt(a -> side*a[0]));
        short[] hash = new short[sort.size()];
        for (int i = 0; i < hash.length; i++)
            hash[i] = arr[i][1];
        history.put(key,hash);
    }

}
