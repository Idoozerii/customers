package com.example.customers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@SpringBootApplication
public class CustomersApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }

    private final String[] names =
            "Jean,Yuxin,Mario,Zhen,Mia,Maria,Dave,Johan,Francoise,Jose,Ibrahim".split(",");

    private final AtomicInteger counter = new AtomicInteger();
    private final Flux<Customer> customers = Flux.fromStream(
                    Stream.generate(() -> {
                        var id = counter.incrementAndGet();
                        return new Customer(id, names[id % names.length]);
                    })
            )
            .delayElements(Duration.ofSeconds(3));

    @Bean
    Flux<Customer> customers() {
        return this.customers.publish().autoConnect();
    }
}

@RestController
@RequiredArgsConstructor
class CustomerRestController {
    private final Flux<Customer> customerFlux;

    @GetMapping(
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            value = "/customers"
    )
    Flux<Customer> get() {
        return this.customerFlux;
    }
}

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

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {
    private Integer id;
    private String name;
}