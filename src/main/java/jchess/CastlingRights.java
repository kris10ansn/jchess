package jchess;

public class CastlingRights {

    private int castlingRights = 0b1111;

    private final int WHITE_MASK = 0b1100;
    private final int BLACK_MASK = 0b0011;
    private final int KINGS_MASK = 0b0101;
    private final int QUEEN_MASK = 0b1010;

    private int getMask(int color, boolean kingside) {
        return (Piece.isWhite(color) ? WHITE_MASK : BLACK_MASK) & (kingside ? KINGS_MASK : QUEEN_MASK);
    }

    public void setCastlingRight(int color, boolean kingside, boolean canCastle) {
        final int mask = getMask(color, kingside);

        if (canCastle) {
            castlingRights = castlingRights | mask;
        } else {
            castlingRights = castlingRights & ~mask;
        }
    }

    public void removeCastlingRight(int color, boolean kingside) {
        setCastlingRight(color, kingside, false);
    }

    public void removeKingsideCastlingRight(int color) {
        removeCastlingRight(color, true);
    }

    public void removeQueensideCastlingRight(int color) {
        removeCastlingRight(color, false);
    }

    public boolean hasCastlingRight(int color, boolean kingside) {
        final int mask = getMask(color, kingside);
        return Bits.overlap(castlingRights, mask);
    }

    public boolean hasKingsideCastlingRight(int color) {
        return hasCastlingRight(color, true);
    }

    public boolean hasQueensideCastlingRight(int color) {
        return hasCastlingRight(color, false);
    }

    public String toFenString() {
        String result = "";

        if (hasCastlingRight(Piece.WHITE, true)) {
            result += 'K';
        }
        if (hasCastlingRight(Piece.WHITE, false)) {
            result += 'Q';
        }
        if (hasCastlingRight(Piece.BLACK, true)) {
            result += 'k';
        }
        if (hasCastlingRight(Piece.BLACK, false)) {
            result += 'q';
        }

        return result;
    }
}
