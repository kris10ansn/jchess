package jchess;

import java.awt.Point;

public class Square {

    private final int index;

    public Square(int index) {
        this.index = index;
    }

    public Square(int file, int rank) {
        this.index = Square.toIndex(file, rank);
    }

    public int getFile() {
        return index % 8;
    }

    public int getRank() {
        return index / 8;
    }

    public int getIndex() {
        return index;
    }

    public int getX(int squareSize) {
        return getFile() * squareSize;
    }

    public int getY(int squareSize) {
        return (squareSize * 7) - (getRank() * squareSize);
    }

    public Point getPoint(int squareSize) {
        return new Point(getX(squareSize) + squareSize / 2, getY(squareSize) + squareSize / 2);
    }

    public long getPositionBitBoard() {
        return Bits.oneAt(index);
    }

    public boolean isLightSquare() {
        return (getRank() + getFile()) % 2 == 1;
    }

    public static int toIndex(int file, int rank) {
        return rank * 8 + file;
    }

    @Override
    public String toString() {
        return String.format("""
            Square {
                index: %d,
                rank: %d,
                file: %d
            }
            """, index, getRank(), getFile());
    }
}
