package com.sse.repository;

import com.sse.domain.Session;
import com.sse.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
