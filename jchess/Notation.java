package jchess;

public class Notation {

    public static int toIndex(String notation) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Invalid notation!");
        }

        final int file = notation.charAt(0) - 'a';
        final int rank = Character.getNumericValue(notation.charAt(1));
        return toIndex(file, rank);
    }

    public static int toIndex(int file, int rank) {
        return rank * 8 + file;
    }
}
