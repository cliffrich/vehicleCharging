package com.sse.repository;

import com.sse.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    @Transactional()
    List<Account> findAll();
}
