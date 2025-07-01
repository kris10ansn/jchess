package jchess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import jchess.Board;
import jchess.Piece;

public class ChessBoardPanel extends JPanel {

    private final HashMap<Integer, BufferedImage> pieceImageMap = new HashMap<>();

    private final Color COLOR_DARK = new Color(0xFFAC825E);
    private final Color COLOR_LIGHT = new Color(0xFFDCC7A6);
    private final Board board;

    public ChessBoardPanel(Board board) {
        this.board = board;
        loadPieceImages();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1024, 1024);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

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
                final BufferedImage pieceImage = pieceImageMap.get(piece);
                g.drawImage(pieceImage, squareX, squareY, SQUARE_SIZE, SQUARE_SIZE, this);
            }
        }
    }

    private void loadPieceImages() {
        try {
            pieceImageMap.put(Piece.WHITE | Piece.PAWN, ImageIO.read(new File("./pieces/wP.png")));
            pieceImageMap.put(Piece.WHITE | Piece.KING, ImageIO.read(new File("./pieces/wK.png")));
            pieceImageMap.put(Piece.WHITE | Piece.KNIGHT, ImageIO.read(new File("./pieces/wN.png")));
            pieceImageMap.put(Piece.WHITE | Piece.BISHOP, ImageIO.read(new File("./pieces/wB.png")));
            pieceImageMap.put(Piece.WHITE | Piece.ROOK, ImageIO.read(new File("./pieces/wR.png")));
            pieceImageMap.put(Piece.WHITE | Piece.QUEEN, ImageIO.read(new File("./pieces/wQ.png")));

            pieceImageMap.put(Piece.BLACK | Piece.PAWN, ImageIO.read(new File("./pieces/bP.png")));
            pieceImageMap.put(Piece.BLACK | Piece.KING, ImageIO.read(new File("./pieces/bK.png")));
            pieceImageMap.put(Piece.BLACK | Piece.KNIGHT, ImageIO.read(new File("./pieces/bN.png")));
            pieceImageMap.put(Piece.BLACK | Piece.BISHOP, ImageIO.read(new File("./pieces/bB.png")));
            pieceImageMap.put(Piece.BLACK | Piece.ROOK, ImageIO.read(new File("./pieces/bR.png")));
            pieceImageMap.put(Piece.BLACK | Piece.QUEEN, ImageIO.read(new File("./pieces/bQ.png")));
        } catch (IOException exception) {
            System.out.println(exception);
            System.exit(1);
        }
    }
}
