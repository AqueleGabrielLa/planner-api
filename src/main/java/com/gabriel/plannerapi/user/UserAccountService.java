package com.gabriel.plannerapi.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository userRepository;

    public UserAccount save(UserAccount user){
        return userRepository.save(user);
    }

    public Optional<UserAccount> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<UserAccount> findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
