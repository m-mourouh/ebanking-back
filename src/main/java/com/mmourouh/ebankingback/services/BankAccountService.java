package com.mmourouh.ebankingback.services;

import com.mmourouh.ebankingback.dto.*;
import com.mmourouh.ebankingback.exceptions.BalanceNotSufficientException;
import com.mmourouh.ebankingback.exceptions.BankAccountNotFoundException;
import com.mmourouh.ebankingback.exceptions.CustomerNotFoundException;
import com.mmourouh.ebankingback.models.BankAccount;
import com.mmourouh.ebankingback.repositories.AccountOperationRepository;

import java.util.List;

public interface BankAccountService {
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    List<BankAccountDTO> getBankAccounts();
    List<AccountOperationDTO> getAccountOperations(String accountId);
    List<BankAccountDTO> getCustomerBankAccounts(Long customerId);
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
