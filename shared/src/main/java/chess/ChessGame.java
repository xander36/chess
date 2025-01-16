package chess;

import java.util.Collection;
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
        board.resetBoard();
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessBoard oldBoard = new ChessBoard(board);

        for (ChessMove move : moves){
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);

            if (!isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }
            board = new ChessBoard(oldBoard);
        }


        return validMoves;
    }

    public boolean hasValidMoves(TeamColor color){
        ChessPosition testPos = null;
        ChessPiece testPiece = null;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j <9; j++) {
                testPos = new ChessPosition(i,j);
                testPiece = board.getPiece(testPos);

                if(testPiece != null && testPiece.getTeamColor() != color){
                    continue;
                }

                Collection<ChessMove> moveset = validMoves(testPos);

                if (moveset != null && !moveset.isEmpty()){
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
        ChessBoard oldBoard = new ChessBoard(board);

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece piece = board.getPiece(start);

        if (piece == null){
            throw new InvalidMoveException("No piece to move at this location");
        }

        if (turn != piece.getTeamColor()){
            throw new InvalidMoveException("Not your turn");
        }

        if (board.getPiece(end) != null && board.getPiece(end).getTeamColor() == piece.getTeamColor()){
            throw new InvalidMoveException("Can't kill teammate");
        }

        //I call this the AllCHECK, it just checks to see if the move that's being tried
        //is in the list of valid moves that can be generated by the validMoves method
        //It makes some of these InvalidMoveException condition blocks for specific illegality reasons irrelevant.
        if (!validMoves(start).contains(move)){
            throw new InvalidMoveException("Not in valid moves list");
        }

        if (move.getPromotionPiece() == null){
            board.addPiece(end, piece);
        }else{
            board.addPiece(end, new ChessPiece(piece.getTeamColor() , move.getPromotionPiece()));
        }
        board.addPiece(start, null);

        //This may not ever trip, as the functionality for differing error messages has been somewhat superseded by the AllCHECK
        if (isInCheck(piece.getTeamColor())){
            board = oldBoard;
            throw new InvalidMoveException("Cannot make move that puts king into check");
        }

        if (turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }else{
            turn = TeamColor.WHITE;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingTestPos = null;
        ChessPosition kingPos = null;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j <9; j++) {
                kingTestPos = new ChessPosition(i, j);

                if (board.getPiece(kingTestPos) != null && board.getPiece(kingTestPos).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(kingTestPos).getTeamColor() == teamColor) {
                    kingPos = kingTestPos;
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

                if (testPiece == null){
                    continue;
                }
//
//                if(testPiece.getPieceType() == ChessPiece.PieceType.KING || testPiece.getTeamColor() == teamColor){
//                    //I may be the problem, solution is below
//                    continue;
//                }

                //Collection<ChessMove> moveset = validMoves(testPos);
                Collection<ChessMove> moveset = testPiece.pieceMoves(board, testPos);

                if (moveset == null){
                    continue;
                }

                for (ChessMove move : moveset){
                    if (move.getEndPosition().getRow() == kingPos.getRow() && move.getEndPosition().getColumn() == kingPos.getColumn()){
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
        return !hasValidMoves(teamColor) && isInCheck(teamColor);
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
