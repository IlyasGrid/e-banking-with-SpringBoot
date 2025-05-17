package enset.ilyasgrid.ebankbackend;

import enset.ilyasgrid.ebankbackend.entities.AccountOperation;
import enset.ilyasgrid.ebankbackend.entities.CurrentAccount;
import enset.ilyasgrid.ebankbackend.entities.Customer;
import enset.ilyasgrid.ebankbackend.entities.SavingAccount;
import enset.ilyasgrid.ebankbackend.enums.AccountStatus;
import enset.ilyasgrid.ebankbackend.enums.OperationType;
import enset.ilyasgrid.ebankbackend.repositories.AccountOperationRepository;
import enset.ilyasgrid.ebankbackend.repositories.BankAccountRepository;
import enset.ilyasgrid.ebankbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EBankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBankBackendApplication.class, args);
    }
    @Bean
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
                    accountOperationRepository.save(new AccountOperation(null,  new java.util.Date(),Math.random() * 1000, OperationType.DEBIT, account));
                    accountOperationRepository.save(new AccountOperation(null,  new java.util.Date(),Math.random() * 1000, OperationType.CREDIT, account));
                }
            });
        };
    }
}
