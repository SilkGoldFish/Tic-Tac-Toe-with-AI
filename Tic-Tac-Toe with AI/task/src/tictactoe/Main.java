package tictactoe;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.menu();
    }
}

class Game {
    private Player player1; //first to move, always plays with 'X'
    private Player player2; //second to move, always plays with 'O'
    private Board board;
    private boolean end;
    boolean isEnd() { return this.end; }

    void menu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Input command: ");
        String input = scanner.nextLine();
        String[] parameters = input.trim().split(" ");
        while (!parameters[0].equals("exit")) {
            if (parameters.length < 3) {
                System.out.println("Bad parameters!");
            } else {
                init(parameters[1], parameters[2]);
                start();
            }
            System.out.print("Input command: ");
            input = scanner.nextLine();
            parameters = input.trim().split(" ");
        }
    }

    Player setPlayer(String parameter, char chessPiece) {
        return switch (parameter) {
            case "user" -> new Human(board, chessPiece);
            case "easy" -> new EasyAI(board, chessPiece);
            case "medium" -> new MediumAI(board, chessPiece);
            case "hard" -> new HardAI(board, chessPiece);
            default -> new Player(board, chessPiece);
        };
    }
    void init(String parameters1, String parameters2) {
        Board board = new Board();
        this.board = board;
        board.print();
        player1 = setPlayer(parameters1, 'X');
        player2 = setPlayer(parameters2, 'O');
        this.end = false;
    }

    void start() {
        while (!isEnd()) {
            player1.move();
            player2.move();
            judge();
        }
    }

    void judge() {
        //row
        for (int i = 0; i < 3; i++) {
            if (!board.isEmptyAtIndex(i, 0) &&
                    board.getAtIndex(i, 0) == board.getAtIndex(i, 1) &&
                    board.getAtIndex(i, 0) == board.getAtIndex(i, 2)) {
                System.out.println(board.getAtIndex(i, 0) + " wins");
                end = true;
                return;
            }
        }
        //col
        for (int i = 0; i < 3; i++) {
            if (!board.isEmptyAtIndex(0, i) &&
                    board.getAtIndex(0, i) == board.getAtIndex(1, i) &&
                    board.getAtIndex(0, i) == board.getAtIndex(2, i)) {
                System.out.println(board.getAtIndex(0, i) + " wins");
                end = true;
                return;
            }
        }
        //diagonal
        if (!board.isEmptyAtIndex(1, 1) &&
                ((board.getAtIndex(1, 1) == board.getAtIndex(0, 0) &&
                        board.getAtIndex(1, 1) == board.getAtIndex(2, 2))||
                        (board.getAtIndex(1, 1) == board.getAtIndex(0, 2) &&
                                board.getAtIndex(1, 1) == board.getAtIndex(2, 0)))) {
            System.out.println(board.getAtIndex(1, 1) + " wins");
            end = true;
            return;
        }
        //full
        if (board.isFull()) {
            System.out.println("Draw");
            end = true;
            return;
        }
    }
}

interface Play {
    void move();
}
class Player implements Play{
    protected Board board;
    protected char chessPiece;
    Player(Board board, char chessPiece) {
        this.board = board;
        this.chessPiece = chessPiece;
    }
    @Override
    public void move() {

    }
}

class Board {
    private final char[][] chessBoard;
    private int pieceNumber;

    Board() {
        this.chessBoard = new char[][] {{' ', ' ', ' '},{' ', ' ', ' '},{' ', ' ', ' '}};
        this.pieceNumber = 0;
    }

    void print() {
        System.out.println("---------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(chessBoard[i][j] + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    void setAtIndex(int x, int y, char ch) {
        chessBoard[x][y] = ch;
        pieceNumber++;
    }

    void setEmptyAtIndex(int x, int y) {
        chessBoard[x][y] = ' ';
        pieceNumber--;
    }

    char getAtIndex(int x, int y) {
        return chessBoard[x][y];
    }

    boolean isEmptyAtIndex(int x, int y) {
        return chessBoard[x][y] == ' ';
    }

    boolean isFull() {
        return pieceNumber == 9;
    }
}

class Human extends Player {
    Human(Board board, char chessPiece) {
        super(board, chessPiece);
    }
    @Override
    public void move() {
        if (board.isFull()) {
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the coordinates: ");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        board.setAtIndex(x - 1, y - 1, chessPiece);
        board.print();
    }
}

class AI extends Player {
    AI(Board board, char chessPiece) {
        super(board, chessPiece);
    }
    @Override
    public void move() {
        if (board.isFull()) {
            return;
        }
        Random random = new Random();
        int next = random.nextInt(9);
        int x = next / 3;
        int y = next - (next / 3) * 3;
        while (!board.isEmptyAtIndex(x, y)) {
            next = random.nextInt(9);
            x = next / 3;
            y = next - next / 3 * 3;
        }
        board.setAtIndex(x, y, chessPiece);
        board.print();
    }
}

class EasyAI extends AI {
    EasyAI(Board board, char chessPiece) {
        super(board, chessPiece);
    }

    @Override
    public void move() {
        System.out.println("Making move level \"easy\"");
        super.move();
    }
}

class MediumAI extends AI {
    MediumAI(Board board, char chessPiece) {
        super(board, chessPiece);
    }

