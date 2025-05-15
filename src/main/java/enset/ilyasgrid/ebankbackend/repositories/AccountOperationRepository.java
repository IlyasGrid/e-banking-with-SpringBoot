package enset.ilyasgrid.ebankbackend.repositories;

import enset.ilyasgrid.ebankbackend.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
    // Custom query methods can be defined here if needed
}
