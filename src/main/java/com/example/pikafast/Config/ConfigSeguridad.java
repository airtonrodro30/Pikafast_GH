package com.example.pikafast.Config;

import com.example.pikafast.Servicio.UserDetailsServiceImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class ConfigSeguridad {

    private final UserDetailsServiceImp userDetailsService;

    //Constructor
    public ConfigSeguridad(UserDetailsServiceImp userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AuthenticationSuccessHandler successHandlerOK() {
        return (request, response, authentication) -> {
            response.sendRedirect("/");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Autenticación: usa mi UserDetailsService para cargar usuarios desde BD
                .userDetailsService(userDetailsService) // <-- Enlaza al userDetailsService
                .authorizeHttpRequests(auth -> auth
                // Permite que cualquiera entre al registro y login sin autenticarse
                .requestMatchers("/assets/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/registrar", "/login", "/").permitAll()
                .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "USER")
                // Cualquier otra ruta requerirá inicio de sesión
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login") // Tu vista de login personalizada
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandlerOK()) // Al login exitoso, ejecuta redirección
                .failureUrl("/login?error=true") // Cuando Exiten Errores al iniciar Sesión
                .permitAll()
                );

        return http.build();
    }
}
