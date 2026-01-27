package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;


    private final ModelMapper modelMapper;

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

        log.info("Calling user exists by userId validation method {} ", userId);
        return userRepository.existsById(userId);
    }
}
