package com.gabeust.forohub.service.interf;

import com.gabeust.forohub.entity.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {
    List<Role> findAll();
    Optional<Role> findById(Long id);
    Role save(Role role);
}
