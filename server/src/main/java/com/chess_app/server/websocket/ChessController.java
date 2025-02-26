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

    private ChessBoard board = new ChessBoard();

    @MessageMapping("/chess")
    @SendTo("/state/response")
    public ChessServerMessage response(ChessClientMessage message){
        switch (message.getRequest()){
            case "MOVESET":
                try {
                    Set<String> moves = board.getMoveset(message.getPositionFrom(), message.getPlayer().equals("WHITE") ? ChessPiece.WHITE : ChessPiece.BLACK);
                    System.out.println("Success: " + moves );
                    return new ChessServerMessage("SUCCESS", "MOVESET", message.getPlayer(), board.serialize(), String.join(",", moves));
                }catch (ChessError e){
                    System.out.println("Fail: "+e.getMessage());
                    return new ChessServerMessage("FAIL", "MOVESET", message.getPlayer(), e.getMessage(), e.getPositionInvolved());
                }
            case "STATE":
                System.out.println("Success: " + board.serialize());
                return new ChessServerMessage("SUCCESS", "STATE", message.getPlayer(), board.serialize());
            case "MOVE":
                try {
                    board.move(message.getPositionFrom(), message.getPositionTo(), message.getPlayer().equals("WHITE") ? ChessPiece.WHITE : ChessPiece.BLACK);
                    System.out.println("Success");
                    return new ChessServerMessage("SUCCESS", "MOVE", message.getPlayer(), board.serialize());
                }catch (ChessError e){
                    System.out.println("Fail: " + e.getMessage());
                    return new ChessServerMessage("FAIL", "MOVE", message.getPlayer(), e.getMessage(), e.getPositionInvolved());
                }
            case "RESET":
                board = new ChessBoard();
                System.out.println("Success: Game state is reset");
                return new ChessServerMessage("SUCCESS", "RESET", message.getPlayer(), board.serialize());
            case "PROMOTE":
                try {
                    board.promote(message.getPromoteTo(), message.getPlayer().equals("WHITE") ? ChessPiece.WHITE : ChessPiece.BLACK);
                    System.out.println("Success: Promoted to "+message.getPromoteTo());
                    return new ChessServerMessage("SUCCESS", "PROMOTE", message.getPlayer(), board.serialize());
                }catch(ChessError e){
                    System.out.println("Fail: "+e.getMessage());
                    return new ChessServerMessage("FAIL", "PROMOTE", message.getPlayer(), e.getMessage());
                }
        }

        return new ChessServerMessage("FAIL", message.getRequest(), message.getPlayer(), "Invalid request");
    }

}
