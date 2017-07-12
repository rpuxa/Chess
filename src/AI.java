import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class AI {

    static int scoreNow;

    static int r =2;

    static Map<Long,Byte> mDraw = new HashMap<>();

    static Map<Long,int[]> history = new HashMap<>();

    static int generateIndex(double[] p) {
        double prob = Math.random();

        int index = Arrays.binarySearch(p,prob);
        if (index < 0) index = ~index;

        return index;
    }

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
           for (int i = 6; i <= depth; i++) {
               num = AI.alphaBeta(new BitBoard(bitBoard),0,i,-100000,100000,false,new HashMap<>(mDraw),null)[0];
                if (Game.makeMove(new BitBoard(bitBoard),num).WHITE_KING==0)
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

    static int[] alphaBeta(BitBoard bitBoard, int depth, int maxDepth, int alpha, int beta, boolean nullMove, Map<Long,Byte> draw,int[] kill) {
        if (depth!=0) {
            Byte num = draw.get(bitBoard.getKey());
            draw.put(bitBoard.getKey(), (byte) ((num == null) ? 1 : num + 1));
            if (isDraw(draw))
                return new int[]{0, 0, 0};
        }
        if (bitBoard.WHITE_KING==0)
            return new int[]{-30000 + depth - 1,0,0};
        if (bitBoard.BLACK_KING==0)
            return new int[]{30000 - depth + 1,0,0};

        boolean isCheck = bitBoard.isCheckTo(depth % 2 == 1);

        if (!nullMove) {
            if (isCheck && maxDepth < 6)
                maxDepth += 2;
        }

        if (depth >= maxDepth)
            return new int[]{quies(bitBoard, depth, alpha, beta),0,0};

        ArrayList<int[]> sort = new ArrayList<>();

        if (depth == 0) {
            int best = 0;
            int count = 0;
            for (int move : bitBoard.getMoves(false, null)) {
                System.out.print(count + " ");
                int result = alphaBeta(Game.makeMove(new BitBoard(bitBoard, false), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),null)[0];
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
            return new int[]{best};
        }

        if (depth % 2 == 0) {
            int min = 100000;
            if (depth>3 && !nullMove && !isCheck && alphaBeta(new BitBoard(bitBoard, true), depth + 1 + r, maxDepth, alpha, beta, true,new HashMap<>(draw),null)[0] <= alpha)
                return new int[]{alpha,0,0};
            int[] killMoves = new int[2];
            for (int move : bitBoard.getMoves(false, kill)) {
                int[] tmp = alphaBeta(Game.makeMove(new BitBoard(bitBoard, false), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),((killMoves[0]!=0) ? killMoves.clone():null));
                killMoves[0] = tmp[1];
                killMoves[1] = tmp[2];
                int result = tmp[0];
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
            int[] killer = historyHashing(1, sort, bitBoard.getKey());
            if (min > 20000 && Math.abs(30000-depth-min)==1 && !bitBoard.isCheckTo(false))
                min = 0;
            return new int[]{min,killer[0],killer[1]};
        } else {
            int max = -100000;
            if (depth>3 && !nullMove && !isCheck && alphaBeta(new BitBoard(bitBoard, true), depth + 1 + r, maxDepth, alpha, beta, true,new HashMap<>(draw),null)[0] >= beta)
                return new int[]{beta,0,0};
            int[] killMoves = new int[2];
            for (int move : bitBoard.getMoves(true,kill)) {
                int[] tmp = alphaBeta(Game.makeMove(new BitBoard(bitBoard, false), move), depth + 1, maxDepth, alpha, beta, nullMove,new HashMap<>(draw),((killMoves[0]!=0) ? killMoves.clone():null));
                killMoves[0] = tmp[1];
                killMoves[1] = tmp[2];
                int result = tmp[0];
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
            int[] killer = historyHashing(-1, sort, bitBoard.getKey());
            if (max < -20000 && Math.abs(-30000+depth-max)==1 && !bitBoard.isCheckTo(true))
                max = 0;
            return new int[]{max,killer[0],killer[1]};
        }
    }

    static int quies(BitBoard bitBoard,int depth, int alpha, int beta) {
        if (bitBoard.WHITE_KING==0)
            return -30000 + depth - 2;
        if (bitBoard.BLACK_KING==0)
            return 30000 - depth + 2;

        int val = Eval.evaluate(new BitBoard(bitBoard));

        if (depth % 2 == 0) {
            if (val < beta)
                beta = val;
            for (int move : bitBoard.getMoves(false,null,true)) {
                int result = quies(Game.makeMove(new BitBoard(bitBoard),move), depth + 1, alpha, beta);
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
                for (int move : bitBoard.getMoves(true,null, true)) {
                    int result = quies(Game.makeMove(new BitBoard(bitBoard), move), depth + 1, alpha, beta);
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

    static int[] historyHashing(int side, ArrayList<int[]> sort,long key) {
        int[][] arr = sort.toArray(new int[sort.size()][2]);
        Arrays.sort(arr, Comparator.comparingInt(a -> side*a[0]));
        int[] hash = new int[sort.size()];
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
