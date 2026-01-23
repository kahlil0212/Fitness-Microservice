package com.fitness.user_service.service;

import com.fitness.user_service.dto.RegisterRequest;
import com.fitness.user_service.dto.UserResponse;
import com.fitness.user_service.model.User;
import com.fitness.user_service.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public UserResponse register(@Valid RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User newUser = modelMapper.map(request,User.class);

        User savedUser = userRepository.save(newUser);

        return modelMapper.map(savedUser,UserResponse.class);
    }

    public UserResponse getUserProfile(String userId) {

        User retrievedUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));


        return modelMapper.map(retrievedUser,UserResponse.class);
    }

    public Boolean existsByUserId(String userId) {

        return userRepository.existsById(userId);
    }
}
