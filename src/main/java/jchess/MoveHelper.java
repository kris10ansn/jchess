package jchess;

public class MoveHelper {

    private static final long KNIGHT_MOVES = 0b01010000_10001000_00000000_10001000_01010000_00000000_00000000_00000000L;
    private static final long KING_MOVES = 0b00000000_00000000_00000000_00000000_00000000_00000111_00000101_00000111L;

    public static final long WHITE_SHORT_CASTLE_PATH = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_01100000L;
    public static final long WHITE_LONG_CASTLE_PATH = 0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00001110L;

    public static final long BLACK_SHORT_CASTLE_PATH = 0b01100000_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;
    public static final long BLACK_LONG_CASTLE_PATH = 0b00001110_00000000_00000000_00000000_00000000_00000000_00000000_00000000L;

    public static long getShiftedKnightMovesMask(int index) {
        Square square = new Square(index);

        long mask = KNIGHT_MOVES;

        if (square.getRank() >= 7) {
            mask &= ~BitBoard.RANK_1;
        }
        if (square.getRank() >= 6) {
            mask &= ~BitBoard.RANK_2;
        }
        if (square.getFile() >= 7) {
            mask &= ~BitBoard.FILE_7;
        }
        if (square.getFile() >= 6) {
            mask &= ~BitBoard.FILE_8;
        }
        if (square.getFile() <= 1) {
            mask &= ~BitBoard.FILE_4;
        }
        if (square.getFile() <= 0) {
            mask &= ~BitBoard.FILE_5;
        }

        return Bits.shift(mask, index - new Square(5, 5).getIndex());
    }

    public static long getShiftedKingMovesMask(int index) {
        Square square = new Square(index);
        long mask = KING_MOVES;

        if (square.getFile() == 0) {
            mask &= ~BitBoard.FILE_1;
        }
        if (square.getRank() == 0) {
            mask &= ~BitBoard.RANK_1;
        }
        if (square.getRank() == 7) {
            mask &= ~BitBoard.RANK_3;
        }
        if (square.getFile() == 7) {
            mask &= ~BitBoard.FILE_3;
        }

        return Bits.shift(mask, index - new Square(1, 1).getIndex());
    }

    public static long getShortCastlePath(int piece) {
        return Piece.isWhite(piece) ? WHITE_SHORT_CASTLE_PATH : BLACK_SHORT_CASTLE_PATH;
    }

    public static long getLongCastlePath(int piece) {
        return Piece.isWhite(piece) ? WHITE_LONG_CASTLE_PATH : BLACK_LONG_CASTLE_PATH;
    }

}
