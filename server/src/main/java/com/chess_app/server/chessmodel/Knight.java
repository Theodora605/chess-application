package com.chess_app.server.chessmodel;

import java.util.HashSet;
import java.util.Set;

public class Knight extends ChessPiece{

    public Knight(String id, int team){
        super(id, team);
    }

    @Override
    public Set<String> getMoveset(int xPos, int yPos, ChessPiece[][] board) {
        Set<String> res = new HashSet<>();
        int[][] dirs = new int[][]{{2,1}, {-2,1}, {2,-1}, {-2,-1}, {1,2}, {-1,2}, {1,-2}, {-1,-2}};
        for(int[] dir: dirs){
            if(inBounds(xPos+dir[0], yPos+dir[1]) && ( board[xPos+dir[0]][yPos+dir[1]] == null || board[xPos+dir[0]][yPos+dir[1]].getTeam() != this.getTeam() ) ){
                res.add(String.valueOf(xPos+dir[0]) + String.valueOf(yPos+dir[1]));
            }
        }

        return res;
    }

    @Override
    public boolean hasCheck(int xPos, int yPos, ChessPiece[][] board) {
        int[][] dirs = new int[][]{{2,1}, {-2,1}, {2,-1}, {-2,-1}, {1,2}, {-1,2}, {1,-2}, {-1,-2}};
        for(int[] dir: dirs){
            if(inBounds(xPos+dir[0], yPos+dir[1]) && board[xPos+dir[0]][yPos+dir[1]] != null
                    && board[xPos+dir[0]][yPos+dir[1]].getTeam() != this.getTeam() && board[xPos+dir[0]][yPos+dir[1]] instanceof King ){
                return true;
            }
        }
        return false;
    }
}
