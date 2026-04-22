package org.booknest.auth.repo;

import org.booknest.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepo extends JpaRepository<User,Long>{
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
