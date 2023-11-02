package com.example.customer.controller;

import com.example.customer.ddd.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class WebsocketController {

    private final Queue<WebSocketSession> socketSessions = new ConcurrentLinkedQueue<WebSocketSession>();

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websocket", webSocketHandler());

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(-1); // before annotated controllers
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

/*    @Bean
    SimpleUrlHandlerMapping webSocketSession(WebSocketHandler webSocketHandler) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/customers/", webSocketHandler), 10);
    }*/

    //https://docs.spring.io/spring-framework/reference/web/webflux-websocket.html
    @Bean
    WebSocketHandler webSocketHandler() {
        return new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession session) {

                return session.send(

                        session.receive()
                                .map(
                                        message -> {
                                            if (message.getType().toString().equals("TEXT")) {
                                                return message.getPayloadAsText(Charset.forName("UTF-8"));
                                            }
                                            return message;
                                        }
                                )
                                .doOnRequest(
                                        (value) -> {
                                            // OnOpen
                                            socketSessions.add(session);
                                            session.send(CustomerService.customers(new String[]{"A", "B", "C", "D"}).map(data -> {
                                                return session.textMessage(data.toString());
                                            })).subscribe();
                                        }
                                )
                                .doOnNext(message -> {//onMessage
                                            // broadCast
                                            Flux.fromIterable(socketSessions)
                                                    .filter(socketSession -> {
                                                        return !socketSession.equals(session);
                                                    })
                                                    .subscribe(socketSession -> {
                                                        socketSession.send(Flux.just(message).map(data -> {
                                                                    return session.textMessage("receiver: " + data);
                                                                }))
                                                                .subscribe();
                                                    });
                                            // broadCast
                                        }
                                )
                                .doOnError(throwException -> {
                                    socketSessions.remove(session);
                                })
                                .doOnComplete(
                                        () -> {//onClose
                                            socketSessions.remove(session);
                                        }
                                )
                                .doOnTerminate(
                                        () -> {//After onClose
                                            socketSessions.remove(session);
                                        }
                                )
                                .doOnCancel(
                                        () -> {
                                            socketSessions.remove(session);
                                        }
                                )
                                .map(
                                        message -> {
                                            return session.textMessage("sender: " + message);
                                        }
                                )

                );

            }
        };
    }


}
