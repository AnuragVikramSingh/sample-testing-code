package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User createUser(User user) {
        Long nextId = userRepository.findAll().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L) + 1;
        user.setId(nextId);
        return userRepository.save(user);
    }

    public boolean deleteUser(Long id) {
        return userRepository.deleteById(id);
    }

    public User patchUser(Long id, User user) {
        Optional<User> updatedUser = userRepository.update(id, user);
        return updatedUser.orElse(null);
    }
}
