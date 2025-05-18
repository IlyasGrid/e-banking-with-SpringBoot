package enset.ilyasgrid.ebankbackend.services;

import enset.ilyasgrid.ebankbackend.dtos.*;
import enset.ilyasgrid.ebankbackend.entities.BankAccount;
import enset.ilyasgrid.ebankbackend.entities.CurrentAccount;
import enset.ilyasgrid.ebankbackend.entities.Customer;
import enset.ilyasgrid.ebankbackend.entities.SavingAccount;
import enset.ilyasgrid.ebankbackend.exceptions.BalanceAccountInsufficientException;
import enset.ilyasgrid.ebankbackend.exceptions.BankAccountNotFoundException;
import enset.ilyasgrid.ebankbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer(Customer customer);
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceAccountInsufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceAccountInsufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<CustomerDTO> searchCustomers(String keyword);
    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
