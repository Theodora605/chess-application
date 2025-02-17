# Chess Service
This Java application is a server that manages a chess game state using a STOMP websocket

## Recent Changes
- Added en passant
- Added castling
**Upcoming:** Handling of pawn promotions

## Build Instructions
It is recommended to use IntelliJ with Java 17 for this project.

Compile a jar file by executing the Maven Goal:
`mvn package -Dmaven.test.skip`

## Running the Server
The server can either be launched directly from the command terminal or as a docker container:

### 1. Command Terminal
Navigate to the compiled `server-3.4.1.jar` and run the command

```
java -jar server-3.4.1.jar
```

### 2. Docker
Build the docker image

```
docker build -t chess-server .
```

Then, run the container

```
docker run -p 8080:8080 chess-server
```

## API
The requests endpoint is `\app\chess` and the subscription endpoint is `\state\response`. The four types of requests that can be made to the server are: `STATE`, `RESET`, `MOVESET`, and `MOVE`.

### 1. STATE
```
SEND
destination:/app/chess

{
    "request":"STATE"
}
{{NULL_CHAR}}
```
A `STATE` request has the server return a comma-deliminated list representing the current board state. The list represents the contents of each tile, starting from row 1 to row 8. ie: A1, B1, C1, D1, E1, F1, G1, H1, A2, B2, ...

For example, the initial board state will give the following:

``
wr0,wn0,wb0,wk,wq,wb1,wn1,wr1,wp0,wp1,wp2,wp3,wp4,wp5,wp6,wp7,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,bp0,bp1,bp2,bp3,bp4,bp5,bp6,bp7,br0,bn0,bb0,bk,bq,bb1,bn1,br1
``

Periods represent empty tiles, otherwise an encoding for the piece on that tile is given. The encoding is of the following format:
- First character is either 'w' or 'b', representing white or black respectively.
- The second character denotes the type of piece:
  - **p :** Pawn
  - **r :** Rook
  - **n :** Knight
  - **b :** Bishop
  - **q :** Queen
  - **k :** King
- Any subsequent characters are used for identifiers for duplicate pieces where applicable

### 2. RESET
```
SEND
destination:/app/chess

{
    "request":"RESET"
}
{{NULL_CHAR}}
```
A `RESET` request is used to reset the board back to the initial game state.

### 3. MOVESET
```
SEND
destination:/app/chess

{
    "request":"MOVESET",
    "positionFrom":"01",
    "player":"WHITE"
}
{{NULL_CHAR}}
```
A `MOVESET` request has the server return a list of valid moves that can be made by the chess piece located at the position passed to `positionFrom`. The position should be given by a string of form "cr" where c is the column number and r is the row number,
both numbers between 0 to 7. For example, "01" refers to the chess piece located at A2 on the chess board.

If this request is made for a chess piece that does not belong to the specified player, it will result in a failure.

### 4. Move
```
SEND
destination:/app/chess

{
    "request":"MOVE",
    "positionFrom":"01",
    "positionTo":"03",
    "player":"WHITE"
}
{{NULL_CHAR}}
```
A `MOVE` request prompts the server to update the board state by making the move as specified by `positionFrom` and `positionTo`. See the above section on `MOVESET` on information on how the position data ought to be passed.

The server handles whether it is the correct player's turn, and if the move is valid.

## Postman
Before exploring the API with Postman, in `WebSocketConfig.java` modify
```java
public void registerStompEndpoints(StompEndpointRegistry registry){
  registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
}
```
to the following
```java
public void registerStompEndpoints(StompEndpointRegistry registry){
  registry.addEndpoint("/websocket");
}
```
A sample collection of requests can be forked from https://www.postman.com/gold-shadow-757137/chess-stomp-websocket/overview

**Bug:** The `{{NULL_CHAR}}` collection variable does not get forked correctly with the collection. A workaround is to execute the GraphQL request in the collection before the websocket requests. The request will fail, but will
run a Postman script to re-assign the variable correctly.

Revert this change in `WebSocketConfig.java` to circumvent any CORS issues that may occur when implementing a game client.
