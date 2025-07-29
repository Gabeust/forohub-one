package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    User save(User user);
    void deleteByid(Long id);
    String encriptPassword(String password);
    User findUserByEmail(String email);
    boolean existsByEmail(String email);
}
