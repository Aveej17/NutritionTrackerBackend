package com.jeeva.calorietrackerbackend.repository;

import com.jeeva.calorietrackerbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    Optional<User> findByEmail(String email);

    @Query(value = "Select * from users WHERE email= :email", nativeQuery = true)
    User findUserByEmailNative(@Param("email") String email);
}
