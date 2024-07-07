package com.mmourouh.ebankingback.services;

import com.mmourouh.ebankingback.dto.*;
import com.mmourouh.ebankingback.enums.AccountStatus;
import com.mmourouh.ebankingback.enums.OperationType;
import com.mmourouh.ebankingback.exceptions.BalanceNotSufficientException;
import com.mmourouh.ebankingback.exceptions.BankAccountNotFoundException;
import com.mmourouh.ebankingback.exceptions.CustomerNotFoundException;
import com.mmourouh.ebankingback.mappers.BankAccountMapperImpl;
import com.mmourouh.ebankingback.models.*;
import com.mmourouh.ebankingback.repositories.AccountOperationRepository;
import com.mmourouh.ebankingback.repositories.BankAccountRepository;
import com.mmourouh.ebankingback.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{
    private AccountOperationRepository accountOperationRepository;
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private BankAccountMapperImpl bankAccountMapper;
    //For logging (log4j, slf4j)
    //Logger log = Logger.getLogger(this.getClass().getName()); // or @slf4j instead

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));

        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;

            return bankAccountMapper.fromSavingBankAccount(savingAccount);
        }
        else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapper.fromCurrentBankAccount(currentAccount);

        }
    }
    @Override
    public List<BankAccountDTO> getBankAccounts() {
        List<BankAccount> bankAccounts =  bankAccountRepository.findAll();

        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream()
                .map(bankAccount -> {
                    if(bankAccount instanceof SavingAccount){
                        return bankAccountMapper.fromSavingBankAccount((SavingAccount) bankAccount);
                    }
                    return bankAccountMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
                })
                .collect(Collectors.toList());

        return bankAccountDTOS;
    }
    private BankAccount getBankAccountById(String accountId) throws BankAccountNotFoundException{
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setStatus(AccountStatus.CREATED);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);
        return bankAccountMapper.fromCurrentBankAccount(savedCurrentAccount);
    }
    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)  throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");

        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setStatus(AccountStatus.CREATED);

        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);

        return bankAccountMapper.fromSavingBankAccount(savedSavingAccount);
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
            BankAccount bankAccount = getBankAccountById(accountId);

            if(bankAccount.getBalance() < amount)
                throw new BalanceNotSufficientException("Balance not sufficient");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAccount(bankAccount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);

        accountOperationRepository.save(accountOperation);

        // Debit operation
        log.info("Debit operation started");
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccountById(accountId);

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAccount(bankAccount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);

        accountOperationRepository.save(accountOperation);

        // Credit operation
        log.info("Credit operation started");
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        log.info("Transfer operation started");
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();

        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));
        Page<AccountOperation> accountOperations = accountOperationRepository.findByAccountId(accountId,PageRequest.of(page, size));

        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream()
                .map(accountOperation -> bankAccountMapper.fromAccountOperation(accountOperation))
                .toList();

        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountHistoryDTO.getTotalPages());

        return accountHistoryDTO;
    }

    @Override
    public List<AccountOperationDTO> getAccountOperations(String accountId){
        List<AccountOperation> operations = accountOperationRepository.findByAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = operations.stream()
                .map(operation -> bankAccountMapper.fromAccountOperation(operation))
                .toList();

        return accountOperationDTOS;
    }

    @Override
    public List<BankAccountDTO> getCustomerBankAccounts(Long customerId){
        List<BankAccountDTO> bankAccountDTOS = bankAccountRepository.findByCustomerId(customerId).stream().map(
                bankAccount -> {
                    if (bankAccount instanceof SavingAccount) {
                        SavingAccount savingAccount = (SavingAccount) bankAccount;
                        return bankAccountMapper.fromSavingBankAccount(savingAccount);
                    } else {
                        CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                        return bankAccountMapper.fromCurrentBankAccount(currentAccount);
                    }
                }).collect(Collectors.toList());

        return bankAccountDTOS;
    }

}
