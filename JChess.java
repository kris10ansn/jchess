
import jchess.Board;

public class JChess {

    public static void main(String args[]) {
        var board = new Board();
        board.loadFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        board.debugPrint();
    }
}
