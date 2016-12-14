package com.gft.challenge.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.challenge.ChallengeOneMainApplication;
import com.gft.challenge.rx.FileEvent;
import com.gft.challenge.rx.FileEventReactiveStream;
import com.gft.challenge.rx.FileEventReactiveStreamObserver;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.WatchServiceConfiguration;
import org.junit.Before;
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
import rx.Observable;

import java.lang.reflect.Type;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = ChallengeOneMainApplication.class)
public class WebSocketIT {
    private static final String STOMP_URL = "ws://localhost:8080/ws";
    private static final String TOPIC = "/topic/event";

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Before
    public void setUp() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
                Collections.singletonList(new WebSocketTransport((new StandardWebSocketClient()))))
        );

        StompSession session = stompClient.connect(STOMP_URL,
                new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {
                }).get(2, TimeUnit.SECONDS);

        session.subscribe(TOPIC, new DefaultStompFrameHandler());

        Thread.sleep(100);
    }

    @Test
    public void shouldSendWebSocketMessage() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(10, TimeUnit.MILLISECONDS))
                .build());
        Path home = fileSystem.getPath("/home");
        Files.createDirectory(home);
        FileEventReactiveStream fileEventReactiveStream = new FileEventReactiveStream(fileSystem);
        Observable<FileEvent> observable = fileEventReactiveStream.getEventStream(home);
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate));

        Files.createDirectory(fileSystem.getPath("/home/test"));
        ObjectMapper objectMapper = new ObjectMapper();
        FileEvent fe = objectMapper.readValue(messages.poll(5000, TimeUnit.MILLISECONDS), FileEvent.class);

        assertThat(fe.getEventType()).isEqualTo("ENTRY_CREATE");
        assertThat(fe.getAbsolutePath()).isEqualTo("/home/test");
    }

    @Test
    public void shouldSendErrorInformationMessage() throws InterruptedException {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.error(new Exception("Exception")));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate));
        awaitMessagesCount(1, 5000, TimeUnit.MILLISECONDS);

        assertThat(messages.poll()).isEqualTo("Exception");
    }

    @Test
    public void shouldSendCompleteInformationMessage() throws InterruptedException {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.just(new FileEvent()));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate));
        awaitMessagesCount(2, 5000, TimeUnit.MILLISECONDS);
        messages.poll(5000, TimeUnit.MILLISECONDS);

        assertThat(messages.poll()).isEqualTo("done");
    }

    public final boolean awaitMessagesCount(int expected, long timeout, TimeUnit unit) {
        while (timeout != 0 && messages.size() < expected) {
            try {
                unit.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Interrupted", e);
            }
            timeout--;
        }
        return messages.size() >= expected;
    }

    class DefaultStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public synchronized void handleFrame(StompHeaders headers, Object payload) {
            messages.offer(new String((byte[]) payload));
        }
    }

}
