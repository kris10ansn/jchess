package jchess;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class BoardTest {

    /**
     * Tests the singleDepthPerft method for various chess positions.
     * <br></br>
     * Based on: https://www.chessprogramming.org/Perft_Results
     *
     * This test is categorized under {@link PerftTests}.
     */
    @Test
    @Category(PerftTests.class)
    public void singleDepthPerftResults() {

        Board position1 = new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(20, singleDepthPerft(position1));

        Board position2 = new Board("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        assertEquals(48, singleDepthPerft(position2));

        Board position3 = new Board("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        assertEquals(14, singleDepthPerft(position3));

        Board position4a = new Board("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        assertEquals(6, singleDepthPerft(position4a));

        Board position4b = new Board("r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1");
        assertEquals(6, singleDepthPerft(position4b));

        Board position5 = new Board("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        assertEquals(44, singleDepthPerft(position5));

        Board position6 = new Board("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
        assertEquals(46, singleDepthPerft(position6));
    }

    private int singleDepthPerft(Board board) {
        int total = 0;

        for (int i = 0; i < 64; i++) {
            long moves = board.generateMovesFor(new Square(i));
            total += Long.bitCount(moves);
        }

        return total;
    }
}
