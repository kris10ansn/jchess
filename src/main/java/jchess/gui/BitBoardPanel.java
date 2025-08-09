package jchess.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import jchess.Bits;
import jchess.Square;

public class BitBoardPanel extends JPanel {

    private static final Color COLOR_ON = new Color(0xA0A0FF);
    private static final Color COLOR_OFF = Color.DARK_GRAY;

    private final int size;
    private final int squareSize;
    private long bitboard;

    public BitBoardPanel(long bitboard, int size) {
        this.bitboard = bitboard;
        this.size = size;
        this.squareSize = size / 8;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(size, size);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < 64; i++) {
            boolean value = Bits.getBit(bitboard, i);

            drawSquare(g, new Square(i), value);
        }
    }

    private void drawSquare(Graphics g, Square square, boolean on) {
        g.setColor(on ? COLOR_ON : COLOR_OFF);
        g.fillRect(square.getX(squareSize), square.getY(squareSize), squareSize, squareSize);
    }

    public void setBitboard(long bitboard) {
        this.bitboard = bitboard;
    }
}
