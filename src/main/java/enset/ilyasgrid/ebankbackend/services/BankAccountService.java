package enset.ilyasgrid.ebankbackend.services;

import enset.ilyasgrid.ebankbackend.entities.BankAccount;
import enset.ilyasgrid.ebankbackend.entities.CurrentAccount;
import enset.ilyasgrid.ebankbackend.entities.Customer;
import enset.ilyasgrid.ebankbackend.entities.SavingAccount;
import enset.ilyasgrid.ebankbackend.exceptions.BalanceAccountInsufficient;
import enset.ilyasgrid.ebankbackend.exceptions.BankAccountNotFoundException;
import enset.ilyasgrid.ebankbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer(Customer customer);
    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<Customer> listCustomers();
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceAccountInsufficient;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BalanceAccountInsufficient, BankAccountNotFoundException;

    List<BankAccount> bankAccountList();
}
