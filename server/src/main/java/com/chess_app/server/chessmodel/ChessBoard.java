package com.chess_app.server.chessmodel;

import java.util.*;

public class ChessBoard {

    // Turn state constants
    private static final int WHITE = 0,
                             BLACK = 1,
                             WHITE_PROMOTION = 2,
                             BLACK_PROMOTION = 3;

    private final ChessPiece[][] board;
    private final Map<String, int[]> whitePositionMap;
    private final Map<String, int[]> blackPositionMap;
    private int turn;

    // Must be tracked for en passant
    private Pawn pawnJustHopped;

    private int[] promoteSelection;
    private int nPromotions;

    public ChessBoard(){
        pawnJustHopped = null;
        promoteSelection = null;
        nPromotions = 0;

        turn = ChessPiece.WHITE;

        board = new ChessPiece[8][8];

        ChessPiece[] whiteBackRow = new ChessPiece[]{
                new Rook("r0", ChessPiece.WHITE),
                new Knight("n0", ChessPiece.WHITE),
                new Bishop("b0", ChessPiece.WHITE),
                new King("k", ChessPiece.WHITE),
                new Queen("q", ChessPiece.WHITE),
                new Bishop("b1", ChessPiece.WHITE),
                new Knight("n1", ChessPiece.WHITE),
                new Rook("r1", ChessPiece.WHITE)
        };

        ChessPiece[] blackBackRow = new ChessPiece[]{
                new Rook("r0", ChessPiece.BLACK),
                new Knight("n0", ChessPiece.BLACK),
                new Bishop("b0", ChessPiece.BLACK),
                new King("k", ChessPiece.BLACK),
                new Queen("q", ChessPiece.BLACK),
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

    public void promote(char promoteTo, int player) throws ChessError{
        if(turn != WHITE_PROMOTION && turn != BLACK_PROMOTION){
            throw new ChessError("Not the time to promote");
        }
        if(turn % 2 != player){
            throw new ChessError("Not your turn");
        }

        int x = promoteSelection[0], y = promoteSelection[1];

        switch (promoteTo){
            case 'r':
                board[x][y] = new Rook("rp"+nPromotions, player);
                break;
            case 'n':
                board[x][y] = new Knight("np"+nPromotions, player);
                break;
            case 'b':
                board[x][y] = new Bishop("bp"+nPromotions, player);
                break;
            case 'q':
                board[x][y] = new Queen("qp"+nPromotions, player);
                break;
            default:
                throw new ChessError(promoteTo + " is not a valid promotion");
        }

        nPromotions++;
        turn = (turn + 1) % 2;
    }

    public void move(String from, String to, int player) throws ChessError{
        if(turn % 2 != player){
            throw new ChessError("Not your turn");
        }
        if(turn == WHITE_PROMOTION || turn == BLACK_PROMOTION){
            throw new ChessError("Promotion expected");
        }

        int xCurr = Integer.parseInt(from.substring(0,1));
        int yCurr = Integer.parseInt(from.substring(1));

        Set<String> moves = board[xCurr][yCurr].getMoveset(xCurr,yCurr, board);
        if(board[xCurr][yCurr].getTeam() != player || !moves.contains(to)){
            throw new ChessError(to + " is not a valid move");
        }

        int xNew = Integer.parseInt(to.substring(0,1));
        int yNew = Integer.parseInt(to.substring(1));

        ChessPiece captured = board[xNew][yNew];
        board[xNew][yNew] = board[xCurr][yCurr];
        board[xCurr][yCurr] = null;

        if(captured != null){
            if(captured.getTeam() == ChessPiece.WHITE) {
                whitePositionMap.remove(captured.getId());
            }else{
                blackPositionMap.remove(captured.getId());
            }
        }

        ChessPiece enPassantCapture = null;
        // Verify en passant
        if(captured == null && board[xNew][yNew] instanceof Pawn && xNew != xCurr){
            enPassantCapture = board[xNew][yCurr];
            board[xNew][yCurr] = null;
            if(enPassantCapture.getTeam() == ChessPiece.WHITE) {
                whitePositionMap.remove(enPassantCapture.getId());
            }else{
                blackPositionMap.remove(enPassantCapture.getId());
            }
        }

        Map<String, int[]> oppPositionMap = player == ChessPiece.WHITE ? blackPositionMap : whitePositionMap;
        // Verify check!
        for(String key: oppPositionMap.keySet()){
            int oppX = oppPositionMap.get(key)[0];
            int oppY = oppPositionMap.get(key)[1];
            if (board[oppX][oppY].hasCheck(oppX,oppY,board)){
                // then revert state
                if(captured!=null){
                    oppPositionMap.put(captured.getId(), new int[]{xNew, yNew});
                }
                if(enPassantCapture != null){
                    board[xNew][yCurr] = enPassantCapture;
                    oppPositionMap.put(enPassantCapture.getId(), new int[]{xNew, yCurr});
                }
                board[xCurr][yCurr] = board[xNew][yNew];
                board[xNew][yNew] = captured;
                throw new ChessError("In check!", String.valueOf(oppX)+String.valueOf(oppY));
            }
        }
        /**
        if(player == ChessPiece.WHITE){
            for(String key: blackPositionMap.keySet()){
                int oppX = blackPositionMap.get(key)[0];
                int oppY = blackPositionMap.get(key)[1];
                if (board[oppX][oppY].hasCheck(oppX,oppY,board)){
                    // then revert state
                    if(captured!=null){
                        blackPositionMap.put(captured.getId(), new int[]{xNew, yNew});
                    }
                    if(enPassantCapture != null){
                        board[xNew][yCurr] = enPassantCapture;
                        blackPositionMap.put(enPassantCapture.getId(), new int[]{xNew, yCurr});
                    }
                    board[xCurr][yCurr] = board[xNew][yNew];
                    board[xNew][yNew] = captured;
                    throw new ChessError("In check!", String.valueOf(oppX)+String.valueOf(oppY));
                }
            }

        } else { // player == BLACK
            for(String key: whitePositionMap.keySet()){
                int oppX = whitePositionMap.get(key)[0];
                int oppY = whitePositionMap.get(key)[1];

                if (board[oppX][oppY].hasCheck(oppX,oppY,board)){
                    //then revert
                    if(captured!=null){
                        whitePositionMap.put(captured.getId(), new int[]{xNew, yNew});
                    }
                    if(enPassantCapture != null){
                        board[xNew][yCurr] = enPassantCapture;
                        whitePositionMap.put(enPassantCapture.getId(), new int[]{xNew, yCurr});
                    }
                    board[xCurr][yCurr] = board[xNew][yNew];
                    board[xNew][yNew] = captured;
                    throw new ChessError("In check!", String.valueOf(oppX)+String.valueOf(oppY));
                }
            }
        }
             */

        if(player == ChessPiece.WHITE){
            whitePositionMap.put(board[xNew][yNew].getId(), new int[]{xNew,yNew});
        }else {
            blackPositionMap.put(board[xNew][yNew].getId(), new int[]{xNew,yNew});
        }

        // Castling edge case
        if(board[xNew][yNew] instanceof King){
            if(xNew - xCurr == 2){

                board[xNew-1][yNew] = board[7][yNew];
                board[7][yNew] = null;
                if(board[xNew-1][yNew].getTeam() == ChessPiece.WHITE){
                    whitePositionMap.put(board[xNew-1][yNew].getId(), new int[]{xNew-1,yNew});
                }else{
                    blackPositionMap.put(board[xNew-1][yNew].getId(), new int[]{xNew-1, yNew});
                }
            }
            else if(xNew - xCurr == -2){
                board[xNew+1][yNew] = board[0][yNew];
                board[0][yNew] = null;
                if(board[xNew+1][yNew].getTeam() == ChessPiece.WHITE){
                    whitePositionMap.put(board[xNew+1][yNew].getId(), new int[]{xNew+1,yNew});
                }else{
                    blackPositionMap.put(board[xNew+1][yNew].getId(), new int[]{xNew+1, yNew});
                }
            }
        }

        // Update pawn hopped for en passant case
        if(pawnJustHopped != null){
            pawnJustHopped.setHopped(false);
        }
        if(board[xNew][yNew] instanceof Pawn pawnPiece){
            pawnPiece.setHopped(true);
            pawnJustHopped = pawnPiece;
        }

        board[xNew][yNew].moved();

        // Move to promotion state if pawn reaches end of board, otherwise advance to opponents turn.
        if((yNew == 0 || yNew == 7) && board[xNew][yNew] instanceof Pawn){
            promoteSelection = new int[]{xNew, yNew};
            turn += 2;
        }else{
            turn = (turn + 1) % 2;
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
