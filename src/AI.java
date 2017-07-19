import static Util.BitUtils.*;
import java.util.*;

class AI {

    static int scoreNow;

    private static int r =2;
    private static int margin_razoring = Eval.costQueen;
    private static int margin_futility = Eval.costPawn/2;

    static Map<Long,Byte> mDraw = new HashMap<>();

    static Map<Long,int[]> history = new HashMap<>();

   /* static int generateIndex(double[] p) {
        double prob = Math.random();

        int index = Arrays.binarySearch(p,prob);
        if (index < 0) index = ~index;

        return index;
    }*/

       static int bfs(BitBoard bitBoard,int depth) {
         /*  String name = "";
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
           }*/

           int num = 0;
           for (int i = 2; i <= depth; i++) {
               num = AI.alphaBeta(BitBoard.make_bitboard_from(bitBoard),0,i,-100000,100000,false,new HashMap<>(mDraw),false);
               if (scoreNow > 20000 || scoreNow < -20000)
                   break;
           }
          // history.clear();
           return num;
       }

       static boolean isDraw(Map<Long,Byte> thisDraw){
           for (byte pos:
                thisDraw.values())
               if (pos>=3)
                   return true;
           return false;
       }

    private static int alphaBeta(BitBoard bitBoard, int depth, int maxDepth, int alpha, int beta, boolean nullMove, Map<Long, Byte> draw, boolean capture) {
        if (depth!=0) {
            Byte num = draw.get(bitBoard.getKey());
            draw.put(bitBoard.getKey(), (byte) ((num == null) ? 1 : num + 1));
            if (isDraw(draw))
                return 0;
        }
        if (bitBoard.white[king]==0)
            return -30000 + depth - 1;
        if (bitBoard.black[king]==0)
            return 30000 - depth + 1;

        boolean isCheck = bitBoard.isCheckTo(depth % 2 == 1);

        if (depth >= maxDepth)
            return quies(bitBoard, depth, alpha, beta);

        ArrayList<int[]> sort = new ArrayList<>();

        if (depth == 0) {
            int best = 0;
            int count = 0;
            for (int move : bitBoard.getMoves(false)) {
                System.out.print(count + " ");
                int result = alphaBeta(Game.makeMove(BitBoard.make_bitboard_from(bitBoard), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),false);
                if (result < beta) {
                    sort.add(new int[]{result, move});
                    beta = result;
                    best = move;
                }
                count++;
            }
            historyHashing(-1, sort, bitBoard.getKey());
            scoreNow = beta;
            System.out.println();
            return best;
        }

        if (depth % 2 == 0) {
            int min = 100000;
            if (depth>3 && maxDepth - depth > 2 && !nullMove && !isCheck && !capture && alphaBeta(BitBoard.make_bitboard_nullMove_from(bitBoard), depth + 1 + r, maxDepth, alpha, beta, true,new HashMap<>(draw),false) <= alpha)
                return alpha;
            if (maxDepth - depth <= 4 && !nullMove && !capture && !isCheck) {
                int eval = Eval.evaluate(bitBoard);
                if (maxDepth - depth <= 2 && eval + margin_futility <= alpha)
                    return alpha;
                if (eval + margin_razoring <= alpha)
                    return alpha;
            }
            for (int move : bitBoard.getMoves(false)) {
                boolean cap = getBit(bitBoard.all[0],((move>>7) & 63));
                int result = alphaBeta(Game.makeMove(BitBoard.make_bitboard_from(bitBoard), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),cap);
                if (result < min) {
                    sort.add(new int[]{result, move});
                    min  = result;
                }
                if (result < beta)
                    beta = result;

                if (alpha >= beta)
                    break;

                if (beta < -20000)
                    break;

            }
            historyHashing(1, sort, bitBoard.getKey());
            if (min > 20000 && Math.abs(30000-depth-min)==1 && !bitBoard.isCheckTo(false))
                min = 0;
            return min;
        } else {
            int max = -100000;
            if (depth>3 && maxDepth - depth > 2 && !nullMove && !isCheck && !capture && alphaBeta(BitBoard.make_bitboard_nullMove_from(bitBoard), depth + 1 + r, maxDepth, alpha, beta, true,new HashMap<>(draw),false) >= beta)
                return beta;
            if (maxDepth - depth <= 4 && !nullMove && !capture && !isCheck) {
                int eval = Eval.evaluate(bitBoard);
                if (maxDepth - depth <= 2 && eval - margin_futility >= beta)
                    return beta;
                if (eval - margin_razoring >= beta)
                    return beta;
            }
            for (int move : bitBoard.getMoves(true)) {
                boolean cap = getBit(bitBoard.all[0],((move>>7) & 63));
                int result = alphaBeta(Game.makeMove(BitBoard.make_bitboard_from(bitBoard), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),cap);
                if (result > max) {
                    sort.add(new int[]{result, move});
                    max = result;
                }
                if (result > alpha)
                    alpha = result;

                if (alpha >= beta) {
                    break;
                }
                if (alpha > 20000) {
                    break;
                }
            }
            historyHashing(-1, sort, bitBoard.getKey());
            if (max < -20000 && Math.abs(-30000+depth-max)==1 && !bitBoard.isCheckTo(true))
                max = 0;
            return max;
        }
    }

    private static int quies(BitBoard bitBoard, int depth, int alpha, int beta) {
        if (bitBoard.white[king]==0)
            return -30000 + depth - 1;
        if (bitBoard.black[king]==0)
            return 30000 - depth + 1;

        int val = Eval.evaluate(BitBoard.make_bitboard_from(bitBoard));

        if (depth % 2 == 0) {
            if (val < beta)
                beta = val;
            for (int move : bitBoard.getMoves(false,true)) {
                int result = quies(Game.makeMove(BitBoard.make_bitboard_from(bitBoard),move), depth + 1, alpha, beta);
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
                for (int move : bitBoard.getMoves(true, true)) {
                    int result = quies(Game.makeMove(BitBoard.make_bitboard_from(bitBoard), move), depth + 1, alpha, beta);
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

    private static void historyHashing(int side, ArrayList<int[]> sort, long key) {
        int[][] arr = sort.toArray(new int[sort.size()][2]);
        Arrays.sort(arr, Comparator.comparingInt(a -> side * a[0]));
        int[] hash = new int[sort.size()];
        for (int i = 0; i < hash.length; i++)
            hash[i] = arr[i][1];
        history.put(key, hash);
    }
}
