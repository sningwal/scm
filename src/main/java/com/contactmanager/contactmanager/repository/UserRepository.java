package com.contactmanager.contactmanager.repository;

import com.contactmanager.contactmanager.entity.User;
import com.contactmanager.contactmanager.entity.type.AuthProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByProviderIdAndProviderType(String providerId, AuthProviderType providerType);
}
