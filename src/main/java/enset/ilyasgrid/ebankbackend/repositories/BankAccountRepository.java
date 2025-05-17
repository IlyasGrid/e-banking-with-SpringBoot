package enset.ilyasgrid.ebankbackend.repositories;

import enset.ilyasgrid.ebankbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {

}
