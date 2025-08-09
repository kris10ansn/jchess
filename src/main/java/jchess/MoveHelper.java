package jchess;

public class MoveHelper {

    private static final long KNIGHT_MOVES = 0b01010000_10001000_00000000_10001000_01010000_00000000_00000000_00000000L;

    public static long getKnightMovesMaskedAndShifted(int position) {
        final int file = position % 8;
        final int rank = position / 8;

        long masked = KNIGHT_MOVES;

        if (rank >= 7) {
            masked &= ~BitBoard.RANK_1;
        }
        if (rank >= 6) {
            masked &= ~BitBoard.RANK_2;
        }
        if (file >= 7) {
            masked &= ~BitBoard.FILE_7;
        }
        if (file >= 6) {
            masked &= ~BitBoard.FILE_8;
        }
        if (file <= 1) {
            masked &= ~BitBoard.FILE_4;
        }
        if (file <= 0) {
            masked &= ~BitBoard.FILE_5;
        }

        return Bits.shift(masked, position - 45);
    }
}
