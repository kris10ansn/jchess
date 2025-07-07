package jchess;

public class Square {

    private final int index;

    public Square(int index) {
        this.index = index;
    }

    public Square(int file, int rank) {
        this.index = Square.toIndex(file, rank);
    }

    public static int toIndex(int file, int rank) {
        return rank * 8 + file;
    }
}
