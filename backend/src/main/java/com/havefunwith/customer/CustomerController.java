package com.havefunwith.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public ResponseEntity<String> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        customerService.addCustomer(request);
        return ResponseEntity.ok("Customer successfully added");
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
