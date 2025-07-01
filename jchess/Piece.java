package jchess;

public class Piece {

    public static final int NONE = 0b00000;
    public static final int KING = 0b00001;
    public static final int PAWN = 0b00010;
    public static final int KNIGHT = 0b00011;
    public static final int BISHOP = 0b00100;
    public static final int ROOK = 0b00101;
    public static final int QUEEN = 0b00110;

    public static final int WHITE = 0b01000;
    public static final int BLACK = 0b10000;

    private static final int COLOR_MASK = 0b11000;

    public static boolean isColor(int piece, int color) {
        return (piece & COLOR_MASK) == color;
    }

    public static int fromFenChar(char fenChar) {

        int piece = switch (Character.toLowerCase(fenChar)) {
            case 'k' ->
                Piece.KING;
            case 'p' ->
                Piece.PAWN;
            case 'n' ->
                Piece.KNIGHT;
            case 'b' ->
                Piece.BISHOP;
            case 'r' ->
                Piece.ROOK;
            case 'q' ->
                Piece.QUEEN;
            default ->
                throw new IllegalArgumentException("Invalid fen character: '" + fenChar + "'");
        };

        boolean isWhite = Character.isUpperCase(fenChar);

        return piece | (isWhite ? Piece.WHITE : Piece.BLACK);
    }

    public static char toFenChar(int piece) {
        char c = switch (piece & 0b00111) {
            case Piece.KING ->
                'k';
            case Piece.PAWN ->
                'p';
            case Piece.KNIGHT ->
                'n';
            case Piece.BISHOP ->
                'b';
            case Piece.ROOK ->
                'r';
            case Piece.QUEEN ->
                'q';
            default ->
                throw new IllegalArgumentException("Invalid piece " + piece);
        };

        return Piece.isColor(piece, Piece.WHITE) ? Character.toUpperCase(c) : c;
    }
}
