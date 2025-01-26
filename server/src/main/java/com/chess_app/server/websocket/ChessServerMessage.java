package com.chess_app.server.websocket;

public class ChessServerMessage {

    private String status;
    private String request;
    private String position;
    private String state;

    public ChessServerMessage(){
        status = null;
        position = null;
        state = null;
    }

    public ChessServerMessage(String status){
        this.status = status;
        position = null;
        state = null;
    }
    
    public ChessServerMessage(String status, String request){
        this.status = status;
        this.request = request;
        position = null;
        state = null;
    }

    public ChessServerMessage(String status, String request, String state){
        this.status = status;
        this.request = request;
        this.state = state;
        this.position = null;
    }

    public ChessServerMessage(String status, String request, String state, String position){
        this.status = status;
        this.request = request;
        this.state = state;
        this.position = position;
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
