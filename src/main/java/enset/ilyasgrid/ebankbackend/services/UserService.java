package enset.ilyasgrid.ebankbackend.services;

import enset.ilyasgrid.ebankbackend.dtos.UserRegistrationDTO;
import enset.ilyasgrid.ebankbackend.entities.User;

public interface UserService {
    User registerUser(UserRegistrationDTO registrationDTO);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void fixUserAccountStatus();
}
