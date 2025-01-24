package com.chess_app.server.chessmodel;

public class Tests {
    public static void main(String[] args){
        ChessBoard board = new ChessBoard();

        System.out.println(board);

        try{
            board.move("01","04",ChessPiece.WHITE);
            System.out.println(board);
        }catch(ChessError e){
            System.out.println(e.getMessage());
        }
    }
}
