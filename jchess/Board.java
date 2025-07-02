package jchess;

public class Board {

    private final CastlingRights castlingRights = new CastlingRights();
    private final int[] board = new int[64];

    private int activeColor = Piece.WHITE;
    private int moveCounter = 1;
    private int fiftyMoveCounter = 0;
    private String enPassantSquare = null;

    // Piece placement bitboards
    private long whitePieces = 0;
    private long blackPieces = 0;

    public void makeMove(Move move) {
        setPiece(board[move.fromSquare()], move.toSquare());
        removePiece(move.fromSquare());
    }

    public long generateMovesFor(int square) {
        final int piece = board[square];

        if (Piece.isType(piece, Piece.PAWN)) {
            boolean isWhite = Piece.isColor(piece, Piece.WHITE);
            int direction = isWhite ? -1 : 1;

            long bitboardPos = (1L << square);

            long singlePush = BitBoardHelper.shift(bitboardPos, 8 * direction);
            long doublePush = BitBoardHelper.shift(singlePush, 8 * direction);

            long startingSquares = isWhite
                    ? BitBoardHelper.WHITE_STARTING_SQUARES
                    : BitBoardHelper.BLACK_STARTING_SQUARES;

            if ((bitboardPos & startingSquares) > 0) {
                return singlePush | doublePush;
            }

            return singlePush;
        }

        return 0L;
    }

    /**
     * Loads board position from FEN notation:
     * https://en.wikipedia.org/wiki/Forsyth-Edwards_Notation
     *
     * @param fen String containing FEN notation
     */
    public void loadFen(String fen) {
        String[] segments = fen.split(" ");

        if (segments.length != 6) {
            throw new IllegalArgumentException("Invalid fen string: " + fen);
        }

        // Piece placement data
        int pos = 0;
        for (char c : segments[0].toCharArray()) {
            if (c == '/') {
                continue;
            }
            if (Character.isDigit(c)) {
                pos += Character.getNumericValue(c);
            } else {
                setPiece(Piece.fromFenChar(c), pos);
                pos++;
            }

        }

        // Active color data
        activeColor = segments[1].equals("w") ? Piece.WHITE : Piece.BLACK;

        // Castling rights data
        castlingRights.setCastlingRight(Piece.WHITE, true, segments[2].contains("K"));
        castlingRights.setCastlingRight(Piece.BLACK, true, segments[2].contains("k"));
        castlingRights.setCastlingRight(Piece.WHITE, false, segments[2].contains("Q"));
        castlingRights.setCastlingRight(Piece.BLACK, false, segments[2].contains("q"));

        // En passant square data (TODO: Implement)
        enPassantSquare = segments[3].equals("-") ? null : segments[3];

        // Halfmove data
        fiftyMoveCounter = Integer.parseInt(segments[4]);

        // Fullmove data
        moveCounter = Integer.parseInt(segments[5]);
    }

    /**
     * Convert board to FEN notation
     * https://en.wikipedia.org/wiki/Forsyth-Edwards_Notation
     *
     * @return FEN string
     */
    public String toFen() {
        String fen = "";
        int empties = 0;

        // Piece placement data
        for (int i = 0; i < board.length; i++) {
            final boolean isRankEnd = i % 8 == 0;
            final int piece = board[i];

            if (empties > 0 && (piece != Piece.NONE || isRankEnd)) {
                fen += empties;
                empties = 0;
            }

            if (i > 0 && isRankEnd) {
                fen += '/';
            }

            if (board[i] == Piece.NONE) {
                empties++;
            } else {
                fen += Piece.toFenChar(board[i]);
            }
        }

        // Active color data
        fen += " ";
        fen += activeColor == Piece.WHITE ? "w" : "b";

        // Castling rights data
        fen += " ";

        if (castlingRights.hasCastlingRight(Piece.WHITE, true)) {
            fen += 'K';
        }
        if (castlingRights.hasCastlingRight(Piece.WHITE, false)) {
            fen += 'Q';
        }
        if (castlingRights.hasCastlingRight(Piece.BLACK, true)) {
            fen += 'k';
        }
        if (castlingRights.hasCastlingRight(Piece.BLACK, false)) {
            fen += 'q';
        }

        // En passant square data
        fen += " " + (enPassantSquare != null ? enPassantSquare : "-");

        // Halfmove data
        fen += " " + fiftyMoveCounter;

        // Fullmove data
        fen += " " + moveCounter;

        return fen;
    }

    /**
     * Prints the current state of the chess board and game information to the
     * standard output.
     */
    public void debugPrint() {
        System.out.println((activeColor == Piece.WHITE ? "White" : "Black") + " to move");
        System.out.println("Move counter: " + moveCounter);
        System.out.println("Halfmove counter: " + fiftyMoveCounter);
        System.out.println("En passant square: " + enPassantSquare);

        System.out.println("White can"
                + (castlingRights.hasCastlingRight(Piece.WHITE, true) ? "" : "'t")
                + " castle kingside");
        System.out.println("White can"
                + (castlingRights.hasCastlingRight(Piece.WHITE, false) ? "" : "'t")
                + " castle queenside");
        System.out.println("Black can"
                + (castlingRights.hasCastlingRight(Piece.BLACK, true) ? "" : "'t")
                + " castle kingside");
        System.out.println("Black can"
                + (castlingRights.hasCastlingRight(Piece.BLACK, false) ? "" : "'t")
                + " castle queenside");

        for (int i = 0; i < board.length; i++) {
            if (i > 0 && i % 8 == 0) {
                System.out.println();
            }

            System.out.print(board[i]);
            System.out.print('\t');
        }

        System.out.println();

        System.out.println("FEN: " + toFen());

        System.out.println("White pieces: " + BitBoardHelper.stringify(whitePieces));
        System.out.println("Black pieces: " + BitBoardHelper.stringify(blackPieces));

    }

    public int[] getBoard() {
        return board;
    }

    public int getSquare(int index) {
        return board[index];
    }

    private void setPiece(int piece, int position) {
        if (Piece.isColor(piece, Piece.WHITE)) {
            whitePieces |= (1L << position);
        } else {
            blackPieces |= (1L << position);
        }

        board[position] = piece;
    }

    private void removePiece(int position) {
        whitePieces &= ~(1L << position);
        blackPieces &= ~(1L << position);

        board[position] = Piece.NONE;
    }
}
