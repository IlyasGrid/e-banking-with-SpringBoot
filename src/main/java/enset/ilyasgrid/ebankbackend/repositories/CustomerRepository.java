package enset.ilyasgrid.ebankbackend.repositories;

import enset.ilyasgrid.ebankbackend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
