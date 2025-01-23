package com.chess_app.server.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    public void greeting(HelloMessage message) throws Exception{
        System.out.println("Hello, "+message.getName());
    }
}
