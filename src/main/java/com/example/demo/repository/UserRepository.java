package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final List<User> users = Arrays.asList(
            new User(1L, "John Doe", "john.doe@example.com"),
            new User(2L, "Jane Smith", "jane.smith@example.com"),
            new User(3L, "Bob Johnson", "bob.johnson@example.com")
    );

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
}
