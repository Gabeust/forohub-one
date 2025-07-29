package com.gabeust.forohub.controller;

import com.gabeust.forohub.entity.Role;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.service.RoleServiceImpl;
import com.gabeust.forohub.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controlador REST para operaciones sobre usuarios del sistema.
 *
 * Provee endpoints para listar todos los usuarios, obtener uno por ID y crear un nuevo usuario.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;

    public UserController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * @return lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.findAll());
    }
    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return el usuario si existe, o 404 si no se encuentra
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
    /**
     * Crea un nuevo usuario con roles asignados.
     *
     * Valida que el email no esté en uso, la contraseña no esté vacía y los roles existan.
     *
     * @param user objeto usuario a crear
     * @return usuario creado sin exponer contraseña
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        // Valida si el email ya está en uso
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already registered.");
        }

        // Valida que la contraseña no esté vacía
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password cannot be empty.");
        }

        // Encripta contraseña
        user.setPassword(userService.encriptPassword(user.getPassword()));

        // Verificar y asigna roles válidos
        Set<Role> roleList = new HashSet<>();
        for (Role role : user.getRolesList()) {
            roleService.findById(role.getId()).ifPresent(roleList::add);
        }

        if (roleList.isEmpty()) {
            return ResponseEntity.badRequest().body("The user must have at least one valid role.");
        }

        user.setRolesList(roleList);

        // Guardar usuario
        User newUser = userService.save(user);

        // Retornar una respuesta segura sin exponer la contraseña
        return ResponseEntity.ok(Map.of(
                "id", newUser.getId(),
                "email", newUser.getEmail(),
                "roles", newUser.getRolesList().stream().map(Role::getName).toList(),
                "message", "User created successfully."
        ));
    }

}
