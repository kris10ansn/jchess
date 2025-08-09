package jchess;

public class MoveHelper {

    private static final long KNIGHT_MOVES = 0b01010000_10001000_00000000_10001000_01010000_00000000_00000000_00000000L;
    private static final long KING_MOVES = 0b00000000_00000000_00000000_00000000_00000000_00000111_00000101_00000111L;

    public static long getShiftedKnightMovesMask(int index) {
        Square square = new Square(index);

        long masked = KNIGHT_MOVES;

        if (square.getRank() >= 7) {
            masked &= ~BitBoard.RANK_1;
        }
        if (square.getRank() >= 6) {
            masked &= ~BitBoard.RANK_2;
        }
        if (square.getFile() >= 7) {
            masked &= ~BitBoard.FILE_7;
        }
        if (square.getFile() >= 6) {
            masked &= ~BitBoard.FILE_8;
        }
        if (square.getFile() <= 1) {
            masked &= ~BitBoard.FILE_4;
        }
        if (square.getFile() <= 0) {
            masked &= ~BitBoard.FILE_5;
        }

        return Bits.shift(masked, index - new Square(5, 5).getIndex());
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
}
