import { StompSessionProvider, useStompClient } from "react-stomp-hooks";
import ChessBoard from "./components/ChessBoard";
import styles from "./App.module.css";
import TeamSelection from "./components/TeamSelection";
import { useState } from "react";

export type Team = null | "WHITE" | "BLACK";

function App() {
  const [selection, setSelection] = useState<Team>(null);

  const handleSelection = (s: Team) => {
    setSelection(s);
  };

  return (
    <div className={styles.main}>
      <StompSessionProvider url={"http://localhost:8080/websocket"}>
        {!selection && <TeamSelection onSelection={handleSelection} />}
        {selection && <ChessBoard player={selection} />}
        <SendingMessages />
      </StompSessionProvider>
    </div>
  );
}

export function SendingMessages() {
  const stompClient = useStompClient();

  const requestReset = () => {
    if (stompClient) {
      stompClient.publish({
        headers: {
          "content-type": "application/json",
        },
        destination: "/app/chess",
        body: '{"request":"RESET","player":"WHITE"}',
      });
    } else {
      console.log("Failed to publish");
    }
  };
  return (
    <div>
      <button onClick={requestReset}>Reset</button>
    </div>
  );
}

export default App;
