import { useState } from "react";
import { useStompClient, useSubscription } from "react-stomp-hooks";
import { z } from "zod";
import styles from "./ChessBoard.module.css";

import WPawnImg from "../assets/white-pawn.png";
import WKnightImg from "../assets/white-knight.png";
import WBishopImg from "../assets/white-bishop.png";
import WRookImg from "../assets/white-rook.png";
import WQueenImg from "../assets/white-queen.png";
import WKingImg from "../assets/white-king.png";

import BPawnImg from "../assets/black-pawn.png";
import BKnightImg from "../assets/black-knight.png";
import BBishopImg from "../assets/black-bishop.png";
import BRookImg from "../assets/black-rook.png";
import BQueenImg from "../assets/black-queen.png";
import BKingImg from "../assets/black-king.png";

interface ChessPiece {
  id: string;
  player: string;
}

interface Position {
  x: number;
  y: number;
}

interface ChessClientMessage {
  request: "STATE" | "MOVE" | "MOVESET" | "RESET";
  positionFrom: string | null;
  positionTo: string | null;
  player: "WHITE" | "BLACK";
}

interface ChessServerMessage {
  status: "SUCCESS" | "FAIL";
  request: "STATE" | "MOVE" | "MOVESET" | "RESET";
  position: string | null;
  state: string;
}

const responseSchema: z.ZodType<ChessServerMessage> = z.object({
  status: z.enum(["SUCCESS", "FAIL"]),
  request: z.enum(["STATE", "MOVE", "MOVESET", "RESET"]),
  position: z.string().or(z.null()),
  state: z.string(),
});

const imgMap: { [name: string]: string } = {
  wp: WPawnImg,
  wn: WKnightImg,
  wb: WBishopImg,
  wr: WRookImg,
  wq: WQueenImg,
  wk: WKingImg,

  bp: BPawnImg,
  bn: BKnightImg,
  bb: BBishopImg,
  br: BRookImg,
  bq: BQueenImg,
  bk: BKingImg,
};

export const ChessBoard = () => {
  const [board, setBoard] = useState<ChessPiece[][]>(
    Array(8)
      .fill(null)
      .map(() => new Array(8).fill(null))
  );
  const stompClient = useStompClient();
  const [selection, setSelection] = useState<Position | null>(null);
  const [moves, setMoves] = useState<Set<string> | null>(null);

  const handleServerResponse = (response: string) => {
    const result = responseSchema.safeParse(JSON.parse(response));
    if (!result.success) {
      console.log("Failed to parse response");
      return;
    }

    const serverMessage = result.data;

    if (serverMessage.status === "FAIL") {
      console.log(serverMessage.state);
      return;
    }

    if (serverMessage.request === "MOVESET") {
      if (serverMessage.position === null) {
        setMoves(null);
      } else {
        const newMoves = new Set<string>();
        for (const pos of serverMessage.position.split(",")) {
          if (pos !== "") {
            newMoves.add(pos);
          }
        }
        setMoves(newMoves);
        console.log(moves);
      }
      return;
    }

    if (serverMessage.request === "STATE") {
      const boardBuffer = serverMessage.state.split(",");
      console.log(boardBuffer);
      const newBoard: ChessPiece[][] = Array(8)
        .fill(null)
        .map(() => new Array(8).fill(null));
      for (let y = 0; y < 8; y++) {
        for (let x = 0; x < 8; x++) {
          if (boardBuffer[y + 8 * x] !== ".") {
            newBoard[x][y] = {
              player: boardBuffer[y + 8 * x].substring(0, 1),
              id: boardBuffer[y + 8 * x].substring(1),
            };
          }
        }
      }
      setBoard(newBoard);
      return;
    }
  };

  const makeRequest = (message: ChessClientMessage) => {
    if (stompClient) {
      stompClient.publish({
        headers: {
          "content-type": "application/json",
        },
        destination: "/app/chess",
        body: JSON.stringify(message),
      });
    } else {
      console.log("Failed to publish");
    }
  };

  useSubscription("/state/response", (message) =>
    handleServerResponse(message.body)
  );

  return (
    <div className={styles.board}>
      {board.map((row, r) =>
        row.map((tile, c) => (
          <div
            className={styles.tile}
            style={{ background: (r + c) % 2 === 0 ? "green" : "beige" }}
            onClick={
              tile === null
                ? () => {}
                : () => {
                    if (selection?.x === c && selection?.y === r) {
                      setSelection(null);
                      setMoves(null);
                    } else {
                      setSelection({ x: c, y: r });
                      makeRequest({
                        request: "MOVESET",
                        positionFrom: c.toString() + r.toString(),
                        positionTo: null,
                        player: tile.player === "w" ? "WHITE" : "BLACK",
                      });
                    }
                  }
            }
          >
            <div
              className={
                selection?.x === c && selection?.y === r
                  ? styles.selectedTile
                  : ""
              }
            >
              <div
                className={
                  moves?.has(c.toString() + r.toString())
                    ? styles.possibleMoves
                    : styles.tile
                }
              >
                {tile && (
                  <img
                    src={imgMap[tile.player + tile.id.substring(0, 1)]}
                    className={styles.chessPiece}
                  />
                )}
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

export default ChessBoard;
