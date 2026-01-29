package com.fitness.userservice.repository;

import com.fitness.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;



    private User testUser;

    @BeforeEach
    void beforeEach() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@gmail.com");
        testUser.setPassword("12345");
    }

    @Test
    @DisplayName("Should return true when email exists")
    void shouldReturnTrueWhenEmailExists() {

        //Given

        userRepository.save(testUser);

        //When
        boolean exists = userRepository.existsByEmail(testUser.getEmail());

        //Then
        assertTrue(exists, "Should return true when email exists");
    }

    @Test
    @DisplayName("Test when email does not exist")
    public void shouldReturnFalseWhenEmailDoesNotExist() {

        //Given

        userRepository.save(testUser);

        //When
        boolean exists = userRepository.existsByEmail("false@email.com");

        //Then
        assertFalse(exists, "Should return false when email does not exist");
    }
}
