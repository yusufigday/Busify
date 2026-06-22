package com.yusufgun.busify.repository;

import com.yusufgun.busify.entity.User;
import com.yusufgun.busify.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByTcNo(String tcNo);

    Optional<User> findByTcNo(String tcNo);

    Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);
}
