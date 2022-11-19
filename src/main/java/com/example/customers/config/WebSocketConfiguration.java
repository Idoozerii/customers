package com.example.customers.config;

import com.example.customers.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Flux;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
class WebSocketConfiguration {
    private final ObjectMapper objectMapper;

    @SneakyThrows
    private String from(Customer customer) {
        return this.objectMapper.writeValueAsString(customer);
    }

    @Bean
    WebSocketHandler webSocketHandler(Flux<Customer> customerFlux) {
        return session -> {
            var map = customerFlux
                    .map(this::from)
                    .map(session::textMessage);

            return session.send(map);
        };
    }

    @Bean
    SimpleUrlHandlerMapping simpleUrlHandlerMapping(WebSocketHandler customersWsh) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/customers", customersWsh), 10);
    }
}
