package com.example.customers.config;

import com.example.customers.model.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Configuration
public class CustomerConfig {
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
