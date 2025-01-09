package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor color;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        switch (type){
            case ROOK:
                return rookMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);

            case BISHOP:
                return bishopMoves(board, myPosition);
            case QUEEN:

                break;

        }
        return null;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = row; i > 0; i--){
            ChessPosition checkPosition = new ChessPosition(i, col);
            moves.add(new ChessMove(myPosition, checkPosition, null));
            if (board.getPiece(checkPosition) != null){
                break;
            }
        }
        for (int i = row; i < 9; i++){
            ChessPosition checkPosition = new ChessPosition(i, col);
            moves.add(new ChessMove(myPosition, checkPosition, null));
            if (board.getPiece(checkPosition) != null){
                break;
            }
        }

        for (int i = col; i > 0; i--){
            ChessPosition checkPosition = new ChessPosition(row, i);
            moves.add(new ChessMove(myPosition, checkPosition, null));
            if (board.getPiece(checkPosition) != null){
                break;
            }
        }
        for (int i = col; i < 9; i++){
            ChessPosition checkPosition = new ChessPosition(row, i);
            moves.add(new ChessMove(myPosition, checkPosition, null));
            if (board.getPiece(checkPosition) != null){
                break;
            }
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        List <ChessPosition> positions = new ArrayList<>();

        positions.add(new ChessPosition(row+1, col+2));
        positions.add(new ChessPosition(row+1, col-2));
        positions.add(new ChessPosition(row-2, col+1));
        positions.add(new ChessPosition(row-2, col-1));
        positions.add(new ChessPosition(row-1, col+2));
        positions.add(new ChessPosition(row-1, col-2));
        positions.add(new ChessPosition(row+2, col+1));
        positions.add(new ChessPosition(row+2, col-1));

        for (ChessPosition position : positions) {
            if (position.getRow() > 0 && position.getRow() < 9 && position.getColumn() > 0 && position.getColumn() < 9) {
                moves.add(new ChessMove(myPosition, position, null));
            }
        }

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        int testRow = row;
        int testCol = col;

        while(validPosInt(testRow) && validPosInt(testCol)) {
            testRow += 1;
            testCol += 1;
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            moves.add(new ChessMove(myPosition, testPosition, null));
            if (board.getPiece(testPosition) != null) {
                break;
            }
        }

        testRow = row;
        testCol = col;
        while(validPosInt(testRow) && validPosInt(testCol)) {
            testRow += 1;
            testCol -= 1;
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            moves.add(new ChessMove(myPosition, testPosition, null));
            if (board.getPiece(testPosition) != null) {
                break;
            }
        }

        testRow = row;
        testCol = col;
        while(validPosInt(testRow) && validPosInt(testCol)) {
            testRow -= 1;
            testCol -= 1;
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            moves.add(new ChessMove(myPosition, testPosition, null));
            if (board.getPiece(testPosition) != null) {
                break;
            }
        }

        testRow = row;
        testCol = col;
        while(validPosInt(testRow) && validPosInt(testCol)) {
            testRow -= 1;
            testCol += 1;
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            moves.add(new ChessMove(myPosition, testPosition, null));
            if (board.getPiece(testPosition) != null) {
                break;
            }
        }

        return moves;
    }

        @Override
    public String toString() {
        String out;
        if (color == ChessGame.TeamColor.WHITE) {
            out = switch (type) {
                case KING -> "K";
                case QUEEN -> "Q";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case ROOK -> "R";
                case PAWN -> "P";
            };
        }
        else{
            out = switch (type) {
                case KING -> "k";
                case QUEEN -> "q";
                case BISHOP -> "b";
                case KNIGHT -> "n";
                case ROOK -> "r";
                case PAWN -> "p";
            };

        }

        return out;
    }

    private static boolean validPosInt(int val){
        return (val > 0 && val < 9);
    }
}
