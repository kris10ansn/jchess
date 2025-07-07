package jchess;

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

    public long getPositionBitBoard() {
        return Bits.oneAt(index);
    }

    public static int toIndex(int file, int rank) {
        return rank * 8 + file;
    }
}
