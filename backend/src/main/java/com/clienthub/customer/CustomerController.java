package com.clienthub.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers() {
        log.info("GET /api/v1/customers");
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("customerId") Long customerId) {
        log.info("GET /api/v1/customers/{}", customerId);
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @PostMapping
    public ResponseEntity<Void> registerCustomer(@RequestBody CustomerCreateRequest request) {
        log.info("POST /api/v1/customers :: username=[{}]", request.username());
        Long customerId = customerService.addCustomer(request);
        URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(customerId)
        .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        log.info("DELETE /api/v1/customers/{}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{customerId}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable("customerId") Long customerId,
            @RequestBody CustomerUpdateRequest request) {
        log.info("PUT /api/v1/customers/{}", customerId);
        customerService.updateCustomer(customerId, request);
        return ResponseEntity.noContent().build();
    }

}
