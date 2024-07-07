package com.mmourouh.ebankingback.controllers;


import com.mmourouh.ebankingback.dto.*;
import com.mmourouh.ebankingback.exceptions.BalanceNotSufficientException;
import com.mmourouh.ebankingback.exceptions.BankAccountNotFoundException;
import com.mmourouh.ebankingback.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestController {
    private BankAccountService bankAccountService;

    @GetMapping
    public List<BankAccountDTO> getAllBankAccounts(){
        return bankAccountService.getBankAccounts();
    }

    @GetMapping("/{id}")
    public BankAccountDTO getBankAccount(@PathVariable(name = "id") String bankAccountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(bankAccountId);
    }

    @GetMapping("/{accountId}/operations")
    public List<AccountOperationDTO> getAccountOperations(@PathVariable String accountId){
        return bankAccountService.getAccountOperations(accountId);
    }

    @GetMapping("/{customerId}")
    public List<BankAccountDTO> getCustomerBankAccounts(@PathVariable Long customerId){
        return bankAccountService.getCustomerBankAccounts(customerId);
    }
    @GetMapping("/{accountId}/operations-page")
    public AccountHistoryDTO getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }
    @PostMapping("/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDTO.getAccountId(),debitDTO.getAmount(),debitDTO.getDescription());
        return debitDTO;
    }
    @PostMapping("/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDTO.getAccountId(),creditDTO.getAmount(),creditDTO.getDescription());
        return creditDTO;
    }
    @PostMapping("/transfer")
    public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount());
    }
}
