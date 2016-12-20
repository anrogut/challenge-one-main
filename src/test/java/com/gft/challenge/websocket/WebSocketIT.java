package com.gft.challenge.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gft.challenge.ChallengeOneMainApplication;
import com.gft.challenge.rx.event.FileEvent;
import com.gft.challenge.rx.event.FileEventReactiveStream;
import com.gft.challenge.rx.event.FileEventReactiveStreamObserver;
import com.gft.challenge.rx.SubscriptionHandler;
import com.gft.challenge.rx.struct.DirStructureReactiveStream;
import com.gft.challenge.rx.struct.DirStructureReactiveStreamObserver;
import com.gft.challenge.tree.Node;
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
        Observable<FileEvent> observable = fileEventReactiveStream.getEventStream(home);
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));

        Files.createDirectory(fileSystem.getPath("/home/test"));
        ObjectMapper objectMapper = new ObjectMapper();
        FileEvent fe = objectMapper.readValue(events.poll(5000, TimeUnit.MILLISECONDS), FileEvent.class);

        assertThat(fe.getEventType()).isEqualTo("ENTRY_CREATE");
        assertThat(fe.getAbsolutePath()).isEqualTo("/home/test");
    }

    @Test
    public void shouldEmitDirectoryStructure() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix()
                .toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(10, TimeUnit.MILLISECONDS))
                .build());
        Path home = fileSystem.getPath("/home");
        Files.createDirectory(home);
        Path p = Files.createDirectory(fileSystem.getPath("/home/test"));

        FileEventReactiveStream fileEventReactiveStream = new FileEventReactiveStream(fileSystem);
        DirStructureReactiveStream dirStructureReactiveStream = new DirStructureReactiveStream();
        SubscriptionHandler subscriptionHandler = new SubscriptionHandler(simpMessagingTemplate,fileSystem,fileEventReactiveStream,dirStructureReactiveStream);
        subscriptionHandler.observeDirectory("/home",1);
        subscriptionHandler.sendDirectoryStructure("/home", 1);
        awaitMessagesCount(dirs,1, 5000, TimeUnit.MILLISECONDS);


        String jsonString = dirs.poll(5000, TimeUnit.MILLISECONDS);

        assertThat(jsonString.contains(p.toString())).isNotNull();
        assertThat(jsonString.contains("/home/test"));
    }

    @Test
    public void shouldSendErrorInformationMessageFromEventStream() throws InterruptedException {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.error(new Exception("Exception")));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(events,1, 5000, TimeUnit.MILLISECONDS);

        assertThat(events.poll()).isEqualTo("Exception");
    }

    @Test
    public void shouldSendErrorWithoutMessageFromEventStream() {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.error(new Exception()));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(events,1, 5000, TimeUnit.MILLISECONDS);

        assertThat(events.poll()).isEqualTo("Error");
    }

    @Test
    public void shouldSendErrorInformationMessageFromDirStream() throws InterruptedException {
        Observable<Node<Path>> observable = Observable.defer(() -> Observable.error(new Exception("Exception")));
        observable.subscribe(new DirStructureReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(dirs,1, 5000, TimeUnit.MILLISECONDS);

        assertThat(dirs.poll()).isEqualTo("Exception");
    }

    @Test
    public void shouldSendErrorWithoutMessageFromDirStream() {
        Observable<Node<Path>> observable = Observable.defer(() -> Observable.error(new Exception()));
        observable.subscribe(new DirStructureReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(dirs,1, 5000, TimeUnit.MILLISECONDS);

        assertThat(dirs.poll()).isEqualTo("Error");
    }

    @Test
    public void shouldSendCompleteInformationMessage() throws InterruptedException {
        Observable<FileEvent> observable = Observable.defer(() -> Observable.just(FileEvent.empty()));
        observable.subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, 1));
        awaitMessagesCount(events,2, 5000, TimeUnit.MILLISECONDS);
        events.poll(5000, TimeUnit.MILLISECONDS);

        assertThat(events.poll()).isEqualTo("done");
    }

    public final boolean awaitMessagesCount(BlockingQueue queue, int expected, long timeout, TimeUnit unit) {
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
