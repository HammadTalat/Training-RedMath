package com.example.newsmanagementsystem.user.repository;

import com.example.newsmanagementsystem.user.entity.AppUser;
import com.example.newsmanagementsystem.user.entity.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByAuthProviderAndProviderUserId(
            AuthProvider authProvider,
            String providerUserId
    );
    Optional<AppUser> findByUsernameIgnoreCase(String username);

    Optional<AppUser> findByAccessToken(String token);
}
