import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class Game {

    static Board board = new Board();
    static long[][] zKeys = new long[16][64];

    public static void main(String[] args) throws InterruptedException {
        genZObristKeys();
       byte[][] pos = {
               {2,1,0,0,0,0,9,10},
               {4,1,0,0,0,0,9,12},
               {5,1,0,0,0,0,9,13},
               {6,1,0,0,0,0,9,14},
               {7,1,0,0,0,0,9,15},
               {5,1,0,0,0,0,9,13},
               {4,1,0,0,0,0,9,12},
               {2,1,0,0,0,0,9,10}
       };
       board = new Board(pos,(byte)-1);
       Move.board = new Board(board,false);
       new Gui().setVisible(true);
       start();
    }

    static void start() throws InterruptedException {
        while (true){
            System.out.println();
            int x1,x2,y1,y2;
            System.out.println("Ждем хода...");
            Move.block = false;
            while (Move.to == null || Move.to[1] == null || !Move.block) {
                Thread.sleep(100);
            }
            System.out.println("Ходим...");
            x1 = Move.from[0];
            y1 = Move.from[1];
            x2 = Move.to[0];
            y2 = Move.to[1];
            Move.from = null;
            Move.to = null;
            board = makeMove(new Board(board,false),x1,y1,x2,y2);
            Move.sounds("Sounds/whiteTurn.wav");
            Move.board = new Board(board,false);
            Move.updatePosition();
            if (board.isCheckMateTo(false)){
                JOptionPane.showMessageDialog(null, "Мат! Вы победили!");
                break;
            }
            long st = System.currentTimeMillis();
            int num = AI.alphaBeta(new Board(board,false),0,6,-100000,100000,false);
            long secondTime = System.currentTimeMillis() - st;
            System.out.println("Время на ход: " + secondTime);

            board = makeMove(new Board(board,false),(short)num);
            Move.sounds("Sounds/blackTurn.wav");
            Move.board = new Board(board,false);
            Move.updatePosition();
            System.out.println((double) AI.scoreNow/100);
            if (board.isCheckMateTo(true)){
                JOptionPane.showMessageDialog(null, "Мат! Компьютер победил!");
                break;
            }
        }

    }

    static void genZObristKeys(){
        Random rand = new Random();
        for (int i = 0; i < 16; i++)
            for (int j = 0; j < 64; j++)
            zKeys[i][j] = rand.nextLong();
    }

    static boolean makeLegalMove(Board board,int x,int y, int x1, int y1){
        return makeLegalMove(board,board.moveToNum(new byte[]{(byte)x,(byte)y,(byte)x1,(byte)y1}));
    }

    static boolean makeLegalMove(Board board,short move2){
        for (short move : board.getMoves(true))
            if (move == move2){
                makeMove(board,move);
                boolean have = board.haveKing()==0;
                ArrayList<Short> validMove = board.getMoves(false);
                makeMove(board,validMove.get(0));
                return !(have && board.haveKing() != 0);
            }
        return false;
    }

    static Board makeMove(Board board,short move){
        byte[] moveArr = board.numToMove(move);
        int x = moveArr[0],y = moveArr[1],x1 = moveArr[2],y1 = moveArr[3];
        return makeMove(board,x,y,x1,y1);
    }

    static Board makeMove(Board board,int x,int y, int x1, int y1){
        //превращение пешки
        if (board.pos[x][y]==1 && y==6){
            board.pos[x][y]=0;
            board.pos[x1][7]=(byte)y1;
            return board;
        }
        if (board.pos[x][y]==9 && y==1){
            board.pos[x][y]=0;
            board.pos[x1][0]=(byte)(y1+8);
            return board;
        }
        //рокировка
        if (board.pos[x][y]==7 && Math.abs(x-x1)==2){
            board.pos[x][y]=0;
            if (x1==2){
                board.pos[0][0]=0;
                board.pos[2][0]=8;
                board.pos[3][0]=3;
                return board;
            }
            if (x1==6){
                board.pos[7][0]=0;
                board.pos[6][0]=8;
                board.pos[5][0]=3;
                return board;
            }
        }
        if (board.pos[x][y]==15 && Math.abs(x-x1)==2){
            board.pos[x][y]=0;
            if (x1==2){
                board.pos[0][7]=0;
                board.pos[2][7]=16;
                board.pos[3][7]=11;
                return board;
            }
            if (x1==6){
                board.pos[7][7]=0;
                board.pos[6][7]=16;
                board.pos[5][7]=11;
                return board;
            }
        }

        //взятие на проходе
        if (y1==9){
            if (y==4){
                board.pos[board.pawn][5]=1;
                board.pos[x][y]=0;
                board.pos[board.pawn][4]=0;
            }
            if (y==3){
                board.pos[board.pawn][2]=1;
                board.pos[x][y]=0;
                board.pos[board.pawn][3]=0;
            }
            return board;
        }

        //обычный ход
        if (board.pos[x][y]==2){
            board.pos[x][y]=0;
            board.pos[x1][y1]=3;
            return board;
        }
        if (board.pos[x][y]==7){
            board.pos[x][y]=0;
            board.pos[x1][y1]=8;
            return board;
        }
        if (board.pos[x][y]==10){
            board.pos[x][y]=0;
            board.pos[x1][y1]=11;
            return board;
        }
        if (board.pos[x][y]==15){
            board.pos[x][y]=0;
            board.pos[x1][y1]=16;
            return board;
        }

        if (board.pos[x][y]==1 && y==1 && y1==3)
            board.pawn = (byte)x;
        if (board.pos[x][y]==9 && y==6 && y1==4)
            board.pawn = (byte)x;

        board.pos[x1][y1]=board.pos[x][y];
        board.pos[x][y]=0;
        return board;
    }
}

