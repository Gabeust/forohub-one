package com.gabeust.forohub.service;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.service.interf.IUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio para operaciones sobre usuarios.
 *
 * Provee métodos para buscar, guardar, eliminar usuarios, y gestionar contraseñas.
 */
@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;

    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return lista de usuarios
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return Optional con el usuario si existe, vacío en caso contrario
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    /**
     * Guarda un usuario en la base de datos.
     *
     * @param user entidad usuario a guardar
     * @return usuario guardado con ID asignado
     */
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    /**
     * Elimina un usuario por su ID.
     *
     * Lanza una excepción si el usuario no existe.
     *
     * @param id identificador del usuario a eliminar
     */
    @Override
    public void deleteByid(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    /**
     * Encripta una contraseña usando BCrypt.
     *
     * @param password texto plano de la contraseña
     * @return contraseña encriptada
     */
    @Override
    public String encriptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
    /**
     * Busca un usuario por su email.
     *
     * @param email correo electrónico del usuario
     * @return usuario encontrado o null si no existe
     */
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email correo electrónico a verificar
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
