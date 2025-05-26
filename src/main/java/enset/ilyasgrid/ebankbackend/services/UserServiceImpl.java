package enset.ilyasgrid.ebankbackend.services;

import enset.ilyasgrid.ebankbackend.dtos.UserRegistrationDTO;
import enset.ilyasgrid.ebankbackend.entities.User;
import enset.ilyasgrid.ebankbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserRegistrationDTO registrationDTO) {
        if (existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(registrationDTO.getUsername())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .email(registrationDTO.getEmail())
                .fullName(registrationDTO.getFullName())
                .role(User.Role.USER)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void fixUserAccountStatus() {
        // Fix any existing users that might have null or false account status flags
        userRepository.findAll().forEach(user -> {
            boolean needsUpdate = false;

            if (!user.isEnabled()) {
                user.setEnabled(true);
                needsUpdate = true;
            }
            if (!user.isAccountNonExpired()) {
                user.setAccountNonExpired(true);
                needsUpdate = true;
            }
            if (!user.isAccountNonLocked()) {
                user.setAccountNonLocked(true);
                needsUpdate = true;
            }
            if (!user.isCredentialsNonExpired()) {
                user.setCredentialsNonExpired(true);
                needsUpdate = true;
            }

            if (needsUpdate) {
                userRepository.save(user);
                System.out.println("Fixed account status for user: " + user.getUsername());
            }
        });
    }
}
