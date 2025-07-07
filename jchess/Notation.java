package jchess;

public class Notation {

    public static int toIndex(String notation) {
        if (notation.length() != 2) {
            throw new IllegalArgumentException("Invalid notation!");
        }

        final int file = notation.charAt(0) - 'a';
        final int rank = Character.getNumericValue(notation.charAt(1));
        return Square.toIndex(file, rank);
    }

    public static String toNotation(int index) {
        return toNotation(new Square(index));
    }

    public static String toNotation(Square square) {
        return getFileCharacter(square.getFile()) + String.valueOf(square.getRank());
    }

    public static String getFileCharacter(int file) {
        return String.valueOf((char) ('a' + file));
    }
}
