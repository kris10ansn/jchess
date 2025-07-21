package jchess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import jchess.Bits;
import jchess.Board;
import jchess.Move;
import jchess.Notation;
import jchess.Piece;
import jchess.Square;

public class ChessBoardPanel extends JPanel {

    private final HashMap<Integer, BufferedImage> pieceImageMap = new HashMap<>();

    private final Color COLOR_DARK = new Color(0xFFAC825E);
    private final Color COLOR_LIGHT = new Color(0xFFDCC7A6);
    private final Color COLOR_HIGHLIGHT = new Color(20, 85, 30, 128);
    private final Color COLOR_EXTRA_DARK = new Color(0x946F51);

    private final int BOARD_SIZE = 1024;
    private final int SQUARE_SIZE = BOARD_SIZE / 8;

    private final int MOVE_CIRCLE_SIZE = SQUARE_SIZE / 4;
    private final int MOVE_HOLE_SIZE = SQUARE_SIZE / 12;

    private final int FONT_SIZE = 24;
    private final int FONT_PADDING = FONT_SIZE / 6;

    private final Point dragPosition = new Point(0, 0);

    private final Board board;

    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent event) {
            handleMousePressed(event);
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            handleMouseReleased(event);
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            handleMouseDragged(event);
        }

        @Override
        public void mouseMoved(MouseEvent event) {
            handleMouseMoved(event);
        }
    };

    private int selectedSquare = -1;
    private int hoveringSquare = -1;
    private int dragPiece = Piece.NONE;
    private long moveSquares = 0L;

    public ChessBoardPanel(Board board) {
        this.board = board;
        loadPieceImages();
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

        for (int i = 0; i < 64; i++) {
            final Square square = new Square(i);

            drawSquare(g, square);

            if (isHighlighted(i)) {
                drawHighlight(g, square);
            } else if (inMoves(i)) {
                drawMoveHighlight(g, square);
            }

            if (square.getRank() == 0) {
                final String file = Notation.getFileCharacter(square.getFile());
                drawIndicator(g, file, square, false);
            }

            if (square.getFile() == 7) {
                final String rank = String.valueOf(square.getRank() + 1);
                drawIndicator(g, rank, square, true);
            }

            // Draw piece
            final int piece = boardArray[i];

            if (piece != Piece.NONE) {
                drawPiece(g, piece, square);
            }
        }

        if (dragPiece != Piece.NONE) {
            drawPiece(g, dragPiece, dragPosition);
        }
    }

    private void handleMousePressed(MouseEvent event) {
        Square square = getSquare(event);
        final int piece = board.getSquare(square.getIndex());

        if (inMoves(square.getIndex())) {
            moveSelectedPiece(square.getIndex());
        } else if (piece == Piece.NONE) {
            clearSelection();
        } else {
            selectSquare(square.getIndex());
            startDrag(event.getX(), event.getY(), piece);
        }

        repaint();
    }

    private void handleMouseReleased(MouseEvent event) {
        releaseDrag(event.getX(), event.getY());
        repaint();
    }

    private void handleMouseDragged(MouseEvent event) {
        doDrag(event.getX(), event.getY());
        repaint();
    }

    private void handleMouseMoved(MouseEvent event) {
        Square square = getSquare(event);

        if (inMoves(square.getIndex())) {
            setHovering(square.getIndex());
        } else {
            clearHovering();
        }

        repaint();
    }

    private void drawIndicator(Graphics g, String indicator, Square square, boolean topRight) {
        Font font = g.getFont().deriveFont((float) FONT_SIZE).deriveFont(Font.BOLD);
        g.setFont(font);
        g.setColor(square.isLightSquare() ? COLOR_EXTRA_DARK : COLOR_LIGHT);

        FontMetrics fontMetrics = g.getFontMetrics();

        int x = square.getX(SQUARE_SIZE);
        int y = square.getY(SQUARE_SIZE);

        if (topRight) {
            x += SQUARE_SIZE - fontMetrics.charWidth(indicator.charAt(0)) - FONT_PADDING;
            y += FONT_SIZE + FONT_PADDING;
        } else {
            x += FONT_PADDING;
            y += SQUARE_SIZE - FONT_PADDING;
        }

        g.drawString(indicator, x, y);
    }

    private void drawMoveHighlight(Graphics g, Square square) {
        g.setColor(COLOR_HIGHLIGHT);

        final int x = square.getX(SQUARE_SIZE);
        final int y = square.getY(SQUARE_SIZE);

        if (board.getSquare(square) == Piece.NONE) {
            g.fillOval(
                    x + SQUARE_SIZE / 2 - MOVE_CIRCLE_SIZE / 2,
                    y + SQUARE_SIZE / 2 - MOVE_CIRCLE_SIZE / 2,
                    MOVE_CIRCLE_SIZE,
                    MOVE_CIRCLE_SIZE
            );
        } else {
            Rectangle2D rect = new Rectangle.Float(x, y, SQUARE_SIZE, SQUARE_SIZE);
            Ellipse2D hole = new Ellipse2D.Float(
                    x - MOVE_HOLE_SIZE, y - MOVE_HOLE_SIZE,
                    SQUARE_SIZE + MOVE_HOLE_SIZE * 2, SQUARE_SIZE + MOVE_HOLE_SIZE * 2
            );

            Area clip = new Area(rect);
            clip.subtract(new Area(hole));

            g.setClip(clip);
            g.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
            g.setClip(null);
        }
    }

    private void drawSquare(Graphics g, Square square) {
        g.setColor(square.isLightSquare() ? COLOR_LIGHT : COLOR_DARK);
        g.fillRect(square.getX(SQUARE_SIZE), square.getY(SQUARE_SIZE), SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawHighlight(Graphics g, Square square) {
        g.setColor(COLOR_HIGHLIGHT);
        g.fillRect(square.getX(SQUARE_SIZE), square.getY(SQUARE_SIZE), SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawPiece(Graphics g, int piece, Point point) {
        final BufferedImage pieceImage = pieceImageMap.get(piece);
        g.drawImage(
                pieceImage,
                point.x - SQUARE_SIZE / 2,
                point.y - SQUARE_SIZE / 2,
                SQUARE_SIZE, SQUARE_SIZE, null
        );
    }

    private void drawPiece(Graphics g, int piece, Square square) {
        drawPiece(g, piece, square.getPoint(SQUARE_SIZE));
    }

    private boolean isHighlighted(int index) {
        return isSelected(index) || isHovering(index);
    }

    private boolean inMoves(int index) {
        return Bits.getBit(moveSquares, index);
    }

    private void setHovering(int index) {
        hoveringSquare = index;
    }

    private boolean isHovering(int index) {
        return hoveringSquare == index;
    }

    private void clearHovering() {
        hoveringSquare = -1;
    }

    private void selectSquare(int index) {
        selectedSquare = index;
        moveSquares = board.generateMovesFor(selectedSquare);
    }

    private boolean isSelected(int index) {
        return selectedSquare == index;
    }

    private void clearSelection() {
        moveSquares = 0;
        selectedSquare = -1;
    }

    private void moveSelectedPiece(int toIndex) {
        board.makeMove(new Move(selectedSquare, toIndex));
        clearSelection();
        clearHovering();
    }

    private void startDrag(int x, int y, int piece) {
        dragPosition.setLocation(x, y);
        dragPiece = piece;
    }

    private void doDrag(int x, int y) {
        dragPosition.setLocation(x, y);
        Square square = getSquare(x, y);

        if (inMoves(square.getIndex())) {
            setHovering(square.getIndex());
        } else {
            clearHovering();
        }
    }

    private void releaseDrag(int x, int y) {
        Square square = getSquare(x, y);

        if (dragPiece != Piece.NONE && !isSelected(square.getIndex()) && inMoves(square.getIndex())) {
            moveSelectedPiece(square.getIndex());
        }

        dragPiece = Piece.NONE;
        hoveringSquare = -1;
    }

    private Square getSquare(int x, int y) {
        return new Square(x / SQUARE_SIZE, 7 - (y / SQUARE_SIZE));
    }

    private Square getSquare(MouseEvent mouseEvent) {
        return getSquare(mouseEvent.getX(), mouseEvent.getY());
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
