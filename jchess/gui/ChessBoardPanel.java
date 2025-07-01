package jchess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private final Color COLOR_HIGHLIGHT = new Color(20, 85, 30, 128);

    private final int BOARD_SIZE = 1024;
    private final int SQUARE_SIZE = BOARD_SIZE / 8;

    private int selectedSquare = -1;

    private int dragPiece = Piece.NONE;
    private Point dragPosition = new Point(0, 0);

    private final Board board;

    public ChessBoardPanel(Board board) {
        this.board = board;
        loadPieceImages();

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int rank = event.getY() / SQUARE_SIZE;
                int file = event.getX() / SQUARE_SIZE;

                selectedSquare = rank * 8 + file;
                int piece = board.getSquare(selectedSquare);

                dragPosition.setLocation(event.getX(), event.getY());
                dragPiece = piece;

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (dragPiece == Piece.NONE) {
                    return;
                }

                int rank = event.getY() / SQUARE_SIZE;
                int file = event.getX() / SQUARE_SIZE;

                int toIndex = rank * 8 + file;

                if (selectedSquare != toIndex) {
                    board.makeMove(selectedSquare, toIndex);
                }

                dragPiece = Piece.NONE;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                dragPosition.setLocation(event.getX(), event.getY());
                repaint();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_SIZE, BOARD_SIZE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        final int[] boardArray = board.getBoard();

        final int fontSize = 24;
        g.setFont(g.getFont().deriveFont((float) fontSize));

        int rank = 0;
        for (int i = 0; i < boardArray.length; i++) {
            if (i > 0 && i % 8 == 0) {
                rank++;
            }

            final int file = i % 8;

            final int squareX = file * SQUARE_SIZE;
            final int squareY = rank * SQUARE_SIZE;

            final boolean isLightSquare = (i + rank) % 2 == 0;

            g.setColor(isLightSquare ? COLOR_LIGHT : COLOR_DARK);
            g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);

            if (i == selectedSquare) {
                g.setColor(COLOR_HIGHLIGHT);
                g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);
            }

            // Draw file characters on the lowest rank
            if (rank == 7) {
                // Get character by adding the file index to 'a' (0='a', 1='b', ...)
                final char fileCharacter = (char) ('a' + file);
                g.setColor(isLightSquare ? COLOR_DARK : COLOR_LIGHT);
                g.drawString(
                        String.valueOf(fileCharacter),
                        squareX + fontSize / 8,
                        squareY + SQUARE_SIZE - fontSize / 8
                );
            }

            if (file == 7) {
                g.setColor(isLightSquare ? COLOR_DARK : COLOR_LIGHT);
                g.drawString(
                        String.valueOf(8 - rank),
                        squareX + SQUARE_SIZE - fontSize / 2 - fontSize / 8,
                        squareY + fontSize + fontSize / 8
                );
            }

            final int piece = boardArray[i];

            if (piece != Piece.NONE) {
                final BufferedImage pieceImage = pieceImageMap.get(piece);
                g.drawImage(pieceImage, squareX, squareY, SQUARE_SIZE, SQUARE_SIZE, this);
            }
        }

        if (dragPiece != Piece.NONE) {
            final BufferedImage pieceImage = pieceImageMap.get(dragPiece);
            g.drawImage(pieceImage, dragPosition.x - SQUARE_SIZE / 2, dragPosition.y - SQUARE_SIZE / 2, SQUARE_SIZE, SQUARE_SIZE, this);
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
