package enset.ilyasgrid.ebankbackend;

import enset.ilyasgrid.ebankbackend.entities.*;
import enset.ilyasgrid.ebankbackend.enums.AccountStatus;
import enset.ilyasgrid.ebankbackend.enums.OperationType;
import enset.ilyasgrid.ebankbackend.exceptions.BalanceAccountInsufficient;
import enset.ilyasgrid.ebankbackend.exceptions.BankAccountNotFoundException;
import enset.ilyasgrid.ebankbackend.exceptions.CustomerNotFoundException;
import enset.ilyasgrid.ebankbackend.repositories.AccountOperationRepository;
import enset.ilyasgrid.ebankbackend.repositories.BankAccountRepository;
import enset.ilyasgrid.ebankbackend.repositories.CustomerRepository;
import enset.ilyasgrid.ebankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commmadLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Soufiane", "Abdelkrim", "Mohammed", "Hassan", "Yassine", "Ahmed").forEach(name -> {
                Customer customer = new Customer(null, name, name + "@gmail.com", null);
                bankAccountService.saveCustomer(customer);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                    List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
                    for (BankAccount account : bankAccounts) {
                        for (int i = 0; i < 10; i++) {
                            bankAccountService.credit(account.getId(), 1000 + Math.random() * 12000, "Credit");
                            bankAccountService.debit(account.getId(), 1000 + Math.random() * 9000, "Debit");
                        }
                    }
                } catch (CustomerNotFoundException | BankAccountNotFoundException | BalanceAccountInsufficient e) {
                    e.printStackTrace();
                }
            });
        };
    }
//    @Bean
    CommandLineRunner start(BankAccountRepository bankAccountRepository, CustomerRepository customerRepository, AccountOperationRepository accountOperationRepository) {
        return args -> {
            // Initialize your data here if needed
            Stream.of(
                    "Customer 1",
                    "Customer 2",
                    "Customer 3"
            ).forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name.toLowerCase().replace(" ", "") + "@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount account = new CurrentAccount();
                account.setId(UUID.randomUUID().toString());
                account.setBalance(Math.random() * 10000);
                account.setCreatedAt(new java.util.Date());
                account.setStatus(AccountStatus.Created);
                account.setCustomer(customer);
                account.setOverdraft(1000);
                bankAccountRepository.save(account);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 10000);
                savingAccount.setCreatedAt(new java.util.Date());
                savingAccount.setStatus(AccountStatus.Created);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(account -> {
                for (int i = 0; i < 10; i++) {
                    accountOperationRepository.save(new AccountOperation(null,  new java.util.Date(),Math.random() * 1000, OperationType.DEBIT, account, "Debit Operation"));
                    accountOperationRepository.save(new AccountOperation(null,  new java.util.Date(),Math.random() * 1000, OperationType.CREDIT, account, "Credit Operation"));
                }
            });
        };
    }
}
