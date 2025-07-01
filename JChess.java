
import jchess.Board;

public class JChess {

    public static void main(String args[]) {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        var board = new Board();
        board.loadFen(fen);
        board.debugPrint();

        System.out.println("FEN: " + fen);
    }
}
