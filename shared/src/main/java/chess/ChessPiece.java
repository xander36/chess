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
                List<ChessMove> moves = new ArrayList<>();
                moves.addAll(rookMoves(board, myPosition));
                moves.addAll(bishopMoves(board, myPosition));
                return moves;
            case KING:
                return kingMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board, myPosition);
        }
        return null;
    }

    private boolean isFriendAt(ChessBoard board, ChessPosition position){
        if (!ChessBoard.isValidPos(position)){
            return false;
        }
        return !board.isEmptyAt(position) && board.getPiece(position).getTeamColor() == getTeamColor();
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int i = row-1; i > 0; i--){
            ChessPosition checkPosition = new ChessPosition(i, col);
            if(!isFriendAt(board, checkPosition)) {
                moves.add(new ChessMove(myPosition, checkPosition, null));
            }
            if (!board.isEmptyAt(checkPosition)){
                break;
            }
        }
        for (int i = row+1; i < 9; i++){
            ChessPosition checkPosition = new ChessPosition(i, col);
            if(!isFriendAt(board, checkPosition)) {
                moves.add(new ChessMove(myPosition, checkPosition, null));
            }
            if (!board.isEmptyAt(checkPosition)){
                break;
            }
        }

        for (int i = col-1; i > 0; i--){
            ChessPosition checkPosition = new ChessPosition(row, i);
            if(!isFriendAt(board, checkPosition)) {
                moves.add(new ChessMove(myPosition, checkPosition, null));
            }
            if (!board.isEmptyAt(checkPosition)){
                break;
            }
        }
        for (int i = col+1; i < 9; i++){
            ChessPosition checkPosition = new ChessPosition(row, i);
            if(!isFriendAt(board, checkPosition)) {
                moves.add(new ChessMove(myPosition, checkPosition, null));
            }
            if (!board.isEmptyAt(checkPosition)){
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
                if(!isFriendAt(board, position)){
                    moves.add(new ChessMove(myPosition, position, null));
                }
            }
        }

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        int testRow = row+1;
        int testCol = col+1;

        while(ChessBoard.validPosInt(testRow) && ChessBoard.validPosInt(testCol)) {
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            if(!isFriendAt(board, testPosition)) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
            if (!board.isEmptyAt(testPosition)){
                break;
            }
            testRow += 1;
            testCol += 1;
        }

        testRow = row+1;
        testCol = col-1;
        while(ChessBoard.validPosInt(testRow) && ChessBoard.validPosInt(testCol)) {
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            if(!isFriendAt(board, testPosition)) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
            if (!board.isEmptyAt(testPosition)){
                break;
            }
            testRow += 1;
            testCol -= 1;
        }

        testRow = row-1;
        testCol = col-1;
        while(ChessBoard.validPosInt(testRow) && ChessBoard.validPosInt(testCol)) {
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            if(!isFriendAt(board, testPosition)) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
            if (!board.isEmptyAt(testPosition)){
                break;
            }
            testRow -= 1;
            testCol -= 1;
        }

        testRow = row-1;
        testCol = col+1;
        while(ChessBoard.validPosInt(testRow) && ChessBoard.validPosInt(testCol)) {
            ChessPosition testPosition = new ChessPosition(testRow, testCol);
            if(!isFriendAt(board, testPosition)) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
            if (!board.isEmptyAt(testPosition)){
                break;
            }
            testRow -= 1;
            testCol += 1;
        }

        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        List <ChessPosition> positions = new ArrayList<>();

        positions.add(new ChessPosition(row, col+1));
        positions.add(new ChessPosition(row, col-1));
        positions.add(new ChessPosition(row+1, col));
        positions.add(new ChessPosition(row-1, col));
        positions.add(new ChessPosition(row+1, col+1));
        positions.add(new ChessPosition(row+1, col-1));
        positions.add(new ChessPosition(row-1, col+1));
        positions.add(new ChessPosition(row-1, col-1));

        for (ChessPosition position : positions) {
            if (ChessBoard.isValidPos(position)) {
                if (!isFriendAt(board, position)){
                    moves.add(new ChessMove(myPosition, position, null));
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        List <ChessPosition> positions = new ArrayList<>();
        if (color == ChessGame.TeamColor.BLACK){
            if (board.isEmptyAt(new ChessPosition(row-1, col))){
                positions.add(new ChessPosition(row-1, col));
                if (row == 7 && board.isEmptyAt(new ChessPosition(row-2, col))){
                    positions.add(new ChessPosition(row-2, col));
                }
            }

            ChessPosition testPosition = new ChessPosition(row-1, col-1);
            if(!board.isEmptyAt(testPosition) && !isFriendAt(board, testPosition)){
                positions.add(testPosition);
            }
            testPosition = new ChessPosition(row-1, col+1);
            if(!board.isEmptyAt(testPosition) && !isFriendAt(board, testPosition)){
                positions.add(testPosition);
            }
        }
        else{
            if (board.isEmptyAt(new ChessPosition(row+1, col))){
                positions.add(new ChessPosition(row+1, col));
                if (row == 2 && board.isEmptyAt(new ChessPosition(row+2, col))){
                    positions.add(new ChessPosition(row+2, col));
                }
            }

            ChessPosition testPosition = new ChessPosition(row+1, col-1);
            if(!board.isEmptyAt(testPosition) && !isFriendAt(board, testPosition)){
                positions.add(testPosition);
            }
            testPosition = new ChessPosition(row+1, col+1);
            if(!board.isEmptyAt(testPosition) && !isFriendAt(board, testPosition)){
                positions.add(testPosition);
            }
        }

        for (ChessPosition pos : positions){
            if (ChessBoard.validPosInt(pos.getRow()) && ChessBoard.validPosInt(pos.getColumn())) {
                if ((getTeamColor() == ChessGame.TeamColor.WHITE && pos.getRow() == 8) ||
                        (getTeamColor() == ChessGame.TeamColor.BLACK && pos.getRow() == 1)) {
                    moves.add(new ChessMove(myPosition, pos, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, pos, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, pos, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, pos, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, pos, null));
                }
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


}
