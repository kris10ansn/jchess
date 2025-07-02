package jchess;

public class BitBoard {

    public static final long RANK_8 = 0b11111111;
    public static final long RANK_7 = RANK_8 << 8;
    public static final long RANK_6 = RANK_7 << 8;
    public static final long RANK_5 = RANK_6 << 8;
    public static final long RANK_4 = RANK_5 << 8;
    public static final long RANK_3 = RANK_4 << 8;
    public static final long RANK_2 = RANK_3 << 8;
    public static final long RANK_1 = RANK_2 << 8;

    public static final long FILE_1 = 0x0101010101010101L;
    public static final long FILE_2 = FILE_1 << 1L;
    public static final long FILE_3 = FILE_2 << 1L;
    public static final long FILE_4 = FILE_3 << 1L;
    public static final long FILE_5 = FILE_4 << 1L;
    public static final long FILE_6 = FILE_5 << 1L;
    public static final long FILE_7 = FILE_6 << 1L;
    public static final long FILE_8 = FILE_7 << 1L;

    public static final long WHITE_STARTING_SQUARES = RANK_1 | RANK_2;
    public static final long BLACK_STARTING_SQUARES = RANK_7 | RANK_8;

    public static final long KNIGHT_MOVES = 0b01010000_10001000_00000000_10001000_01010000_00000000_00000000_00000000L;

    public static long createPositionBoard(int pos) {
        return 1L << pos;
    }

    public static String stringify(long l) {
        return String.format("%64s", Long.toBinaryString(l)).replace(' ', '0');
    }

    public static String tablify(long l) {
        StringBuilder result = new StringBuilder(stringify(l));

        for (int i = 8; i < result.length(); i += 9) {
            result.insert(i, '\n');
        }

        return result.toString();
    }
}
