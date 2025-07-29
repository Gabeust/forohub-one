package com.gabeust.forohub.repository;

import com.gabeust.forohub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User findUserByEmail (String email);
    boolean existsByEmail(String email);
    Optional<User> findByProfile_Nick(String nick);}

