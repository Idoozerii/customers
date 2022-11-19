package com.example.customers.controller;

import com.example.customers.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
