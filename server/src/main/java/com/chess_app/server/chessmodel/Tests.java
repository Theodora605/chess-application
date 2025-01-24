package com.chess_app.server.chessmodel;

public class Tests {
    public static void main(String[] args){
        ChessBoard board = new ChessBoard();

        System.out.println(board.serialize());
        try {
            System.out.println(board.getMoveset("01", ChessPiece.WHITE));
        } catch (ChessError e) {
            System.out.println(e.getMessage());
        }

        /**
        try{

            board.move("41","43",ChessPiece.WHITE);
            System.out.println(board);

            board.move("30", "74", ChessPiece.WHITE);
            System.out.println(board);

            board.move("46", "44", ChessPiece.BLACK);
            System.out.println(board);

            board.move("74", "44", ChessPiece.WHITE);
            System.out.println(board);

            board.move("06", "04", ChessPiece.BLACK);
            System.out.println(board);
        }catch(ChessError e){
            System.out.println(e.getMessage());
        }
         */
    }
}
