package com.app.memberservice.loader;

import com.app.memberservice.entity.User;
import com.app.memberservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MemberDataLoader implements CommandLineRunner {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redis;

    public MemberDataLoader(UserRepository repo, PasswordEncoder encoder, StringRedisTemplate redis) {
        this.repo = repo;
        this.passwordEncoder = encoder;
        this.redis = redis;
    }


    @Override
    public void run(String... args) {

        // Prevent duplicate seeding
        if ("true".equals(redis.opsForValue().get("member_seed_done"))) {
            System.out.println("Members already seeded. Skipping...");
            return;
        }

//        if (repo.count() > 0) {
//            System.out.println("Members already exist in DB. Skipping insert.");
//            return;
//        }

        List<User> members = new ArrayList<>(5000);
        Random random = new Random();

        for (int i = 4001; i <= 5000; i++) {
            User m = new User();
            m.setUserName("User_" + i);
            m.setEmail("user" + i + "@gmail.com");
            m.setPassword(passwordEncoder.encode("password123"));
            m.setPhoneNumber("9" + (100000000 + random.nextInt(900000000)));
            members.add(m);
        }

        repo.saveAll(members);
        System.out.println("Inserted 5,000 members into DB");

        redis.opsForValue().set("member_seed_done", "true");
    }
}