    boolean moveRowOrCol(char chessPiece, int mode) {
        // row: 0 col: 1
        for (int i = 0; i < 3; i++){
            int count = 0;
            int index = -1;
            for (int j = 0; j < 3; j++) {
                if (mode == 0) {
                    if (board.getAtIndex(i, j) == chessPiece) {
                        count++;
                    } else if (board.isEmptyAtIndex(i, j)) {
                        index = j;
                    }
                } else {
                    if (board.getAtIndex(j, i) == chessPiece) {
                        count++;
                    } else if (board.isEmptyAtIndex(j, i)) {
                        index = j;
                    }
                }
            }
            if (count == 2 && index != -1) {
                if (mode == 0) {
                    board.setAtIndex(i, index, this.chessPiece);
                } else {
                    board.setAtIndex(index, i, this.chessPiece);
                }
                return true;
            }
        }
        return false;
    }

    boolean moveWithRule(char chessPiece) {
        if (moveRowOrCol(chessPiece, 0) || moveRowOrCol(chessPiece, 1)){
            return false;
        }
        //diagnose
        int count = 0;
        int index = -1;
        for (int i = 0, j = 0; i < 3; i++, j++) {
            if (board.getAtIndex(i, j) == chessPiece) {
                count++;
            } else if (board.isEmptyAtIndex(i, j)) {
                index = i;
            }
        }
        if (count == 2 && index != -1) {
            board.setAtIndex(index, index, this.chessPiece);
            return false;
        }
        count = 0;
        index = -1;
        for (int i = 0, j = 2; i < 3; i++, j--) {
            if (board.getAtIndex(i, j) == chessPiece) {
                count++;
            } else if (board.isEmptyAtIndex(i, j)) {
                index = i;
            }
        }
        if (count == 2 && index != -1) {
            board.setAtIndex(index, 2 - index, this.chessPiece);
            return false;
        }
        return true;
    }
    @Override
    public void move() {
        if (board.isFull()) {
            return;
        }
        System.out.println("Making move level \"medium\"");
        //first offense then defence
        if (moveWithRule(chessPiece) && moveWithRule(chessPiece == 'X' ? 'O' : 'X')) {
            //random
            super.move();
        } else {
            board.print();
        }
    }
}

class Move {
    int score;
    int index;

    Move(int index, int score) {
        this.index = index;
        this.score = score;
    }

}
class HardAI extends AI {
    HardAI(Board board, char chessPiece) {
        super(board, chessPiece);
    }

    ArrayList<Integer> emptyIndexies() {
        ArrayList<Integer> availSpots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.isEmptyAtIndex(i, j)) {
                    availSpots.add(i * 3 + j);
                }
            }
        }
        return availSpots;
    }

    boolean winning(char chessPiece) {
        //row
        for (int i = 0; i < 3; i++) {
            if (board.getAtIndex(i, 0) == chessPiece &&
                    board.getAtIndex(i, 0) == board.getAtIndex(i, 1) &&
                    board.getAtIndex(i, 0) == board.getAtIndex(i, 2)) {
                return true;
            }
        }
        //col
        for (int i = 0; i < 3; i++) {
            if (board.getAtIndex(0, i) == chessPiece &&
                    board.getAtIndex(0, i) == board.getAtIndex(1, i) &&
                    board.getAtIndex(0, i) == board.getAtIndex(2, i)) {
                return true;
            }
        }
        //diagonal
        return board.getAtIndex(1, 1) == chessPiece &&
                ((board.getAtIndex(1, 1) == board.getAtIndex(0, 0) &&
                        board.getAtIndex(1, 1) == board.getAtIndex(2, 2)) ||
                        (board.getAtIndex(1, 1) == board.getAtIndex(0, 2) &&
                                board.getAtIndex(1, 1) == board.getAtIndex(2, 0)));
    }

    Move minimax(char chessPiece) {
        //https://www.freecodecamp.org/news/how-to-make-your-tic-tac-toe-game-unbeatable-by-using-the-minimax-algorithm-9d690bad4b37/
        ArrayList<Integer> availSpots = emptyIndexies();
        if (winning(this.chessPiece == 'X'? 'O': 'X')) { //enemy
            return new Move(-1, -10);
        } else if (winning(this.chessPiece)) {
            return new Move(-1, 10);
        } else if (emptyIndexies().isEmpty()) {
            return new Move(-1, 0);
        }
        ArrayList<Move> moves = new ArrayList<>();
        for (int index : availSpots) {
            Move move = new Move(index, 0);
            board.setAtIndex(index / 3, index - index / 3 * 3, chessPiece);
            move.score = minimax(chessPiece == 'X'? 'O' : 'X').score;
            board.setEmptyAtIndex(index / 3, index - index / 3 * 3);
            moves.add(move);
        }
        int bestMove = -1;
        if (chessPiece == this.chessPiece) {
            int bestScore = -10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score > bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        } else {
            int bestScore = 10000;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).score < bestScore) {
                    bestScore = moves.get(i).score;
                    bestMove = i;
                }
            }
        }
        return moves.get(bestMove);
    }
    @Override
    public void move() {
        if (board.isFull()) {
            return;
        }
        System.out.println("Making move level \"hard\"");
        //minimax
        int index = minimax(chessPiece).index;
        board.setAtIndex(index / 3, index - index / 3 * 3, chessPiece);
        board.print();
    }
}

