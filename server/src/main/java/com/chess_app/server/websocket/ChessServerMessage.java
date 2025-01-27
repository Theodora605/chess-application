package com.chess_app.server.websocket;

public class ChessServerMessage {

    private String status;
    private String request;
    private String player;
    private String position;
    private String state;

    public ChessServerMessage(){
        status = null;
        request = null;
        player = null;
        position = null;
        state = null;
    }

    public ChessServerMessage(String status){
        this.status = status;
        request = null;
        player = null;
        position = null;
        state = null;
    }
    
    public ChessServerMessage(String status, String request){
        this.status = status;
        this.request = request;
        player = null;
        position = null;
        state = null;
    }

    public ChessServerMessage(String status, String request, String player){
        this.status = status;
        this.request = request;
        this.player = player;
        position = null;
        state = null;
    }

    public ChessServerMessage(String status, String request, String player, String state){
        this.status = status;
        this.request = request;
        this.player = player;
        this.state = state;
        this.position = null;
    }

    public ChessServerMessage(String status, String request, String player, String state, String position){
        this.status = status;
        this.request = request;
        this.player = player;
        this.state = state;
        this.position = position;
    }

    public String getPlayer() {
        return player;
    }

    public String getRequest() {
        return request;
    }

    public String getState() {
        return state;
    }

    public String getPosition(){
        return position;
    }

    public String getStatus() {
        return status;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
