package com.gft.challenge.websocket;

import com.gft.challenge.ChallengeOneMainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ChallengeOneMainApplication.class)
public class WebSocketIT {
    private static final String STOMP_URL = "ws://localhost:8080/ws";
    private static final String TOPIC = "/topic/event";

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    public void shouldSendWebSocketMessage() throws InterruptedException, ExecutionException, TimeoutException {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport((new StandardWebSocketClient()))))
        );

        StompSession session = stompClient.connect(STOMP_URL,
                new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        session.subscribe(TOPIC, new DefaultStompFrameHandler());
        Thread.sleep(1000);
        simpMessagingTemplate.convertAndSend(TOPIC, "siema");

        assertThat(messages.poll(1, TimeUnit.SECONDS)).isEqualTo("siema");
    }

    class DefaultStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            messages.offer(new String((byte[]) payload));
        }
    }
}
