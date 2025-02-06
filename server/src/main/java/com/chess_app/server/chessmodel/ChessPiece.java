package com.chess_app.server.chessmodel;

import java.util.List;
import java.util.Set;

public abstract class ChessPiece {
    public static final int WHITE = 0, BLACK = 1;

    private final String id;
    private final int team;
    private boolean moved;

    public abstract Set<String> getMoveset(int xPos, int yPos, ChessPiece[][] board);
    public abstract boolean hasCheck(int xPos, int yPos, ChessPiece[][] board);

    public ChessPiece(String id, int team){
        this.id = id;
        this.team = team;
        this.moved = false;
    }

    public String getId(){
        return id;
    }

    public int getTeam(){
        return team;
    }

    public boolean isMoved(){
        return moved;
    }

    public void moved(){
        moved = true;
    }

    // TODO: Best home for these functions? Maybe separate ChessUtils?
    public static boolean inBounds(int xPos, int yPos){
        return xPos >= 0 && yPos >= 0 && xPos < 8 && yPos < 8;
    }

    public boolean canTake(int xPos, int yPos, ChessPiece[][] board){
        return board[xPos][yPos] != null && board[xPos][yPos].getTeam() != this.getTeam();
    }
}
