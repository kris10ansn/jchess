package jchess;

public class Board {

    private final CastlingRights castlingRights = new CastlingRights();
    private final int[] board = new int[64];

    private int activeColor = Piece.WHITE;
    private int moveCounter = 1;
    private int fiftyMoveCounter = 0;
    private int enPassantSquare = -1;

    private long whitePieces = 0L;
    private long blackPieces = 0L;

    public void makeMove(Move move) {
        if (!isLegalMove(move)) {
            System.out.println("Illegal move!");
            return;
        }

        if (isCastlingMove(move)) {
            handleCastlingMove(move);
        }

        updateCastlingRights(move);

        movePiece(move.fromSquare(), move.toSquare());
    }

    public boolean isLegalMove(Move move) {
        return Bits.overlap(
                generateMovesFor(move.fromSquare()),
                move.toSquare().getPositionBitBoard()
        );
    }

    public boolean isCastlingMove(Move move) {
        return Piece.isType(getPiece(move.fromSquare()), Piece.KING)
                && move.getRankDelta() == 0
                && Math.abs(move.getFileDelta()) > 1;
    }

    public void handleCastlingMove(Move move) {
        final int piece = getPiece(move.fromSquare());
        final int backrank = getBackrankIndex(piece);

        boolean isKingOnBackRank = Piece.isType(piece, Piece.KING)
                && move.fromSquare().getRank() == backrank;

        if (isKingOnBackRank && move.getFileDelta() == 2) {
            movePiece(getKingRookStartingSquare(piece), new Square(5, backrank));
        }

        if (isKingOnBackRank && move.getFileDelta() == -2) {
            movePiece(getQueenRookStartingSquare(piece), new Square(3, backrank));
        }
    }

    private void updateCastlingRights(Move move) {
        final int piece = getPiece(move.fromSquare());
        final Square kRookStartingSquare = getKingRookStartingSquare(piece);
        final Square qRookStartingSquare = getQueenRookStartingSquare(piece);

        if (Piece.isType(piece, Piece.KING)) {
            castlingRights.removeKingsideCastlingRight(piece);
            castlingRights.removeQueensideCastlingRight(piece);
        }

        if (move.fromSquare().equals(kRookStartingSquare) || move.toSquare().equals(kRookStartingSquare)) {
            castlingRights.removeKingsideCastlingRight(piece);
        }

        if (move.fromSquare().equals(qRookStartingSquare) || move.toSquare().equals(qRookStartingSquare)) {
            castlingRights.removeQueensideCastlingRight(piece);
        }
    }

    public long generateMovesFor(Square square) {
        final int piece = getPiece(square);

        final long ownPieces = Piece.isWhite(piece) ? whitePieces : blackPieces;

        if (Piece.isType(piece, Piece.PAWN)) {
            return generatePawnMoves(square);
        }

        if (Piece.isType(piece, Piece.KNIGHT)) {
            return MoveHelper.getShiftedKnightMovesMask(square.getIndex()) & ~ownPieces;
        }

        if (Piece.isType(piece, Piece.ROOK)) {
            return generateRookMoves(square) & ~ownPieces;
        }

        if (Piece.isType(piece, Piece.BISHOP)) {
            return generateBishopMoves(square) & ~ownPieces;
        }

        if (Piece.isType(piece, Piece.QUEEN)) {
            return (generateRookMoves(square) | generateBishopMoves(square))
                    & ~ownPieces;
        }

        if (Piece.isType(piece, Piece.KING)) {
            long moves = MoveHelper.getShiftedKingMovesMask(square.getIndex());

            boolean canCastleShort = castlingRights.hasCastlingRight(piece, true);
            boolean canCastleLong = castlingRights.hasCastlingRight(piece, false);

            long shortCastlePath = MoveHelper.getShortCastlePath(piece);
            long longCastlePath = MoveHelper.getLongCastlePath(piece);

            if (canCastleShort && !Bits.overlap(getAllPieces(), shortCastlePath)) {
                moves |= new Square(6, square.getRank()).getPositionBitBoard();
            }

            if (canCastleLong && !Bits.overlap(getAllPieces(), longCastlePath)) {
                moves |= new Square(2, square.getRank()).getPositionBitBoard();
            }

            return moves & ~ownPieces;
        }

        return 0L;
    }

    /**
     * Generates all possible moves for a sliding piece (such as a rook, bishop,
     * or queen) from a given starting square in a specified direction.
     *
     * The method iterates from the starting square in the direction specified
     * by the rank delta (dr) and file delta (df), adding each reachable square
     * to the move bitboard until it encounters a piece or the edge of the
     * board.
     *
     * @param fromSquare the starting square of the sliding piece
     * @param dr the change in rank per step (direction row)
     * @param df the change in file per step (direction file)
     * @return a bitboard (long) representing all squares the piece can move to
     * in the given direction
     */
    private long generateSlidingMoves(Square fromSquare, int dr, int df) {
        long moves = 0L;

        int file = fromSquare.getFile() + df;
        int rank = fromSquare.getRank() + dr;

        while (0 <= file && file < 8 && 0 <= rank && rank < 8) {
            int index = Square.toIndex(file, rank);
            moves |= Bits.putBit(moves, index);

            if (Bits.getBit(getAllPieces(), index)) {
                break;
            }

            rank += dr;
            file += df;
        }

        return moves;
    }

    private long generateRookMoves(Square fromSquare) {
        return generateSlidingMoves(fromSquare, 1, 0)
                | generateSlidingMoves(fromSquare, 0, 1)
                | generateSlidingMoves(fromSquare, -1, 0)
                | generateSlidingMoves(fromSquare, 0, -1);
    }

