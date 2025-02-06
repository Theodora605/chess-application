package com.chess_app.server.chessmodel;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends ChessPiece{

    boolean hopped;

    @Override
    public Set<String> getMoveset(int xPos, int yPos, ChessPiece[][] board) {
        Set<String> res = new HashSet<>();
        int dir = this.getTeam() == ChessPiece.WHITE ? 1: -1;
        if(ChessPiece.inBounds(xPos, yPos + dir) && board[xPos][yPos+dir] == null){
            res.add( String.valueOf(xPos) + String.valueOf(yPos + dir) );
            if(this.firstMove(yPos) && board[xPos][yPos + 2 * dir] == null){
                res.add(String.valueOf(xPos) + String.valueOf(yPos + 2 * dir) );
            }
        }

        if(inBounds(xPos-1, yPos+dir) && this.canTake(xPos-1, yPos+dir, board) ){
            res.add( String.valueOf(xPos-1) + String.valueOf(yPos+dir) );
        }

        if(inBounds(xPos+1, yPos+dir) && this.canTake(xPos+1, yPos+dir, board) ){
            res.add(String.valueOf(xPos+1) + String.valueOf(yPos+dir) );
        }

        // Handle en passant
        if(inBounds(xPos-1, yPos) && board[xPos-1][yPos] instanceof Pawn p){
            if(p.hasHopped()){
                res.add( String.valueOf(xPos-1) + String.valueOf(yPos+dir) );
            }
        }

        if(inBounds(xPos+1, yPos) && board[xPos+1][yPos] instanceof Pawn p){
            if(p.hasHopped()){
                res.add( String.valueOf(xPos+1) + String.valueOf(yPos+dir) );
            }
        }


        return res;
    }

    @Override
    public boolean hasCheck(int xPos, int yPos, ChessPiece[][] board) {

        int dir = this.getTeam() == ChessPiece.WHITE ? 1: -1;

        return inBounds(xPos - 1, yPos + dir) && canTake(xPos -1, yPos+dir, board) && board[xPos - 1][yPos + dir] instanceof King
           ||  inBounds(xPos + 1, yPos + dir) && canTake(xPos+1, yPos+dir, board) && board[xPos + 1][yPos + dir] instanceof King;
    }


    private boolean firstMove(int yPos){
        return this.getTeam() == ChessPiece.WHITE ? yPos == 1 : yPos == 6;

    }

    public boolean hasHopped(){
        return hopped;
    }

    public void setHopped(boolean hopped){
        this.hopped = hopped;
    }

    public Pawn(String id, int team){
        super(id, team);
        hopped = false;
    }
}
