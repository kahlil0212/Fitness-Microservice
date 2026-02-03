package com.fitness.userservice.service;

import com.fitness.userservice.config.ModelMapperConfig;
import com.fitness.userservice.config.SecurityConfig;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private ModelMapper modelMapper;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initialize config ModelMapper and set to modelMapper
        ModelMapperConfig modelMapperConfig = new ModelMapperConfig();
        modelMapper = modelMapperConfig.modelMapper();

        SecurityConfig securityConfig = new SecurityConfig();
        bCryptPasswordEncoder = securityConfig.bCryptPasswordEncoder();


        // Create service with mocked repository and real ModelMapper
        userService = new UserService(userRepository, bCryptPasswordEncoder, modelMapper);
    }

    // ============================================
    // REGISTER TESTS
    // ============================================

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUser_WhenEmailDoesNotExist() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Doe");

        User savedUser = new User();
        savedUser.setId("user-123");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("password123");
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse result = userService.register(request);

        // Then
        assertNotNull(result);
        assertEquals("user-123", result.getId());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should map RegisterRequest to User correctly before saving")
    void shouldMapRegisterRequestToUser_BeforeSaving() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jane@example.com");
        request.setPassword("securePass123");
        request.setFirstName("Jane");
        request.setLastName("Smith");

        User savedUser = new User();
        savedUser.setId("user-456");
        savedUser.setEmail("jane@example.com");
        savedUser.setPassword("securePass123");
        savedUser.setFirstName("Jane");
        savedUser.setLastName("Smith");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        userService.register(request);

        // Then - Capture the User object passed to save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertNull(capturedUser.getId()); // ID should be null before save
        assertEquals("jane@example.com", capturedUser.getEmail());
        assertEquals("Jane", capturedUser.getFirstName());
        assertEquals("Smith", capturedUser.getLastName());
    }

    @Test
    @DisplayName("Should return UserResponse with all mapped fields")
    void shouldReturnUserResponse_WithAllFieldsMapped() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setFirstName("Test");
        request.setLastName("User");

        User savedUser = new User();
        savedUser.setId("user-789");
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("password");
        savedUser.setFirstName("Test");
        savedUser.setLastName("User");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse result = userService.register(request);

        // Then
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        assertEquals(savedUser.getFirstName(), result.getFirstName());
        assertEquals(savedUser.getLastName(), result.getLastName());
        // Password should NOT be in response (assuming UserResponse doesn't have it)
    }

    // ============================================
    // GET USER PROFILE TESTS
    // ============================================

    @Test
    @DisplayName("Should return user profile when user exists")
    void shouldReturnUserProfile_WhenUserExists() {
        // Given
        String userId = "user-123";

        User user = new User();
        user.setId(userId);
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("hashedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserResponse result = userService.getUserProfile(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowException_WhenUserNotFound() {
        // Given
        String userId = "non-existent-id";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserProfile(userId);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should map User to UserResponse correctly")
    void shouldMapUserToUserResponse_Correctly() {
        // Given
        String userId = "user-456";

        User user = new User();
        user.setId(userId);
        user.setEmail("jane@example.com");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPassword("hashedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserResponse result = userService.getUserProfile(userId);

        // Then
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        // Verify password is not in response (if UserResponse doesn't have password field)
    }

    // ============================================
    // EXISTS BY USER ID TESTS
    // ============================================

    @Test
    @DisplayName("Should return true when user exists by ID")
    void shouldReturnTrue_WhenUserExistsById() {
        // Given
        String userId = "user-123";

        when(userRepository.existsByKeyCloakId(userId)).thenReturn(true);

        // When
        Boolean result = userService.existsByUserId(userId);

        // Then
        assertTrue(result);

        verify(userRepository).existsByKeyCloakId(userId);
    }

    @Test
    @DisplayName("Should return false when user does not exist by ID")
    void shouldReturnFalse_WhenUserDoesNotExistById() {
        // Given
        String userId = "non-existent-id";

        when(userRepository.existsByKeyCloakId(userId)).thenReturn(false);

        // When
        Boolean result = userService.existsByUserId(userId);

        // Then
        assertFalse(result);

        verify(userRepository).existsByKeyCloakId(userId);
    }

    @Test
    @DisplayName("Should call repository existsByKeyCloakId with correct userId")
    void shouldCallRepositoryExistsByKeyCloakId_WithCorrectUserId() {
        // Given
        String userId = "user-789";

        when(userRepository.existsByKeyCloakId(userId)).thenReturn(true);

        // When
        userService.existsByUserId(userId);

        // Then
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).existsByKeyCloakId(userIdCaptor.capture());

        assertEquals(userId, userIdCaptor.getValue());
    }

    // ============================================
    // EDGE CASE TESTS
    // ============================================

    @Test
    @DisplayName("Should handle null email in register request")
    void shouldHandleNullEmail_InRegisterRequest() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail(null);
        request.setPassword("password123");

        when(userRepository.existsByEmail(null)).thenReturn(false);

        User savedUser = new User();
        savedUser.setId("user-123");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse result = userService.register(request);

        // Then
        assertNotNull(result);
        verify(userRepository).existsByEmail(null);
    }

    @Test
    @DisplayName("Should verify repository methods are called exactly once")
    void shouldVerifyRepositoryMethodsCalled_ExactlyOnce() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");

        User savedUser = new User();
        savedUser.setId("user-123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        userService.register(request);

        // Then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should handle empty userId in getUserProfile")
    void shouldHandleEmptyUserId_InGetUserProfile() {
        // Given
        String emptyUserId = "";

        when(userRepository.findById(emptyUserId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.getUserProfile(emptyUserId);
        });

        verify(userRepository).findById(emptyUserId);
    }
}