import {
  StompSessionProvider,
  useStompClient,
  useSubscription,
} from "react-stomp-hooks";
import "./App.css";
import { useState } from "react";

function App() {
  return (
    <StompSessionProvider url={"http://localhost:8080/websocket"}>
      <SendingMessages />
      <SubscribingComponent />
    </StompSessionProvider>
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
      });
    } else {
      console.log("Failed to publish");
    }
  };
  return <button onClick={sendMessage}>Request State</button>;
}

export default App;
