package com.InfoRefineria.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
                    // Forzar configuración de cookies aquí
                    session.sessionFixation().none();
                })
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login.html", "/login.css", "/img/**", "/api/login",
                            "/imagenes/**", "/Sector.html", "/api/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig.disable())

                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList(
                "http://127.0.0.1:5500",
                "http://localhost:5500" ,
                "http://10.0.0.50",
                "http://localhost:8080"
        ));

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));

        //Esto permite que las cookies/sesión viajen (credentials: "include")
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("administrador")
                .password(passwordEncoder().encode("Refi$7880"))
                .roles("ADMIN")
                .build();
        UserDetails user1 = User.withUsername("ggonzalez")
                .password(passwordEncoder().encode("germang"))
                .roles("ADMIN")
                .build();
        UserDetails user2 = User.withUsername("panelistas")
                .password(passwordEncoder().encode("p1234"))
                .roles("ADMIN")
                .build();
        UserDetails user3 = User.withUsername("envase")
                .password(passwordEncoder().encode("env1234"))
                .roles("ADMIN")
                .build();
        UserDetails user4 = User.withUsername("fusion")
                .password(passwordEncoder().encode("f1234"))
                .roles("ADMIN")
                .build();
        UserDetails user5 = User.withUsername("levadura")
                .password(passwordEncoder().encode("leva1234"))
                .roles("ADMIN")
                .build();
        UserDetails user6 = User.withUsername("produccionvm")
                .password(passwordEncoder().encode("prod.1234"))
                .roles("ADMIN")
                .build();
        UserDetails user7 = User.withUsername("mantenimientovm")
                .password(passwordEncoder().encode("mant.9696"))
                .roles("ADMIN")
                .build();
        UserDetails user8 = User.withUsername("planeamiento")
                .password(passwordEncoder().encode("plan$4253"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, user1, user2, user3, user4, user5, user6, user7, user8);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

