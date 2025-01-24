package com.chess_app.server.chessmodel;

public class ChessError extends Exception{

    private final String positionInvolved;

    public ChessError(String message){
        super(message);
        positionInvolved = null;
    }

    public ChessError(String message, String positionInvolved){
        super(message);
        this.positionInvolved = positionInvolved;
    }

    public String getPositionInvolved() {
        return positionInvolved;
    }
}
