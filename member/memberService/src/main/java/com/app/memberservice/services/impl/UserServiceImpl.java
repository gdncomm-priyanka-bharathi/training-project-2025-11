package com.app.memberservice.services.impl;

import com.app.memberservice.client.CartClient;
import com.app.memberservice.dto.LoginRequest;
import com.app.memberservice.dto.LoginResponse;
import com.app.memberservice.dto.RegisterRequest;
import com.app.memberservice.dto.UserResponse;
import com.app.memberservice.entity.User;
import com.app.memberservice.exceptions.*;
import com.app.memberservice.repositories.UserRepository;
import com.app.memberservice.security.JwtService;
import com.app.memberservice.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StringRedisTemplate redis;
    private final CartClient cartClient;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService, StringRedisTemplate redis, CartClient cartClient, CartClient cartClient1) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redis = redis;
        this.cartClient = cartClient1;
    }

    public UserResponse register(RegisterRequest request) {
        // Check if email already exists
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new UserAlreadyExists("USER_ALREADY_EXIST");
                });

        // Map RegisterRequest → User
        User user = new User();
        BeanUtils.copyProperties(request, user);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Save
        User saved = userRepository.save(user);

        // Convert User → UserResponse
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(saved, response);

        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> optional = userRepository.findByEmail(request.getEmail());
        if (optional.isEmpty()) {
            throw new InvalidUserOrPassword("INVALID_CREDENTIALS");
        }

        User user = optional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidUserOrPassword("INVALID_CREDENTIALS");
        }

        String userId = user.getId().toString();
        String redisKey = "LOGIN:" + userId;

        //Check if user already logged in
        String existingToken = redis.opsForValue().get(redisKey);
        if (existingToken != null) {
            throw new UserAlreadyLoggedInException("USER_ALREADY_LOGGED_IN");
        }

        String token = jwtService.generateToken(user.getId().toString());

        // Store token in Redis (login session)
        redis.opsForValue().set(redisKey, token);

        redis.opsForValue().set(redisKey, token, Duration.ofHours(1));


        UserResponse memberResponse = new UserResponse();
        BeanUtils.copyProperties(user, memberResponse);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setMember(memberResponse);
        return loginResponse;
    }

    public void logout(String userId) {
        String key = "LOGIN:" + userId;

        if (redis.opsForValue().get(key) == null) {
            throw new UserAlreadyLoggedOutException("User already logged out or never logged in");
        }

        redis.delete(key);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new UserNotFoundException("USER_DOES_NOT_EXIST"));

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }


    public void deleteUser(String userId) {
        userRepository.deleteById(Long.valueOf(userId));
        cartClient.deleteCart(userId);
    }




}
