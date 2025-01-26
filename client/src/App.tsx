import {
  StompSessionProvider,
  useStompClient,
  useSubscription,
} from "react-stomp-hooks";
import { useState } from "react";
import ChessBoard from "./components/ChessBoard";
import styles from "./App.module.css";

function App() {
  return (
    <div className={styles.main}>
      <StompSessionProvider url={"http://localhost:8080/websocket"}>
        <ChessBoard />
        <SendingMessages />
      </StompSessionProvider>
    </div>
  );
}

export function SubscribingComponent() {
  const [response, setResponse] = useState("No message yet");

  useSubscription("/state/response", (message) => setResponse(message.body));

  return <div>{response}</div>;
}

export function SendingMessages() {
  const stompClient = useStompClient();

  const sendMessage = () => {
    if (stompClient) {
      stompClient.publish({
        headers: {
          "content-type": "application/json",
        },
        destination: "/app/chess",
        body: '{"request":"STATE"}',
        //body: '{"request":"MOVESET","positionFrom":"01","player":"WHITE"}',
      });
    } else {
      console.log("Failed to publish");
    }
  };
  return <button onClick={sendMessage}>Request State</button>;
}

export default App;
