package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        return Arrays.asList(
                new User(1L, "John Doe", "john.doe@example.com"),
                new User(2L, "Jane Smith", "jane.smith@example.com"),
                new User(3L, "Bob Johnson", "bob.johnson@example.com")
        );
    }

    @GetMapping("/{id}")
    public User getUserById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        List<User> users = Arrays.asList(
                new User(1L, "John Doe", "john.doe@example.com"),
                new User(2L, "Jane Smith", "jane.smith@example.com"),
                new User(3L, "Bob Johnson", "bob.johnson@example.com")
        );

        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
