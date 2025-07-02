package jchess;

public class Bits {

    public static boolean overlap(long l1, long l2) {
        return (l1 & l2) != 0;
    }

    public static long shift(long l, int n) {
        return n > 0 ? l << n : l >> -n;
    }

    public static boolean getBit(long l, int pos) {
        return (l & (1L << pos)) != 0;
    }
}
