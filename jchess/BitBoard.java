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

    public static final long WHITE_STARTING_SQUARES = RANK_1 | RANK_2;
    public static final long BLACK_STARTING_SQUARES = RANK_7 | RANK_8;

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
