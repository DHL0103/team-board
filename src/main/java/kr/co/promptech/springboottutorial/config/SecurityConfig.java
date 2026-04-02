package kr.co.promptech.springboottutorial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .formLogin(
                        (formLogin) -> formLogin
                                .loginPage("/members/login")
                                .loginProcessingUrl("/members/login")  // POST 요청 처리
                                .failureHandler(authenticationFailureHandler())    // 실패 시 이 URL로
                                .defaultSuccessUrl("/boards", true)
                )
                .logout(
                        (logout) -> logout
                                .logoutUrl("/members/logout")
                                .logoutSuccessUrl("/members/login")
                                .invalidateHttpSession(true)
                )
                .authorizeHttpRequests(
                        (authorizeHttpRequests) ->  authorizeHttpRequests
                                .requestMatchers("/members/login", "/members/signup").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/img/**", "/fonts/**").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .anyRequest().hasAnyRole("USER", "ADMIN")
                )
                .sessionManagement(session -> session
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(-1)
                                .sessionRegistry(sessionRegistry())
                                .expiredUrl("/members/login?error=suspended")
                        )
                )
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            String redirectUrl;
            if (exception instanceof LockedException) {
                redirectUrl = "/members/login?error=suspended";
            } else {
                redirectUrl = "/members/login?error=true";
            }
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new  ProviderManager(authenticationProvider);
    }

}
