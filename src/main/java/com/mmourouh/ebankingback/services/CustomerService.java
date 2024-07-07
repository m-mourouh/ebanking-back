package com.mmourouh.ebankingback.services;

import com.mmourouh.ebankingback.dto.CustomerDTO;
import com.mmourouh.ebankingback.exceptions.CustomerNotFoundException;


import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getCustomers();
    CustomerDTO getCustomerById(Long id) throws CustomerNotFoundException;
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomer(Long customerId);
    List<CustomerDTO> searchCustomers(String keyword);
}
