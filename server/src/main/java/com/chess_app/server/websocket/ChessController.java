package com.chess_app.server.websocket;

import com.chess_app.server.chessmodel.ChessBoard;
import com.chess_app.server.chessmodel.ChessError;
import com.chess_app.server.chessmodel.ChessPiece;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
public class ChessController {

    ChessBoard board = new ChessBoard();

    @MessageMapping("/chess")
    @SendTo("/state/response")
    public ChessServerMessage response(ChessClientMessage message){
        switch (message.getRequest()){
            case "MOVESET":
                try {
                    Set<String> moves = board.getMoveset(message.getPositionFrom(), message.getPlayer().equals("WHITE") ? ChessPiece.WHITE : ChessPiece.BLACK);
                    System.out.println("Success: " + moves );
                    return new ChessServerMessage("SUCCESS", board.serialize(), String.join(",", moves));
                }catch (ChessError e){
                    System.out.println("Fail: "+e.getMessage());
                    return new ChessServerMessage("FAIL", e.getMessage(), e.getPositionInvolved());
                }
            case "STATE":
                System.out.println("Success: " + board.serialize());
                return new ChessServerMessage("SUCCESS", board.serialize());
            case "MOVE":
                try {
                    board.move(message.getPositionFrom(), message.getPositionTo(), message.getPlayer().equals("WHITE") ? ChessPiece.WHITE : ChessPiece.BLACK);
                    System.out.println("Success");
                    return new ChessServerMessage("SUCCESS", board.serialize());
                }catch (ChessError e){
                    System.out.println("Fail: " + e.getMessage());
                    return new ChessServerMessage("FAIL", e.getMessage(), e.getPositionInvolved());
                }
        }

        return new ChessServerMessage("FAIL", message.getRequest() + " is not a recognized request");
    }

}