    private long generateBishopMoves(Square fromSquare) {
        return generateSlidingMoves(fromSquare, 1, 1)
                | generateSlidingMoves(fromSquare, 1, -1)
                | generateSlidingMoves(fromSquare, -1, -1)
                | generateSlidingMoves(fromSquare, -1, 1);

    }

    private long generatePawnMoves(Square fromSquare) {
        final int piece = getPiece(fromSquare.getIndex());
        final boolean isWhite = Piece.isWhite(piece);
        final long opponentPieces = isWhite ? blackPieces : whitePieces;

        final int direction = isWhite ? 1 : -1;
        final int up = direction * 8;

        long startingSquares = isWhite
                ? BitBoard.WHITE_STARTING_SQUARES
                : BitBoard.BLACK_STARTING_SQUARES;

        long singlePush = Bits.shift(fromSquare.getPositionBitBoard(), up) & ~getAllPieces();
        long doublePush = Bits.shift(fromSquare.getPositionBitBoard(), 2 * up)
                & Bits.shift(startingSquares, 2 * up)
                & Bits.shift(singlePush, up)
                & ~getAllPieces();

        long attacks = (Bits.oneAt(fromSquare.getIndex() + direction * 9)
                | Bits.oneAt(fromSquare.getIndex() + direction * 7)) & opponentPieces;

        return singlePush | doublePush | attacks;
    }

    public boolean squareHasPiece(int index) {
        return Bits.getBit(whitePieces | blackPieces, index);
    }

    public boolean squareHasPieceOfColor(int index, int color) {
        return Piece.isColor(getPiece(index), color);
    }

    public int getPiece(int index) {
        return board[index];
    }

    public int getPiece(Square square) {
        return getPiece(square.getIndex());
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

        loadFenPiecePlacement(segments[0]);
        loadFenActiveColor(segments[1]);
        loadFenCastlingRights(segments[2]);
        loadFenEnPassantSquare(segments[3]);
        loadFenMoveData(segments[4], segments[5]);
    }

    private void loadFenPiecePlacement(String segment) {
        int rank = 7;
        int file = 0;

        for (char c : segment.toCharArray()) {
            if (c == '/') {
                rank--;
                file = 0;
                continue;
            }
            if (Character.isDigit(c)) {
                file += Character.getNumericValue(c);
            } else {
                setPiece(Piece.fromFenChar(c), Square.toIndex(file, rank));
                file++;
            }
        }
    }

    private void loadFenActiveColor(String segment) {
        activeColor = segment.equals("w") ? Piece.WHITE : Piece.BLACK;
    }

    private void loadFenCastlingRights(String segment) {
        castlingRights.setCastlingRight(Piece.WHITE, true, segment.contains("K"));
        castlingRights.setCastlingRight(Piece.BLACK, true, segment.contains("k"));
        castlingRights.setCastlingRight(Piece.WHITE, false, segment.contains("Q"));
        castlingRights.setCastlingRight(Piece.BLACK, false, segment.contains("q"));
    }

    private void loadFenEnPassantSquare(String segment) {
        enPassantSquare = segment.equals("-") ? -1 : Notation.toIndex(segment);
    }

    private void loadFenMoveData(String halfMoveSegment, String fullMoveSegment) {
        fiftyMoveCounter = Integer.parseInt(halfMoveSegment);
        moveCounter = Integer.parseInt(fullMoveSegment);
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
            final int piece = getPiece(63 - i);

            if (empties > 0 && (piece != Piece.NONE || isRankEnd)) {
                fen += empties;
                empties = 0;
            }

            if (i > 0 && isRankEnd) {
                fen += '/';
            }

            if (piece == Piece.NONE) {
                empties++;
            } else {
                fen += Piece.toFenChar(piece);
            }
        }

        // Active color data
        fen += " ";
        fen += activeColor == Piece.WHITE ? "w" : "b";

        // Castling rights data
        fen += " ";

        fen += castlingRights.toFenString();

        // En passant square data
        fen += " " + (enPassantSquare != -1 ? Notation.toNotation(enPassantSquare) : "-");

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

            System.out.print(getPiece(i));
            System.out.print('\t');
        }

        System.out.println();

        System.out.println("FEN: " + toFen());

        System.out.println("White pieces: " + BitBoard.stringify(whitePieces));
        System.out.println("Black pieces: " + BitBoard.stringify(blackPieces));

    }

    public int[] getBoard() {
        return board;
    }

    private void setPiece(int piece, int pos) {
        final long position = Bits.oneAt(pos);

        if (Piece.isColor(piece, Piece.WHITE)) {
            whitePieces |= position;
            blackPieces &= ~position;
        } else {
            blackPieces |= position;
            whitePieces &= ~position;
        }

        board[pos] = piece;
    }

    private void removePiece(int pos) {
        final long position = Bits.oneAt(pos);
        whitePieces &= ~position;
        blackPieces &= ~position;

        board[pos] = Piece.NONE;
    }

    private void movePiece(int fromIndex, int toIndex) {
        setPiece(getPiece(fromIndex), toIndex);
        removePiece(fromIndex);
    }

    private void movePiece(Square fromSquare, Square toSquare) {
        movePiece(fromSquare.getIndex(), toSquare.getIndex());
    }

    private long getAllPieces() {
        return whitePieces | blackPieces;
    }

    private int getBackrankIndex(int piece) {
        return Piece.isWhite(piece) ? 0 : 7;
    }

    private Square getKingRookStartingSquare(int color) {
        return new Square(7, getBackrankIndex(color));
    }

    private Square getQueenRookStartingSquare(int color) {
        return new Square(7, getBackrankIndex(color));
    }
}
