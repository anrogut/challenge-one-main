package com.gft.challenge.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.challenge.ChallengeOneMainApplication;
import com.gft.challenge.rx.event.model.FileEvent;
import com.gft.challenge.rx.event.FileEventReactiveStream;
import com.gft.challenge.rx.event.FileEventReactiveStreamObserver;
import com.gft.challenge.rx.SubscriptionHandler;
import com.gft.challenge.rx.struct.DirectoryStructureReactiveStream;
import com.gft.challenge.rx.struct.DirectoryStructureReactiveStreamObserver;
import com.gft.challenge.tree.Node;
import com.gft.challenge.ws.model.EventWebSocketMessage;
import com.gft.challenge.ws.model.PathNodeWebSocketMessage;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.WatchServiceConfiguration;
import org.junit.After;
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

import java.io.IOException;
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
    private static final String TOPIC_EVENT = "/topic/event/1";
    private static final String TOPIC_DIR = "/topic/dir/1";

    private BlockingQueue<String> events = new LinkedBlockingQueue<>();
    private BlockingQueue<String> dirs = new LinkedBlockingQueue<>();

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

        session.subscribe(TOPIC_EVENT, new DefaultStompFrameHandler(events));
        session.subscribe(TOPIC_DIR, new DefaultStompFrameHandler(dirs));

        Thread.sleep(100);
    }

    @After
    public void cleanUp() {
        dirs.clear();
        events.clear();
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
        fileEventReactiveStream.init();
        Observable<FileEvent> observable = fileEventReactiveStream.getEventStream(home);
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));

        Files.createDirectory(fileSystem.getPath("/home/test"));
        ObjectMapper objectMapper = new ObjectMapper();
        EventWebSocketMessage fe = objectMapper.readValue(events.poll(5000, TimeUnit.MILLISECONDS), EventWebSocketMessage.class);

        assertThat(fe.getPayload().getEventType()).isEqualTo("ENTRY_CREATE");
        assertThat(fe.getPayload().getAbsolutePath()).isEqualTo("/home/test");
    }

    @Test
    public void shouldEmitDirectoryStructure() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(10, TimeUnit.MILLISECONDS))
                .build());
        Path home = fileSystem.getPath("/home");
        Files.createDirectory(home);
        Files.createDirectory(fileSystem.getPath("/home/test"));

        FileEventReactiveStream fileEventReactiveStream = new FileEventReactiveStream(fileSystem);
        fileEventReactiveStream.init();
        DirectoryStructureReactiveStream directoryStructureReactiveStream = new DirectoryStructureReactiveStream();
        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(simpMessagingTemplate,fileSystem,fileEventReactiveStream, directoryStructureReactiveStream);
        subscriptionHandler.observeDirectory("/home",1);
        subscriptionHandler.sendDirectoryStructure("/home", 1);
        awaitMessagesCount(dirs,1, 5000, TimeUnit.MILLISECONDS);


        String jsonString = dirs.poll(5000, TimeUnit.MILLISECONDS);

        assertThat(jsonString).isNotNull();
        assertThat(jsonString.contains("/home/test")).isTrue();
    }

    @Test
    public void shouldSendErrorInformationMessageFromEventStream() throws Exception {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.error(new Exception("Exception")));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(events,1, 5000, TimeUnit.MILLISECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        EventWebSocketMessage eventWebSocketMessage = objectMapper.readValue(events.poll(),EventWebSocketMessage.class);

        assertThat(eventWebSocketMessage.getDescription()).isEqualTo("Exception");
    }

    @Test
    public void shouldSendErrorWithoutMessageFromEventStream() throws Exception {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.error(new Exception()));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(events,1, 5000, TimeUnit.MILLISECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        EventWebSocketMessage eventWebSocketMessage = objectMapper.readValue(events.poll(),EventWebSocketMessage.class);

        assertThat(eventWebSocketMessage.getDescription()).isEqualTo("Error");
    }

    @Test
    public void shouldSendErrorInformationMessageFromDirStream() throws IOException {
        Observable<Node<Path>> observable = Observable.defer(() -> Observable.error(new Exception("Exception")));
        observable.subscribe(new DirectoryStructureReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(dirs,1, 5000, TimeUnit.MILLISECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        PathNodeWebSocketMessage pathNodeWebSocketMessage = objectMapper.readValue(dirs.poll(),PathNodeWebSocketMessage.class);

        assertThat(pathNodeWebSocketMessage.getDescription()).isEqualTo("Exception");
    }

    @Test
    public void shouldSendErrorWithoutMessageFromDirStream() throws IOException {
        Observable<Node<Path>> observable = Observable.defer(() -> Observable.error(new Exception()));
        observable.subscribe(new DirectoryStructureReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(dirs,1, 5000, TimeUnit.MILLISECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        PathNodeWebSocketMessage pathNodeWebSocketMessage = objectMapper.readValue(dirs.poll(),PathNodeWebSocketMessage.class);

        assertThat(pathNodeWebSocketMessage.getDescription()).isEqualTo("Error");
    }

    @Test
    public void shouldSendCompleteInformationMessage() throws Exception {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.just(FileEvent.empty()));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(events,2, 5000, TimeUnit.MILLISECONDS);
        events.poll(5000, TimeUnit.MILLISECONDS);
        ObjectMapper objectMapper = new ObjectMapper();
        EventWebSocketMessage eventWebSocketMessage = objectMapper.readValue(events.poll(),EventWebSocketMessage.class);

        assertThat(eventWebSocketMessage.getDescription()).isEqualTo("Done");
    }

    public final boolean awaitMessagesCount(BlockingQueue<? extends String> queue, int expected, long timeout, TimeUnit unit) {
        while (timeout != 0 && queue.size() < expected) {
            try {
                unit.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Interrupted", e);
            }
            timeout--;
        }
        return queue.size() >= expected;
    }

    class DefaultStompFrameHandler implements StompFrameHandler {

        private BlockingQueue<String> queue;

        DefaultStompFrameHandler(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        @Override
        public synchronized void handleFrame(StompHeaders headers, Object payload) {
            queue.offer(new String((byte[]) payload));
        }
    }

}
