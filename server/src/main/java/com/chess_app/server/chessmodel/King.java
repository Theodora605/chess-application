package com.chess_app.server.chessmodel;

import java.util.HashSet;
import java.util.Set;

public class King extends ChessPiece{

    public King(String id, int team){
        super(id, team);
    }
    @Override
    public Set<String> getMoveset(int xPos, int yPos, ChessPiece[][] board) {
        Set<String> res = new HashSet<>();
        int[][] dirs = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
        for(int[] dir: dirs){
            if(inBounds(xPos+dir[0], yPos+dir[1]) && ( board[xPos+dir[0]][yPos+dir[1]] == null || board[xPos+dir[0]][yPos+dir[1]].getTeam() != this.getTeam() ) ){
                res.add(String.valueOf(xPos+dir[0]) + String.valueOf(yPos+dir[1]));
            }
        }

        int xCurr;
        // Verify castling possible
        if(!isMoved()){
            xCurr = xPos - 1;
            while(inBounds(xCurr, yPos) && board[xCurr][yPos] == null ){
                xCurr--;
            }

            if(inBounds(xCurr, yPos) && board[xCurr][yPos] instanceof Rook && !board[xCurr][yPos].isMoved()){
                res.add(String.valueOf(xPos-2) + String.valueOf(yPos));
            }

            xCurr = xPos + 1;
            while(inBounds(xCurr, yPos) && board[xCurr][yPos] == null ){
                xCurr++;
            }

            if(inBounds(xCurr, yPos) && board[xCurr][yPos] instanceof Rook && !board[xCurr][yPos].isMoved()){
                res.add(String.valueOf(xPos+2) + String.valueOf(yPos));
            }
        }

        return res;
    }



    @Override
    public boolean hasCheck(int xPos, int yPos, ChessPiece[][] board) {
        int[][] dirs = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,1}, {1,-1}, {-1,-1}};
        for(int[] dir: dirs){
            if(inBounds(xPos+dir[0], yPos+dir[1]) && board[xPos+dir[0]][yPos+dir[1]] instanceof King ){
                return true;
            }
        }
        return false;
    }
}
