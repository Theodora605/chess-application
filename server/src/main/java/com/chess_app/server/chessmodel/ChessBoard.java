package com.chess_app.server.chessmodel;

import java.util.*;

public class ChessBoard {

    private final ChessPiece[][] board;
    private final Map<String, int[]> whitePositionMap;
    private final Map<String, int[]> blackPositionMap;

    public ChessBoard(){
        board = new ChessPiece[8][8];

        ChessPiece[] whiteBackRow = new ChessPiece[]{
                new Rook("r0", ChessPiece.WHITE),
                new Knight("n0", ChessPiece.WHITE),
                new Bishop("b0", ChessPiece.WHITE),
                new Queen("q", ChessPiece.WHITE),
                new King("k", ChessPiece.WHITE),
                new Bishop("b1", ChessPiece.WHITE),
                new Knight("n1", ChessPiece.WHITE),
                new Rook("r1", ChessPiece.WHITE)
        };

        ChessPiece[] blackBackRow = new ChessPiece[]{
                new Rook("r0", ChessPiece.BLACK),
                new Knight("n0", ChessPiece.BLACK),
                new Bishop("b0", ChessPiece.BLACK),
                new Queen("q", ChessPiece.BLACK),
                new King("k", ChessPiece.BLACK),
                new Bishop("b1", ChessPiece.BLACK),
                new Knight("n1", ChessPiece.BLACK),
                new Rook("r1", ChessPiece.BLACK)
        };

        for(int i=0; i < 8; i++){
            board[i][1] = new Pawn("p"+i, ChessPiece.WHITE);
            board[i][6] = new Pawn("p"+i, ChessPiece.BLACK);
            board[i][0] = whiteBackRow[i];
            board[i][7] = blackBackRow[i];
        }


        whitePositionMap = new HashMap<>();
        blackPositionMap = new HashMap<>();
        for(int i = 0; i < 8; i++){
            whitePositionMap.put(board[i][0].getId(), new int[]{i,0});
            whitePositionMap.put(board[i][1].getId(), new int[]{i,1});

            blackPositionMap.put(board[i][7].getId(), new int[]{i,7});
            blackPositionMap.put(board[i][6].getId(), new int[]{i,6});
        }

    }

    public void move(String from, String to, int player) throws ChessError{
        int xCurr = Integer.parseInt(from.substring(0,1));
        int yCurr = Integer.parseInt(from.substring(1));

        Set<String> moves = board[xCurr][yCurr].getMoveset(xCurr,yCurr, board);
        if(board[xCurr][yCurr].getTeam() != player || !moves.contains(to)){
            throw new ChessError(to + " is not a valid move");
        }

        int xNew = Integer.parseInt(to.substring(0,1));
        int yNew = Integer.parseInt(to.substring(1));

        board[xNew][yNew] = board[xCurr][yCurr];
        board[xCurr][yCurr] = null;

        // Verify check!
        if(player == ChessPiece.WHITE){
            for(String key: blackPositionMap.keySet()){
                int oppX = blackPositionMap.get(key)[0];
                int oppY = blackPositionMap.get(key)[1];
                if (board[oppX][oppY].hasCheck(oppX,oppY,board)){
                    // then revert state
                    board[xCurr][yCurr] = board[xNew][yNew];
                    board[xNew][yNew] = board[xCurr][yCurr];
                    throw new ChessError("In check!", String.valueOf(oppX)+String.valueOf(oppY));
                }
            }
        } else { // player == BLACK
            for(String key: whitePositionMap.keySet()){
                int oppX = whitePositionMap.get(key)[0];
                int oppY = whitePositionMap.get(key)[1];

                if (board[oppX][oppY].hasCheck(oppX,oppY,board)){
                    //then revert
                    board[xCurr][yCurr] = board[xNew][yNew];
                    board[xNew][yNew] = board[xCurr][yCurr];
                    throw new ChessError("In check!", String.valueOf(oppX)+String.valueOf(oppY));
                }
            }
        }

        if(player == ChessPiece.WHITE){
            whitePositionMap.put(board[xNew][yNew].getId(), new int[]{xNew,yNew});
        }else {
            blackPositionMap.put(board[xNew][yNew].getId(), new int[]{xNew,yNew});
        }


    }

    public String serialize(){
        List<String> res = new ArrayList<>();
        for (int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(board[x][y] == null){
                    res.add(".");
                }else{
                    res.add( (board[x][y].getTeam() == ChessPiece.WHITE ? "w" : "b") + board[x][y].getId());
                }

            }
        }
        return String.join(",",res);
    }

    public Set<String> getMoveset(String pos, int team) throws ChessError{
        int x = Integer.parseInt(pos.substring(0,1));
        int y = Integer.parseInt(pos.substring(1));
        if(board[x][y] == null){
            throw new ChessError("No piece in this location", pos);
        }
        if(board[x][y].getTeam() != team){
            throw new ChessError("Not your piece", pos);
        }
        return board[x][y].getMoveset(x,y,board);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < 8; i++){
            res.append("|");
            for(int j = 0; j < 8; j++){
                if(board[j][i] == null){
                    res.append(".");
                }else {
                    res.append(board[j][i].getId());
                }
                res.append("|");
            }
            res.append("\n");
        }
        return res.toString();
    }
}
