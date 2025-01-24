package com.chess_app.server.chessmodel;

import java.util.HashSet;
import java.util.Set;

public class Queen extends ChessPiece{

    public Queen(String id, int team){
        super(id, team);
    }

    @Override
    public Set<String> getMoveset(int xPos, int yPos, ChessPiece[][] board) {
        Set<String> res = new HashSet<>();
        int[][] dirs = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}};
        for(int[] dir: dirs){
            int xCurr = xPos+dir[0];
            int yCurr = yPos+dir[1];
            while (inBounds(xCurr,yCurr) && board[xCurr][yCurr] == null){
                res.add(String.valueOf(xCurr) + String.valueOf(yCurr));
                xCurr = xCurr+dir[0];
                yCurr = yCurr+dir[1];
            }
            if(inBounds(xCurr,yCurr) && canTake(xCurr, yCurr, board)){
                res.add(String.valueOf(xCurr) + String.valueOf(yCurr));
            }
        }
        return res;
    }

    @Override
    public boolean hasCheck(int xPos, int yPos, ChessPiece[][] board) {
        int[][] dirs = new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}};
        for(int[] dir: dirs){
            int xCurr = xPos+dir[0];
            int yCurr = yPos+dir[1];
            while (inBounds(xCurr,yCurr) && board[xCurr][yCurr] == null){
                xCurr = xCurr+dir[0];
                yCurr = yCurr+dir[1];
            }
            if(inBounds(xCurr,yCurr) && canTake(xPos, yPos, board) && board[xCurr][yCurr] instanceof King){
                return true;
            }
        }
        return false;
    }
}
