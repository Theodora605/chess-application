package com.chess_app.server.websocket;

public class ChessClientMessage {

    private String request;
    private String positionFrom;
    private String positionTo;
    private String player;
    private char promoteTo;

    public ChessClientMessage(){
        this.request = null;
        this.positionFrom = null;
        this.positionTo = null;
        this.player = null;
        this.promoteTo = '\0';
    }

    public ChessClientMessage(String request){
        this.request = request;
        this.positionFrom = null;
        this.positionTo = null;
        this.player = null;
        this.promoteTo = '\0';
    }

    public ChessClientMessage(String request, String player){
        this.request = request;
        this.player = player;
        this.positionFrom = null;
        this.positionTo = null;
        this.promoteTo = '\0';
    }

    public ChessClientMessage(String request, String player, char promoteTo){
        this.request = request;
        this.player = player;
        this.promoteTo = promoteTo;
        this.positionFrom = null;
        this.positionTo = null;
    }

    public ChessClientMessage(String request, String player, String positionFrom){
        this.request = request;
        this.player = player;
        this.positionFrom = positionFrom;
        this.positionTo = null;
        this.promoteTo = '\0';
    }

    public ChessClientMessage(String request, String player, String positionFrom, String positionTo){
        this.request = request;
        this.player = player;
        this.positionFrom = positionFrom;
        this.positionTo = positionTo;
        this.promoteTo = '\0';
    }

    public String getRequest(){
        return request;
    }

    public String getPositionFrom(){
        return positionFrom;
    }

    public String getPositionTo(){
        return positionTo;
    }

    public String getPlayer() {
        return player;
    }

    public char getPromoteTo() {
        return promoteTo;
    }

    public void setPromoteTo(char promoteTo) {
        this.promoteTo = promoteTo;
    }

    public void setRequest(String request){
        this.request = request;
    }

    public void setPositionFrom(String positionFrom){
        this.positionFrom = positionFrom;
    }

    public void setPositionTo(String positionTo){
        this.positionTo = positionTo;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
