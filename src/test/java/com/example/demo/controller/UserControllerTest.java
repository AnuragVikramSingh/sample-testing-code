package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsersTests {

        @Test
        @DisplayName("should return all users with status 200")
        void shouldReturnAllUsers() throws Exception {
            List<User> users = Arrays.asList(
                    new User(1L, "John Doe", "john.doe@example.com"),
                    new User(2L, "Jane Smith", "jane.smith@example.com"),
                    new User(3L, "Bob Johnson", "bob.johnson@example.com")
            );
            when(userService.getAllUsers()).thenReturn(users);

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("John Doe")))
                    .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                    .andExpect(jsonPath("$[2].id", is(3)))
                    .andExpect(jsonPath("$[2].name", is("Bob Johnson")));

            verify(userService, times(1)).getAllUsers();
        }

        @Test
        @DisplayName("should return empty list when no users exist")
        void shouldReturnEmptyListWhenNoUsers() throws Exception {
            when(userService.getAllUsers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(userService, times(1)).getAllUsers();
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserByIdTests {

        @Test
        @DisplayName("should return user when found by id")
        void shouldReturnUserWhenFound() throws Exception {
            User user = new User(1L, "John Doe", "john.doe@example.com");
            when(userService.getUserById(1L)).thenReturn(user);

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")));

            verify(userService, times(1)).getUserById(1L);
        }

        @Test
        @DisplayName("should return 200 with null body when user not found")
        void shouldReturnNullWhenUserNotFound() throws Exception {
            when(userService.getUserById(99L)).thenReturn(null);

            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isOk());

            verify(userService, times(1)).getUserById(99L);
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CreateUserTests {

        @Test
        @DisplayName("should create user and return 201 status")
        void shouldCreateUserAndReturn201() throws Exception {
            User inputUser = new User(null, "New User", "new.user@example.com");
            User createdUser = new User(4L, "New User", "new.user@example.com");
            when(userService.createUser(any(User.class))).thenReturn(createdUser);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputUser)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(4)))
                    .andExpect(jsonPath("$.name", is("New User")))
                    .andExpect(jsonPath("$.email", is("new.user@example.com")));

            verify(userService, times(1)).createUser(any(User.class));
        }

        @Test
        @DisplayName("should create user with only name provided")
        void shouldCreateUserWithOnlyName() throws Exception {
            User inputUser = new User(null, "Name Only", null);
            User createdUser = new User(4L, "Name Only", null);
            when(userService.createUser(any(User.class))).thenReturn(createdUser);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(4)))
                    .andExpect(jsonPath("$.name", is("Name Only")))
                    .andExpect(jsonPath("$.email", nullValue()));

            verify(userService, times(1)).createUser(any(User.class));
        }

        @Test
        @DisplayName("should create user with only email provided")
        void shouldCreateUserWithOnlyEmail() throws Exception {
            User inputUser = new User(null, null, "email@example.com");
            User createdUser = new User(4L, null, "email@example.com");
            when(userService.createUser(any(User.class))).thenReturn(createdUser);

            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputUser)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(4)))
                    .andExpect(jsonPath("$.email", is("email@example.com")));

            verify(userService, times(1)).createUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/{id}")
    class PatchUserTests {

        @Test
        @DisplayName("should update user name and return 200")
        void shouldUpdateUserNameAndReturn200() throws Exception {
            User patchData = new User(null, "Updated Name", null);
            User updatedUser = new User(1L, "Updated Name", "john.doe@example.com");
            when(userService.patchUser(eq(1L), any(User.class))).thenReturn(updatedUser);

            mockMvc.perform(patch("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchData)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Updated Name")))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")));

            verify(userService, times(1)).patchUser(eq(1L), any(User.class));
        }

        @Test
        @DisplayName("should update user email and return 200")
        void shouldUpdateUserEmailAndReturn200() throws Exception {
            User patchData = new User(null, null, "updated@example.com");
            User updatedUser = new User(1L, "John Doe", "updated@example.com");
            when(userService.patchUser(eq(1L), any(User.class))).thenReturn(updatedUser);

            mockMvc.perform(patch("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchData)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("updated@example.com")));

            verify(userService, times(1)).patchUser(eq(1L), any(User.class));
        }

        @Test
        @DisplayName("should update both name and email and return 200")
        void shouldUpdateBothFieldsAndReturn200() throws Exception {
            User patchData = new User(null, "New Name", "new@example.com");
            User updatedUser = new User(2L, "New Name", "new@example.com");
            when(userService.patchUser(eq(2L), any(User.class))).thenReturn(updatedUser);

            mockMvc.perform(patch("/api/users/2")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchData)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(2)))
                    .andExpect(jsonPath("$.name", is("New Name")))
                    .andExpect(jsonPath("$.email", is("new@example.com")));

            verify(userService, times(1)).patchUser(eq(2L), any(User.class));
        }

        @Test
        @DisplayName("should return 404 when user not found for patch")
        void shouldReturn404WhenUserNotFound() throws Exception {
            User patchData = new User(null, "Updated Name", null);
            when(userService.patchUser(eq(99L), any(User.class))).thenReturn(null);

            mockMvc.perform(patch("/api/users/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchData)))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).patchUser(eq(99L), any(User.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUserTests {

        @Test
        @DisplayName("should delete user and return 204 No Content")
        void shouldDeleteUserAndReturn204() throws Exception {
            when(userService.deleteUser(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isNoContent());

            verify(userService, times(1)).deleteUser(1L);
        }

        @Test
        @DisplayName("should return 404 when user not found for delete")
        void shouldReturn404WhenUserNotFoundForDelete() throws Exception {
            when(userService.deleteUser(99L)).thenReturn(false);

            mockMvc.perform(delete("/api/users/99"))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).deleteUser(99L);
        }
    }
}
