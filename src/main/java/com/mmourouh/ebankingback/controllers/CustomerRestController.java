package com.mmourouh.ebankingback.controllers;

import com.mmourouh.ebankingback.dto.CustomerDTO;
import com.mmourouh.ebankingback.exceptions.CustomerNotFoundException;
import com.mmourouh.ebankingback.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/customers")
@CrossOrigin("*")
public class CustomerRestController {
    private CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<CustomerDTO> getCustomers(){
        return customerService.getCustomers();
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return customerService.getCustomerById(customerId);
    }
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return customerService.saveCustomer(customerDTO);
    }
    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public CustomerDTO updateCustomer(@PathVariable(name = "id") Long customerId, @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        customerDTO.setId(customerId);
        return customerService.updateCustomer(customerDTO);
    }
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public void deleteCustomer(@PathVariable(name = "id") Long customerId) {
        customerService.deleteCustomer(customerId);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<CustomerDTO> searchCustomers(@RequestParam(name = "keyword",defaultValue = "") String keyword){
        return customerService.searchCustomers("%"+keyword+"%");
    }
}
