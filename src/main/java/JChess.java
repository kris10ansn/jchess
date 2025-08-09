
import java.awt.BorderLayout;
import java.awt.Color;

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

        System.out.println("Provided FEN: " + fen);

        SwingUtilities.invokeLater(() -> createWindow());
    }

    private static void createWindow() {
        JFrame frame = new JFrame("JChess");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.GRAY);

        frame.add(new ChessBoardPanel(board), BorderLayout.WEST);
        frame.pack();

        // Center window on screen
        frame.setLocationRelativeTo(null);

        frame.setResizable(false);
        frame.setVisible(true);
    }
}
