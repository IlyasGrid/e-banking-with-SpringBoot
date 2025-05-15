package enset.ilyasgrid.ebankbackend;

import enset.ilyasgrid.ebankbackend.entities.Customer;
import enset.ilyasgrid.ebankbackend.repositories.AccountOperationRepository;
import enset.ilyasgrid.ebankbackend.repositories.BankAccountRepository;
import enset.ilyasgrid.ebankbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;

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
        };
    }
}