class Board {
    /* 0 - нет фигуры
       1 - белая пешка
       2 - белая ладья (неходившая)
       3 - белая ладья (ходившая)
       4 - белый конь
       5 - белый слон
       6 - белый ферзь
       7 - белый король (неходивший)
       8 - белый король (ходивший)
       9 - черная пешка
       10 - черная ладья (неходившая)
       11 - черная ладья (ходившая)
       12 - черный конь
       13 - черный слон
       14 - черный ферзь
       15 - черный король (неходивший)
       16 - черный король ходивший
     */
    byte[][] pos;
    byte pawn;

    Board() {
        pos = new byte[8][8];
    }

    Board(byte[][] pos, byte pawn) {
        this.pos = new byte[8][8];
        for (int i = 0; i < 8; i++)
            this.pos[i] = pos[i].clone();
        this.pawn = pawn;
    }

    Board(Board board, boolean nullMove) {
        this.pos = new byte[8][8];
        for (int i = 0; i < 8; i++)
            pos[i] = board.pos[i].clone();
        if (!nullMove)
        pawn = board.pawn;
    }

    ArrayList<Short> getMoves(boolean isTurnWhite) {
        ArrayList<Short> validMoves = new ArrayList<>();
        for (byte x = 0; x < 8; x++)
            for (byte y = 0; y < 8; y++) {
                byte figure = pos[x][y];
                if (figure > 0 && figure < 9 == isTurnWhite) {
                    switch (figure) {
                        case 1:
                        case 9: {
                            if (y == 1 && pos[x][y + 1] == 0 && pos[x][y + 2] == 0 && isTurnWhite || y == 6 && pos[x][y - 1] == 0 && pos[x][y - 2] == 0 && !isTurnWhite)
                                validMoves.add((short)(1000*x + 100*y+10*x + y + ((isTurnWhite) ? 2 : -2)));
                            if (pos[x][y + ((isTurnWhite) ? 1 : -1)] == 0) {
                                if (y != ((isTurnWhite) ? 6 : 1))
                                    validMoves.add((short)(1000*x + 100*y + 10*x + y + ((isTurnWhite) ? 1 : -1)));
                                else {
                                    validMoves.add((short)(1000*x + 100*y + 10*x + 6));
                                    validMoves.add((short)(1000*x + 100*y + 10*x + 5));
                                    validMoves.add((short)(1000*x + 100*y + 10*x + 3));
                                    validMoves.add((short)(1000*x + 100*y + 10*x + 2));
                                }
                            }
                            if (inBoard(x + 1, y + ((isTurnWhite) ? 1 : -1)) && pos[x + 1][y + ((isTurnWhite) ? 1 : -1)] != 0 && pos[x + 1][y + ((isTurnWhite) ? 1 : -1)] > 8 == isTurnWhite) {
                                if (y != ((isTurnWhite) ? 6 : 1))
                                    validMoves.add((short)(1000*x + 100*y + 10*(x+1) + y + ((isTurnWhite) ? 1 : -1)));
                                else {
                                    validMoves.add((short)(1000*x + 100*y + 10*(x+1) + 6));
                                    validMoves.add((short)(1000*x + 100*y + 10*(x+1) + 5));
                                    validMoves.add((short)(1000*x + 100*y + 10*(x+1) + 4));
                                    validMoves.add((short)(1000*x + 100*y + 10*(x+1) + 3));

                                }
                            }
                            if (inBoard(x - 1, y + ((isTurnWhite) ? 1 : -1)) && pos[x - 1][y + ((isTurnWhite) ? 1 : -1)] != 0 && pos[x - 1][y + ((isTurnWhite) ? 1 : -1)] > 8 == isTurnWhite) {
                                if (y != ((isTurnWhite) ? 6 : 1))
                                    validMoves.add((short)(1000*x + 100*y + 10*(x-1) + y + ((isTurnWhite) ? 1 : -1)));
                                else {
                                    validMoves.add((short)(1000*x + 100*y + 10*(x-1) + 6));
                                    validMoves.add((short)(1000*x + 100*y + 10*(x-1) + 5));
                                    validMoves.add((short)(1000*x + 100*y + 10*(x-1) + 4));
                                    validMoves.add((short)(1000*x + 100*y + 10*(x-1) + 3));
                                }
                            }
                            if ((isTurnWhite && y==4) || (!isTurnWhite && y==3) && pawn!=-1 && Math.abs(x-pawn)==1)
                                validMoves.add((short)(1000*x + 100*y + 10*pawn + 9));
                            break;
                        }
                        case 2:
                        case 3:
                        case 5:
                        case 6:
                        case 10:
                        case 11:
                        case 13:
                        case 14: {
                            int[][] d;
                            if (figure == 3 || figure == 2 || figure == 10 || figure == 11)
                                d = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                            else if (figure == 13 || figure == 5)
                                d = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
                            else
                                d = new int[][]{{1, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                            for (int[] dir :
                                    d)
                                for (int i = 1; i < 8; i++)
                                    if (inBoard(x + i * dir[0], y + i * dir[1]) && pos[x + i * dir[0]][y + i * dir[1]] == 0)
                                        validMoves.add((short)(1000*x + 100*y + 10*(x + i * dir[0]) + (y + i * dir[1])));
                                    else {
                                        if (inBoard(x + i * dir[0], y + i * dir[1]) && pos[x + i * dir[0]][y + i * dir[1]] > 8 == isTurnWhite)
                                            validMoves.add((short)(1000*x + 100*y + 10*(x + i * dir[0]) + (y + i * dir[1])));
                                        break;
                                    }
                            break;
                        }
                        case 4:
                        case 12:
                        case 7:
                        case 8:
                        case 15:
                        case 16: {
                            int[][] d;
                            if (figure == 4 || figure == 12)
                                d = new int[][]{{-1, 2}, {1, 2}, {2, -1}, {2, 1}, {-1, -2}, {1, -2}, {-2, -1}, {-2, 1}};
                            else
                                d = new int[][]{{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
                            for (int[] dir : d)
                                if (inBoard(x + dir[0], y + dir[1]) && (pos[x + dir[0]][y + dir[1]] == 0 || pos[x + dir[0]][y + dir[1]] > 8 == isTurnWhite))
                                    validMoves.add((short)(1000*x + 100*y + 10*(x + dir[0]) + (y + dir[1])));
                            if (figure == 7 && pos[0][0] == 2 && pos[1][0] == 0 && pos[2][0] == 0 && pos[3][0] == 0 && !attackedCell(isTurnWhite,3,0) && !attackedCell(isTurnWhite,4,0))
                                validMoves.add((short)(1000*x + 100*y + 10*2));
                            if (figure == 7 && pos[7][0] == 2 && pos[6][0] == 0 && pos[5][0] == 0 && !attackedCell(isTurnWhite,5,0) && !attackedCell(isTurnWhite,4,0))
                                validMoves.add((short)(1000*x + 100*y + 10*6));
                            if (figure == 15 && pos[0][7] == 10 && pos[1][7] == 0 && pos[2][7] == 0 && pos[3][7] == 0 && !attackedCell(isTurnWhite,3,7) && !attackedCell(isTurnWhite,4,7))
                                validMoves.add((short)(1000*x + 100*y + 10*2 + 7));
                            if (figure == 15 && pos[7][7] == 10 && pos[6][7] == 0 && pos[5][7] == 0 && !attackedCell(isTurnWhite,5,7) && !attackedCell(isTurnWhite,4,7))
                                validMoves.add((short)(1000*x + 100*y + 10*6 + 7));
                            break;
                        }
                    }
                }
            }
        return sortMoves(validMoves);
    }

    ArrayList<Short> sortMoves(ArrayList<Short> moves) {
        byte[][] k = new byte[moves.size()][2];

        int count = 0;
        for (short moveNum :
                moves) {
            byte move[] = numToMove(moveNum);
            if (move[3]==9){
                k[count][0] = (byte) 39900;
                k[count][1] = (byte) count;
                count++;
                continue;
            }
            if (pos[move[2]][move[3]] == 7 || pos[move[2]][move[3]] == 8 || pos[move[2]][move[3]] == 15 || pos[move[2]][move[3]] == 16) {
                ArrayList<Short> list = new ArrayList<>();
                list.add(moveToNum(move));
                return list;
            }
            k[count][0] = (byte) ((pos[move[2]][move[3]] == 0) ? 0 : 400 * Math.abs(cost(move[2], move[3])) - Math.abs(cost(move[0], move[1])));
            k[count][1] = (byte) count;
            count++;
        }
        Arrays.sort(k, Comparator.comparingInt(a -> -a[0]));

        ArrayList<Short> sort = new ArrayList<>();

        for (byte[] num : k) {
            sort.add(moves.get(num[1]));
        }

        short[] hash = AI.history.get(getKey());

        if (hash != null)
        for (int i = hash.length-1; i >= 0; i--)
            for (int j = sort.size()-1; j >= 0; j--)
                if (hash[i] == sort.get(j)) {
                    sort.add(0, sort.remove(j));
                    break;
                }

        return sort;
    }

    boolean isCheckMateTo(boolean white){
        ArrayList<Short> validMoves = getMoves(white);
        if (isCheckTo(white)) {
            for (short move :
                    validMoves) {
                Board board = new Board(pos, pawn);
                board = Game.makeMove(board, move);
                short move2 = board.getMoves(!white).get(0);
                board = Game.makeMove(board,move2);
                if (board.haveKing() == 0)
                    return false;
            }
        }
        else
            return false;
        return true;
    }

    byte[] numToMove(short number){
        byte[] result = new byte[4];
        for (int i = 3, x = number; i >= 0; i--) {
            result[i] = (byte)(x % 10);
            x /= 10;
        }
        return result;
    }

    short moveToNum(byte[] move){
        return (short) (1000*move[0]+100*move[1]+10*move[2]+move[3]);
    }

    int cost(int x, int y) {

        final int costKing = 30000;
        final int costQueen = 900;
        final int costKnight = 300;
        final int costBishop = 320;
        final int costRook = 500;
        final int costPawn = 100;

        switch (pos[x][y]) {
            case 0:
                return 0;
            case 1:
                return costPawn;
            case 2:
            case 3:
                return costRook;
            case 4:
                return costKnight;
            case 5:
                return costBishop;
            case 6:
                return costQueen;
            case 7:
            case 8:
                return costKing;
            case 9:
                return -costPawn;
            case 10:
            case 11:
                return -costRook;
            case 12:
                return -costKnight;
            case 13:
                return -costBishop;
            case 14:
                return -costQueen;
            case 15:
            case 16:
                return -costKing;
        }
        return 0;
    }

    boolean inBoard(int x, int y) {
        return (x < 8 && y < 8 && x > -1 && y > -1);
    }

    boolean attackedCell(boolean isTurnWhite, int x, int y) {
        int[][] dK = {{0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        int[][] dN = {{-1, 2}, {1, 2}, {2, -1}, {2, 1}, {-1, -2}, {1, -2}, {-2, -1}, {-2, 1}};
        int[][] dP = {{1, 1}, {-1, 1}};
        int[][] dB = {{1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
        int[][] dR = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : dK)
            if (inBoard(x + dir[0], y + dir[1]) && (pos[x + dir[0]][y + dir[1]] == ((!isTurnWhite) ? 7 : 15) || pos[x + dir[0]][y + dir[1]] == ((!isTurnWhite) ? 8 : 16)))
                return true;
        for (int[] dir : dN)
            if (inBoard(x + dir[0], y + dir[1]) && (pos[x + dir[0]][y + dir[1]] == ((!isTurnWhite) ? 4 : 12)))
                return true;
        for (int[] dir : dP)
            if (inBoard(x + dir[0], y + dir[1] * ((isTurnWhite) ? 1 : -1)) && (pos[x + dir[0]][y + dir[1] * ((isTurnWhite) ? 1 : -1)] == ((!isTurnWhite) ? 1 : 9)))
                return true;
        for (int[] dir : dR)
            for (int i = 1; i < 8; i++)
                if (inBoard(x + i * dir[0], y + i * dir[1]) && (pos[x + i * dir[0]][y + i * dir[1]] == ((!isTurnWhite) ? 2 : 10) || pos[x + i * dir[0]][y + i * dir[1]] == ((!isTurnWhite) ? 3 : 11) || pos[x + i * dir[0]][y + i * dir[1]] == ((!isTurnWhite) ? 6 : 14)))
                    return true;
                else if (!inBoard(x + i * dir[0], y + i * dir[1]) || pos[x + i * dir[0]][y + i * dir[1]] != 0)
                    break;
        for (int[] dir : dB)
            for (int i = 1; i < 8; i++)
                if (inBoard(x + i * dir[0], y + i * dir[1]) && (pos[x + i * dir[0]][y + i * dir[1]] == ((!isTurnWhite) ? 5 : 13) || pos[x + i * dir[0]][y + i * dir[1]] == ((!isTurnWhite) ? 6 : 14)))
                    return true;
                else if (!inBoard(x + i * dir[0], y + i * dir[1]) || pos[x + i * dir[0]][y + i * dir[1]] != 0)
                    break;
        return false;
    }

    boolean isCheckTo(boolean white) {
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if ((pos[x][y] == ((white) ? 7 : 15) || pos[x][y] == ((white) ? 8 : 16)))
                    return attackedCell(white, x, y);
        return false;
    }

    int haveKing() {
        boolean haveW = false, haveB = false;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (pos[x][y] == 7 || pos[x][y] == 8)
                    haveW = true;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (pos[x][y] == 15 || pos[x][y] == 16)
                    haveB = true;
        if (!haveB)
            return 30000;
        if (!haveW)
            return -30000;
        return 0;
    }

    long getKey() {
        long hash = 0L;
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                if (pos[x][y] != 0)
                    hash = hash ^ Game.zKeys[pos[x][y] - 1][8 * x + y];
        return hash;
    }
}