package kr.co.promptech.springboottutorial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .formLogin(
                        (formLogin) -> formLogin
                                .loginPage("/member/login")
                                .loginProcessingUrl("/member/login")  // POST 요청 처리
                                .failureUrl("/member/login?error=true")    // 실패 시 이 URL로
                                .defaultSuccessUrl("/board")
                )
                .logout(
                        (logout) -> logout
                                .logoutUrl("/member/logout")
                                .logoutSuccessUrl("/member/login")
                                .invalidateHttpSession(true)
                )
                .authorizeHttpRequests(
                        (authorizeHttpRequests) ->  authorizeHttpRequests
                                .requestMatchers("/member/login").permitAll()
                                .requestMatchers("/css/**", "/js/**").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf((csrf) -> csrf.disable()) // 테스트 시에는 CSRF를 꺼두어야 Postman POST 요청이 잘 들어갑니다.
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
