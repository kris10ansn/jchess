package jchess;

public class Bits {

    public static long oneAt(int n) {
        return 1L << n;
    }

    public static boolean overlap(long l1, long l2) {
        return (l1 & l2) != 0;
    }

    public static long shift(long l, int n) {
        return n > 0 ? l << n : l >> -n;
    }

    public static boolean getBit(long l, int pos) {
        return overlap(l, oneAt(pos));
    }

    public static long putBit(long l, int pos) {
        return l | oneAt(pos);
    }

    public static long setBit(long l, int pos, boolean value) {
        return value ? putBit(l, pos) : clearBit(l, pos);
    }

    public static long clearBit(long l, int pos) {
        return l & ~oneAt(pos);
    }

    public static long clearBits(long l, int from, int to) {
        final long mask = ~(((1L << (to - from + 1)) - 1) << from);
        return l & mask;
    }

}
