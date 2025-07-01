
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jchess.Board;
import jchess.gui.ChessBoardPanel;

public class JChess {

    private static Board board;

    public static void main(String args[]) {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        board = new Board();
        board.loadFen(fen);
        board.debugPrint();

        System.out.println("FEN: " + fen);

        SwingUtilities.invokeLater(() -> createWindow());
    }

    private static void createWindow() {
        JFrame frame = new JFrame("JChess");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 1024);

        var p = new ChessBoardPanel(board);
        frame.add(p);
        frame.pack();

        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Dimension frameSize = frame.getPreferredSize();

        frame.setLocation(
                (int) (bounds.width / 2 - frameSize.width / 2),
                (int) (bounds.height / 2 - frameSize.height / 2)
        );

        frame.setResizable(false);
        frame.setVisible(true);
    }
}
