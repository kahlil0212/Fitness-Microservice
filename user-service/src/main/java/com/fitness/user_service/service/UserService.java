package com.fitness.user_service.service;

import com.fitness.user_service.dto.RegisterRequest;
import com.fitness.user_service.dto.UserResponse;
import com.fitness.user_service.model.User;
import com.fitness.user_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse register(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User newUser = new User();

        //TODO: Use ModelMapper pattern
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());

        User savedUser = userRepository.save(newUser);

        UserResponse userResponse = new UserResponse();

        //TODO: Use ModelMapper pattern
        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setPassword(savedUser.getPassword());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedDate(savedUser.getCreatedDate());
        userResponse.setUpdatedDate(savedUser.getUpdatedDate());

        return userResponse;
    }

    public UserResponse getUserProfile(String userId) {

        User retrievedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        //TODO: Use ModelMapper pattern
        UserResponse userResponse = new UserResponse();
        userResponse.setId(retrievedUser.getId());
        userResponse.setEmail(retrievedUser.getEmail());
        userResponse.setPassword(retrievedUser.getPassword());
        userResponse.setFirstName(retrievedUser.getFirstName());
        userResponse.setLastName(retrievedUser.getLastName());
        userResponse.setCreatedDate(retrievedUser.getCreatedDate());
        userResponse.setUpdatedDate(retrievedUser.getUpdatedDate());

        return userResponse;
    }
}
