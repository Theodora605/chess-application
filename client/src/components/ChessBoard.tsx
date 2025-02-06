import { useEffect, useState } from "react";
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
  player: "WHITE" | "BLACK";
  position: string | null;
  state: string;
}

const responseSchema: z.ZodType<ChessServerMessage> = z.object({
  status: z.enum(["SUCCESS", "FAIL"]),
  request: z.enum(["STATE", "MOVE", "MOVESET", "RESET"]),
  player: z.enum(["WHITE", "BLACK"]),
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

interface Props {
  player: "WHITE" | "BLACK";
}

export const ChessBoard = ({ player }: Props) => {
  useEffect(() => {
    makeRequest({
      request: "STATE",
      positionFrom: null,
      positionTo: null,
      player: player,
    });
  }, []);

  const [board, setBoard] = useState<ChessPiece[][]>(
    Array(8)
      .fill(null)
      .map(() => new Array(8).fill(null))
  );
  const stompClient = useStompClient();
  const [selection, setSelection] = useState<Position | null>(null);
  const [moves, setMoves] = useState<Set<string> | null>(null);

  const handleServerResponse = (response: string) => {
    console.log(response);
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

    if (
      serverMessage.request === "MOVESET" &&
      serverMessage.player === player &&
      selection !== null
    ) {
      console.log(serverMessage);
      if (serverMessage.position === null) {
        setMoves(null);
      } else {
        const newMoves = new Set<string>();
        for (const pos of serverMessage.position.split(",")) {
          if (pos !== "" && player === "BLACK") {
            newMoves.add(pos);
          } else {
            newMoves.add(
              (7 - parseInt(pos.charAt(0))).toString() +
                (7 - parseInt(pos.charAt(1))).toString()
            );
          }
        }
        setMoves(newMoves);
        console.log(moves);
      }
      return;
    }

    if (
      serverMessage.request === "STATE" ||
      serverMessage.request === "MOVE" ||
      serverMessage.request === "RESET"
    ) {
      const boardBuffer = serverMessage.state.split(",");
      if (player === "WHITE") {
        boardBuffer.reverse();
      }
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
      if (serverMessage.request === "MOVE") {
        setMoves(null);
        setSelection(null);
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
            key={`tile-${r}${c}`}
            className={styles.tile}
            style={{ background: (r + c) % 2 === 0 ? "beige" : "green" }}
            onClick={
              tile === null || tile.player !== player.charAt(0).toLowerCase()
                ? () => {
                    if (
                      moves?.has(c.toString() + r.toString()) &&
                      selection !== null
                    ) {
                      console.log(c, r);
                      makeRequest({
                        request: "MOVE",
                        positionFrom:
                          player === "BLACK"
                            ? selection.x.toString() + selection.y.toString()
                            : (7 - selection.x).toString() +
                              (7 - selection.y).toString(),
                        positionTo:
                          player === "BLACK"
                            ? c.toString() + r.toString()
                            : (7 - c).toString() + (7 - r).toString(),
                        player: player,
                      });
                    }
                  }
                : () => {
                    if (selection?.x === c && selection?.y === r) {
                      setSelection(null);
                      setMoves(null);
                    } else {
                      setSelection({ x: c, y: r });
                      makeRequest({
                        request: "MOVESET",
                        positionFrom:
                          player === "BLACK"
                            ? c.toString() + r.toString()
                            : (7 - c).toString() + (7 - r).toString(),
                        positionTo: null,
                        player: player,
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
