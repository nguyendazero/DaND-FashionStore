//package com.haibazo_bff_its_rct_webapi.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        // Tạo một user trong bộ nhớ với username/password đã mã hóa
//        return new InMemoryUserDetailsManager(
//                User.withUsername("haibazo")
//                        .password(passwordEncoder.encode("mothaibazo"))
//                        .roles("USER") // Gán quyền cho user
//                        .build()
//        );
//    }
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Tắt CSRF nếu không cần thiết
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/public/**").permitAll() // Cho phép endpoint public không cần login
//                        .anyRequest().authenticated() // Các request khác yêu cầu login
//                )
//                .formLogin(form -> form.permitAll()) // Kích hoạt form login
//                .logout(logout -> logout.permitAll()); // Cho phép logout
//
//        return http.build();
//    }
//}
