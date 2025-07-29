package com.gabeust.forohub.config;


import com.gabeust.forohub.entity.Role;
import com.gabeust.forohub.entity.User;
import com.gabeust.forohub.repository.IUserRepository;
import com.gabeust.forohub.service.RoleServiceImpl;
import com.gabeust.forohub.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
/**
 * Manejador personalizado para el éxito de autenticación OAuth2.
 *
 * Se encarga de realizar las siguientes acciones cuando un usuario inicia sesión con OAuth2:
 * - Verifica si el usuario ya existe en la base de datos.
 * - Si no existe, lo crea y le asigna el rol "USER".
 * - Genera un token JWT utilizando el correo electrónico del usuario.
 * - Redirige al frontend con el token JWT como parámetro en la URL.
 *
 * Se utiliza como parte del flujo de autenticación OAuth2 con Spring Security.
 */
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final IUserRepository userRepository;
    private final RoleServiceImpl roleService;
    private final PasswordEncoder passwordEncoder;


    /**
     * Método invocado automáticamente cuando la autenticación OAuth2 es exitosa.
     *
     * @param request       La solicitud HTTP.
     * @param response      La respuesta HTTP.
     * @param authentication El objeto de autenticación proporcionado por Spring Security.
     * @throws IOException En caso de error al redirigir.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();


        String email = oauthUser.getAttribute("email");
        if (email == null || email.isEmpty()) {

            throw new RuntimeException("Email not found in OAuth2User");
        }

        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("oauth2user"));

            Role userRole = roleService.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            user.setRolesList(Set.of(userRole));

            user = userRepository.save(user);

        }

        String jwt = jwtUtils.createTokenFromEmail(user.getEmail());

        // Redirige al frontend con el JWT como parámetro
        String redirectUrl = "http://localhost:5500/?token=" + jwt;

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}