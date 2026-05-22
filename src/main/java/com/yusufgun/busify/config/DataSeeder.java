package com.yusufgun.busify.config;

import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import com.yusufgun.busify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {

        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == Role.ADMIN);

        if (!adminExists) {
            User rootAdmin = new User();
            rootAdmin.setFirstName("super");
            rootAdmin.setLastName("admin");
            rootAdmin.setEmail("admin@busify.com");
            rootAdmin.setTcNo("00000000000");
            rootAdmin.setPassword(passwordEncoder.encode("admin123"));
            rootAdmin.setRole(Role.ADMIN);

            userRepository.save(rootAdmin);

            System.out.println("\n>> [BUSIFY DATA SEEDER] No ADMIN found in the system. Root Admin created for initial setup!");
            System.out.println(">> Username: admin@busify.com | Password: admin123\n");
        }
    }
}
