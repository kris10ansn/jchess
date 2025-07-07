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

    public static String toNotation(int index) {
        final int file = index % 8;
        final int rank = index / 8;
        
        return ('a' + file) + String.valueOf(rank);
    }
}
