package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] contents;

    public ChessBoard() {
        contents = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard toCopy) {
        contents = new ChessPiece[8][8];

        for (int i = 0; i < 8; i++){
            ChessPiece[] row = toCopy.contents[i];
            ChessPiece[] newRow = new ChessPiece[8];
            for (int j = 0; j < 8; j++){
                ChessPiece piece = row[j];
                if (piece != null) {
                    ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    newRow[j] = newPiece;
                }
            }
            contents[i] = newRow;
        }
    }

    public ChessBoard (String representString){

        String[] rows = representString.split("&");
        contents = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++){
            String row = rows[i];
            ChessPiece[] newRow = new ChessPiece[8];
            for (int j = 0; j < 8; j++){
                ChessGame.TeamColor team = ChessGame.TeamColor.BLACK;
                char posChar = row.charAt(j);
                ChessPiece newPiece = null;
                if (Character.isUpperCase(posChar)){
                    team = ChessGame.TeamColor.WHITE;
                }

                if (posChar == 'K' || posChar == 'k'){
                    newPiece = new ChessPiece(team, ChessPiece.PieceType.KING);
                } else if (posChar == 'Q' || posChar == 'q') {
                    newPiece = new ChessPiece(team, ChessPiece.PieceType.QUEEN);
                } else if (posChar == 'B' || posChar == 'b') {
                    newPiece = new ChessPiece(team, ChessPiece.PieceType.BISHOP);
                }else if (posChar == 'N' || posChar == 'n') {
                    newPiece = new ChessPiece(team, ChessPiece.PieceType.KNIGHT);
                }else if (posChar == 'R' || posChar == 'r') {
                    newPiece = new ChessPiece(team, ChessPiece.PieceType.ROOK);
                }else if (posChar == 'P' || posChar == 'p') {
                    newPiece = new ChessPiece(team, ChessPiece.PieceType.PAWN);
                }

                newRow[j] = newPiece;
            }
            contents[i] = newRow;
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        contents[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return contents[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        contents = new ChessPiece[8][8];

        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        for (int i = 0; i < 8; i++){
            addPiece(new ChessPosition(2,i+1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7,i+1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    public boolean isEmptyAt(ChessPosition position){
        if (!isValidPos(position)){
            return false;
        }
        return this.getPiece(position) == null;
    }

    public static boolean isValidPos(ChessPosition position){
        return validPosInt(position.getRow()) && validPosInt(position.getColumn());
    }


    public static boolean validPosInt(int val){
        return (val > 0 && val < 9);
    }

    @Override
    public String toString() {
        String out = "";

        for (ChessPiece[] row : contents){
            for (ChessPiece piece : row){
                if (piece == null){
                    out += "*";
                }
                else{
                    out += piece.toString();
                }
            }
            out += "&";
        }

        return out;
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }
}
