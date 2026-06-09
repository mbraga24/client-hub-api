package com.havefunwith.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

import java.util.List;

// Customer REST controller - handles all incoming HTTP requests for customer operations
// Endpoint: api/v1/customers
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    // dependency injection
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // @RequestMapping(value = "/api/v1/customers", method = RequestMethod.GET)
    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("customerId") Long customerId) {
        return ResponseEntity.ok(customerService.getCustomer(customerId));
    }

    @PostMapping
    public ResponseEntity<Void> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        Long customerId = customerService.addCustomer(request);
        URI location = URI.create("/api/v1/customers/" + customerId);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    @PutMapping("{customerId}")
    public ResponseEntity<String> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok("Customer updated successfully.");
    }

}
