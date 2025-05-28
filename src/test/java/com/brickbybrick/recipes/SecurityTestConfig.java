//package com.brickbybrick.recipes;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@TestConfiguration
//class SecurityTestConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.POST, "/ingredient").authenticated()
//                        .requestMatchers(HttpMethod.PUT, "/ingredient/**").authenticated()
//                        .requestMatchers(HttpMethod.DELETE, "/ingredient/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/ingredient/2").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/ingredient?category=**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET, "/ingredient").permitAll()
//                        .anyRequest().permitAll()
//                )
//                .httpBasic(Customizer.withDefaults())
//                .csrf(AbstractHttpConfigurer::disable);
//        return http.build();
//    }
//}
