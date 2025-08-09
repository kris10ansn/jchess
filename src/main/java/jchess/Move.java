package jchess;

public record Move(Square fromSquare, Square toSquare) {

    public int getFileDelta() {
        return toSquare.getFile() - fromSquare.getFile();
    }

    public int getRankDelta() {
        return toSquare.getRank() - fromSquare.getRank();
    }
}
