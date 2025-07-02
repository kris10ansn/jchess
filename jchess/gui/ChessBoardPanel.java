package jchess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import jchess.BitBoard;
import jchess.Bits;
import jchess.Board;
import jchess.Move;
import jchess.Piece;

public class ChessBoardPanel extends JPanel {

    private final HashMap<Integer, BufferedImage> pieceImageMap = new HashMap<>();

    private final Color COLOR_DARK = new Color(0xFFAC825E);
    private final Color COLOR_LIGHT = new Color(0xFFDCC7A6);
    private final Color COLOR_HIGHLIGHT = new Color(20, 85, 30, 64);
    private final Color COLOR_HIGHLIGHT_DARK = new Color(20, 85, 30, 128);

    private final int BOARD_SIZE = 1024;
    private final int SQUARE_SIZE = BOARD_SIZE / 8;

    private int selectedSquare = -1;
    private int hoveringSquare = -1;

    private int dragPiece = Piece.NONE;
    private Point dragPosition = new Point(0, 0);

    private long moveSquares = 0L;

    private final Board board;

    public ChessBoardPanel(Board board) {
        this.board = board;
        loadPieceImages();

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                final int rank = event.getY() / SQUARE_SIZE;
                final int file = event.getX() / SQUARE_SIZE;

                final int index = rank * 8 + file;

                if (board.getSquare(index) == Piece.NONE) {
                    return;
                }

                selectedSquare = index;
                int piece = board.getSquare(selectedSquare);

                dragPosition.setLocation(event.getX(), event.getY());
                dragPiece = piece;

                moveSquares = board.generateMovesFor(selectedSquare);

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
                    board.makeMove(new Move(selectedSquare, toIndex));
                    selectedSquare = -1;
                    moveSquares = 0L;
                }

                dragPiece = Piece.NONE;
                hoveringSquare = -1;
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (dragPiece == Piece.NONE) {
                    return;
                }

                dragPosition.setLocation(event.getX(), event.getY());

                int rank = event.getY() / SQUARE_SIZE;
                int file = event.getX() / SQUARE_SIZE;
                int index = rank * 8 + file;

                hoveringSquare
                        = Bits.overlap(
                                BitBoard.createPositionBoard(index),
                                moveSquares
                        )
                        ? index : -1;

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

        for (int i = 0; i < 64; i++) {
            final int rank = i / 8;
            final int file = i % 8;

            final int squareX = file * SQUARE_SIZE;
            final int squareY = rank * SQUARE_SIZE;

            final boolean isLightSquare = (i + rank) % 2 == 0;

            g.setColor(isLightSquare ? COLOR_LIGHT : COLOR_DARK);
            g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);

            if (i == selectedSquare || i == hoveringSquare) {
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

            // Draw rank numbers on right-most file
            if (file == 7) {
                g.setColor(isLightSquare ? COLOR_DARK : COLOR_LIGHT);
                g.drawString(
                        String.valueOf(8 - rank),
                        squareX + SQUARE_SIZE - fontSize / 2 - fontSize / 8,
                        squareY + fontSize + fontSize / 8
                );
            }

            if (Bits.getBit(moveSquares, i)) {
                g.setColor(COLOR_HIGHLIGHT_DARK);

                if (board.getSquare(i) == Piece.NONE) {
                    g.fillOval(
                            squareX + SQUARE_SIZE / 2 - SQUARE_SIZE / 8,
                            squareY + SQUARE_SIZE / 2 - SQUARE_SIZE / 8,
                            SQUARE_SIZE / 4,
                            SQUARE_SIZE / 4
                    );
                } else {
                    final int holePadding = 10;

                    var square = new Rectangle.Float(
                            squareX, squareY, SQUARE_SIZE, SQUARE_SIZE
                    );
                    var hole = new Ellipse2D.Float(
                            squareX - holePadding, squareY - holePadding,
                            SQUARE_SIZE + holePadding * 2, SQUARE_SIZE + holePadding * 2
                    );

                    var clip = new Area(square);
                    clip.subtract(new Area(hole));

                    g.setClip(clip);
                    g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);
                    g.setClip(null);
                }
            }

            // Draw piece
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
            pieceImageMap.put(Piece.create(Piece.PAWN, Piece.WHITE), ImageIO.read(new File("./pieces/wP.png")));
            pieceImageMap.put(Piece.create(Piece.KING, Piece.WHITE), ImageIO.read(new File("./pieces/wK.png")));
            pieceImageMap.put(Piece.create(Piece.KNIGHT, Piece.WHITE), ImageIO.read(new File("./pieces/wN.png")));
            pieceImageMap.put(Piece.create(Piece.BISHOP, Piece.WHITE), ImageIO.read(new File("./pieces/wB.png")));
            pieceImageMap.put(Piece.create(Piece.ROOK, Piece.WHITE), ImageIO.read(new File("./pieces/wR.png")));
            pieceImageMap.put(Piece.create(Piece.QUEEN, Piece.WHITE), ImageIO.read(new File("./pieces/wQ.png")));

            pieceImageMap.put(Piece.create(Piece.PAWN, Piece.BLACK), ImageIO.read(new File("./pieces/bP.png")));
            pieceImageMap.put(Piece.create(Piece.KING, Piece.BLACK), ImageIO.read(new File("./pieces/bK.png")));
            pieceImageMap.put(Piece.create(Piece.KNIGHT, Piece.BLACK), ImageIO.read(new File("./pieces/bN.png")));
            pieceImageMap.put(Piece.create(Piece.BISHOP, Piece.BLACK), ImageIO.read(new File("./pieces/bB.png")));
            pieceImageMap.put(Piece.create(Piece.ROOK, Piece.BLACK), ImageIO.read(new File("./pieces/bR.png")));
            pieceImageMap.put(Piece.create(Piece.QUEEN, Piece.BLACK), ImageIO.read(new File("./pieces/bQ.png")));
        } catch (IOException exception) {
            System.out.println(exception);
            System.exit(1);
        }
    }
}
