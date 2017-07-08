import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class AI {

    static int scoreNow;

    static int r = 2;

    static Map<Long,Byte> mDraw = new HashMap<>();

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

    static int KingEnd[] = {
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

    static final int PawnDoubled = -10;
    static final int PawnIsolated = -19;

    static Map<Long,short[]> history = new HashMap<>();

    static int generateIndex(double[] p) {
        double prob = Math.random();

        int index = Arrays.binarySearch(p,prob);
        if (index < 0) index = ~index;

        return index;
    }

       static int bfs(Board board,int depth) {
           String name = "";
           for (int i = 0; i < 8; i++)
               for (int j = 0; j < 8; j++)
                   name += board.pos[i][j];
           name += false;
           ArrayList<short[]> b = new ArrayList<>();
           File file = new File("Debut/"+name);
           if (file.exists()) {
               try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Debut/"+name))) {
                   b = (ArrayList<short[]>) ois.readObject();
               } catch (Exception ex) {
               }
               short[] moves = new short[b.size()];
               double[] p = new double[b.size()];
               int total = 0;
               for (int i = 0; i < b.size(); i++) {
                   moves[i] = b.get(i)[0];
                   total += b.get(i)[1];
                   p[i] = b.get(i)[1];
               }
               for (int i = 0; i < p.length; i++)
                   p[i] /= total;
               for (int i = 1; i < p.length; i++)
                   p[i] += p[i-1];
               System.out.println("Ход взят из базы");
               return moves[generateIndex(p)];
           }
           int num = 0;
           for (int i = 6; i <= depth; i++) {
               num = AI.alphaBeta(new Board(board,false),0,i,-100000,100000,false,new HashMap<>(mDraw),null)[0];
                if (Game.makeMove(new Board(board,false),(short)num).haveKing()!=0)
                    break;
           }
           history.clear();
           return num;
       }

       static boolean isDraw(Map<Long,Byte> thisDraw){
           for (byte pos:
                thisDraw.values())
               if (pos>=3)
                   return true;
           return false;
       }

    static int[] alphaBeta(Board board, int depth, int maxDepth, int alpha, int beta, boolean nullMove, Map<Long,Byte> draw,int[] kill) {
        Byte num = draw.get(board.getKey());
        draw.put(board.getKey(), (byte) ((num == null) ? 1 : num + 1));
        if (isDraw(draw))
            return new int[]{0,0,0};

        int have = board.haveKing();
        if (have != 0)
            return new int[]{have + ((depth % 2 == 0) ? -depth + 2 : depth - 2),0,0};

        boolean isCheck = board.isCheckTo(depth % 2 == 1);

        if (!nullMove) {
            if (isCheck && maxDepth < 6)
                maxDepth += 2;
        }

        if (depth >= maxDepth && !isCheck)
            return new int[]{quies(board, depth, alpha, beta),0,0};

        ArrayList<short[]> sort = new ArrayList<>();

        if (depth == 0) {
            int best = 0;
            int count = 0;
            for (short move : board.getMoves(false, false)) {
                System.out.print(count + " ");
                int result = alphaBeta(Game.makeMove(new Board(board, false), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),null)[0];
                if (result < beta) {
                    sort.add(new short[]{(short) result, move});
                    beta = result;
                    best = move;
                }
                count++;
            }
            historyHashing(-1, sort, board.getKey());
            scoreNow = beta;
            System.out.println();
            return new int[]{best};
        }

        if (depth % 2 == 0) {
            if (depth>3 && !nullMove && !isCheck && alphaBeta(new Board(board, true), depth + 1 + r, maxDepth, alpha, beta, true,new HashMap<>(draw),null)[0] <= alpha)
                return new int[]{alpha,0,0};
            int[] killMoves = new int[2];
            for (short move : board.getMoves(false, false, kill)) {
                int[] tmp = alphaBeta(Game.makeMove(new Board(board, false), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),((killMoves[0]!=0) ? killMoves.clone():null));
                killMoves[0] = tmp[1];
                killMoves[1] = tmp[2];
                int result = tmp[0];
                if (result < beta) {
                    sort.add(new short[]{(short) result, move});
                    beta = result;
                }
                if (alpha >= beta)
                    break;

                if (beta < -20000)
                    break;

            }
            int[] killer = historyHashing(1, sort, board.getKey());
            if (beta > 20000 && !board.isCheckTo(false))
                beta = 0;
            return new int[]{beta,killer[0],killer[1]};
        } else {
            if (depth>3 && !nullMove && !isCheck && alphaBeta(new Board(board, true), depth + 1 + r, maxDepth, alpha, beta, true,new HashMap<>(draw),null)[0] >= beta)
                return new int[]{beta,0,0};
            int[] killMoves = new int[2];
            for (short move : board.getMoves(true, false)) {
                int[] tmp = alphaBeta(Game.makeMove(new Board(board, false), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),((killMoves[0]!=0) ? killMoves.clone():null));
                killMoves[0] = tmp[1];
                killMoves[1] = tmp[2];
                int result = tmp[0];
                if (result > alpha) {
                    sort.add(new short[]{(short) result, move});
                    alpha = result;
                }
                if (alpha >= beta) {
                    break;
                }

                if (alpha > 20000) {
                    break;
                }
            }
            int[] killer = historyHashing(-1, sort, board.getKey());
            if (alpha < -20000 && !board.isCheckTo(true))
                alpha = 0;
            return new int[]{alpha,killer[0],killer[1]};
        }
    }

    static int quies(Board board,int depth, int alpha, int beta) {
        int have = board.haveKing();
        if (have != 0)
            return have + ((depth % 2 == 0) ? -depth + 2 : depth - 2);

        int val = evaluate(new Board(board,false));

        if (depth % 2 == 0) {
            if (val < beta)
                beta = val;
            for (short move : board.getMoves(false,true)) {
                int result = quies(Game.makeMove(new Board(board,false),move), depth + 1, alpha, beta);
                if (result < beta)
                    beta = result;
                if (alpha >= beta)
                    break;
                if (beta < -20000)
                    break;
            }
            return beta;
        } else {
            if (val > alpha)
                alpha = val;
                for (short move : board.getMoves(true, true)) {
                    int result = quies(Game.makeMove(new Board(board, false), move), depth + 1, alpha, beta);
                    if (result > alpha)
                        alpha = result;
                    if (alpha >= beta)
                        break;
                    if (alpha > 20000)
                        break;
                }
                return alpha;
        }
    }


    static int evaluate(Board board) {
        int score = 0;
        int endW = 0,endB = 0;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                int figure = board.pos[x][y];
                if (figure != 0) {
                    switch (figure) {
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6: {
                            endW += board.cost(x,y);
                        }
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        case 14: {
                            endB -= board.cost(x,y);
                        }
                    }
                }
                //сдвоенная пешка
            }
           boolean endGame = endB+endW<18011;
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
                            if (endB>910)
                                score += KingStart[8 * (7 - y) + x];
                            else
                                score += KingEnd[8 * (7 - y) + x];
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
                            if (endW>910)
                                score -= KingStart[8 * y + 7 - x];
                            else
                                score -= KingEnd[8 * y + 7 - x];
                            break;
                        }
                    }

                }
            }

        return score;
    }

    static int[] historyHashing(int side, ArrayList<short[]> sort,long key) {
        short[][] arr = sort.toArray(new short[sort.size()][2]);
        Arrays.sort(arr, Comparator.comparingInt(a -> side*a[0]));
        short[] hash = new short[sort.size()];
        for (int i = 0; i < hash.length; i++)
            hash[i] = arr[i][1];
        history.put(key,hash);
        try {
            return new int[]{hash[0], hash[1]};
        }
        catch (ArrayIndexOutOfBoundsException e){
            try {
                return new int[]{hash[0],0};
            }
            catch (ArrayIndexOutOfBoundsException ignore) {}
        }
        return new int[]{0,0};
    }

}
