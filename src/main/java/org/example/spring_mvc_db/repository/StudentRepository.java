package org.example.spring_mvc_db.repository;

import org.example.spring_mvc_db.entities.Students;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Students, Long> {

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<Students> findByEmail(String email);
}
