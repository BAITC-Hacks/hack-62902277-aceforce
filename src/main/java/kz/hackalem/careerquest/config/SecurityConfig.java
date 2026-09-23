package kz.hackalem.careerquest.config;

import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(JdbcTemplate jdbc) {
    return username ->
        jdbc
            .query(
                "SELECT username,password_hash,authority FROM app_user WHERE username=?",
                (r, n) ->
                    User.withUsername(r.getString(1))
                        .password(r.getString(2))
                        .authorities(r.getString(3))
                        .build(),
                username)
            .stream()
            .findFirst()
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            a ->
                a.requestMatchers("/login", "/css/**", "/js/**", "/error")
                    .permitAll()
                    .requestMatchers("/hr/**", "/api/hr/**", "/api/data/**")
                    .hasRole("HR")
                    .anyRequest()
                    .authenticated())
        .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
        .logout(l -> l.logoutSuccessUrl("/login?logout"))
        .build();
  }
}
