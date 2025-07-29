package com.gabeust.forohub.service;

import com.gabeust.forohub.entity.Role;
import com.gabeust.forohub.repository.IRoleRepository;
import com.gabeust.forohub.service.interf.IRoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio para gestionar roles de usuario.
 *
 * Proporciona métodos para buscar, guardar y consultar roles mediante el repositorio.
 */
@Service
public class RoleServiceImpl implements IRoleService {

    private final IRoleRepository roleRepository;

    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Devuelve todos los roles disponibles.
     *
     * @return lista de roles
     */
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    /**
     * Busca un rol por su ID.
     *
     * @param id ID del rol
     * @return Optional con el rol si existe
     */
    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * Guarda un nuevo rol o actualiza uno existente.
     *
     * @param role objeto Role a guardar
     * @return el rol guardado
     */
    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }
    /**
     * Busca un rol por su nombre.
     *
     * @param name nombre del rol
     * @return Optional con el rol si existe
     */
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
