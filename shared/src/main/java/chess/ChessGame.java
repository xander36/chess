package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null){
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        ChessBoard oldBoard = new ChessBoard(board);

        return moves;
    }

    public boolean hasValidMoves(TeamColor color){
        ChessPosition testPos = null;
        ChessPiece testPiece = null;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j <9; j++) {
                testPos = new ChessPosition(i,j);
                testPiece = board.getPiece(testPos);

                if(testPiece.getTeamColor() == color){
                    //I may be the problem, solution is below
                    continue;
                }

                Collection<ChessMove> moveset = validMoves(testPos);

                if (!moveset.isEmpty()){

                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j <9; j++) {
                kingPos = new ChessPosition(i, j);
                if (board.getPiece(kingPos).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(kingPos).getTeamColor() == teamColor) {
                    break;
                }
            }
        }

        ChessPosition testPos = null;
        ChessPiece testPiece = null;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j <9; j++) {
                testPos = new ChessPosition(i,j);
                testPiece = board.getPiece(testPos);

                if(testPiece.getPieceType() == ChessPiece.PieceType.KING || testPiece.getTeamColor() == teamColor){
                    //I may be the problem, solution is below
                    continue;
                }

                Collection<ChessMove> moveset = validMoves(testPos);
                //Collection<ChessMove> moveset = testPiece.pieceMoves(board, testPos);


                for (ChessMove move : moveset){
                    if (move.getEndPosition() == kingPos){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return !hasValidMoves(teamColor) && !isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !hasValidMoves(teamColor) && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
