package com.mmourouh.ebankingback.mappers;

import com.mmourouh.ebankingback.dto.CustomerDTO;
import com.mmourouh.ebankingback.models.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapperImpl {
    public CustomerDTO fromCustomer(Customer customer){
        CustomerDTO customerDTO = new CustomerDTO();
        //Mapping using BeanUtils.copyProperties
        BeanUtils.copyProperties(customer, customerDTO);
        // mapping using a library MapStruct....
        // mapping using setters

        // customerDTO.setId(customerDTO.getId());
       // customerDTO.setName(customer.getName());
        // customerDTO.setEmail(customerDTO.getEmail());

        return customerDTO;
    }

    public Customer fromCustomerDTO(CustomerDTO customerDTO){
        Customer customer = new Customer();
        //Mapping using BeanUtils.copyProperties
        BeanUtils.copyProperties(customerDTO, customer);
        // mapping using a library MapStruct....
        // mapping using setters

        // customerDTO.setId(customerDTO.getId());
        // customerDTO.setName(customer.getName());
        // customerDTO.setEmail(customerDTO.getEmail());

        return customer;
    }
}
