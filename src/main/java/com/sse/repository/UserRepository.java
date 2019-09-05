package com.sse.repository;

import com.sse.domain.Account;
import com.sse.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<List<User>> findUserssByAccountEquals(Account account);
}
