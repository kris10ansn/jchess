
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jchess.Board;
import jchess.Piece;

class ChessBoardPanel extends JPanel {

    private final Color COLOR_DARK = new Color(Integer.parseInt("AC825E", 16));
    private final Color COLOR_LIGHT = new Color(Integer.parseInt("DCC7A6", 16));
    private final Board board;

    public ChessBoardPanel(Board board) {
        this.board = board;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1024, 1024);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(COLOR_LIGHT);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        final int[] boardArray = board.getBoard();
        final int SQUARE_SIZE = this.getWidth() / board.BOARD_SIZE;

        int rank = 0;
        for (int i = 0; i < boardArray.length; i++) {
            if (i > 0 && i % 8 == 0) {
                rank++;
            }

            final int squareX = i % 8 * SQUARE_SIZE;
            final int squareY = rank * SQUARE_SIZE;

            g.setColor((i + rank) % 2 == 0 ? COLOR_LIGHT : COLOR_DARK);
            g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);

            final int piece = boardArray[i];

            if (piece != Piece.NONE) {
                final int padding = 32;

                g.setColor(Piece.isColor(piece, Piece.WHITE) ? Color.WHITE : Color.BLACK);
                g.fillRect(squareX + padding, squareY + padding, SQUARE_SIZE - padding * 2, SQUARE_SIZE - padding * 2);
            }
        }
    }
}

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

        frame.setVisible(true);
    }
}
