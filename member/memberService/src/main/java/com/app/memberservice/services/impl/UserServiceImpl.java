package com.app.memberservice.services.impl;

import com.app.memberservice.dto.LoginRequest;
import com.app.memberservice.dto.RegisterRequest;
import com.app.memberservice.entity.User;
import com.app.memberservice.repositories.UserRepository;
import com.app.memberservice.services.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
    }


    @Override
    public void register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.email()))
            throw new RuntimeException("User already exists");

        User user = new User();
        user.setUserName(req.userName());
        user.setEmail(req.email());
        user.setPassword(encoder.encode(req.password()));
        user.setPhoneNumber(req.phoneNumber());
        userRepository.save(user);
    }

    @Override
    public String login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(req.email())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
