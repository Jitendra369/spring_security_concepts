package security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import security.auth_provider.CustomAuthProvider;
import security.service.UserDetailsServiceImple;

@Configuration
@RequiredArgsConstructor
public class MyConfig {

//    @Autowired
//    public final UserDetailsServiceImple customUserDetailsService;

//    @Bean
//   public AuthenticationProvider authenticationProvider(){
//       DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//       authenticationProvider.setUserDetailsService(customUserDetailsService);
//       authenticationProvider.setPasswordEncoder(passwordEncoder());
//       return authenticationProvider;
//   }

//     This is use DAO Authentication
//    @Bean
//    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder){
//        return new CustomAuthProvider(customUserDetailsService, passwordEncoder);
//    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        UserDetails userDetails = User.builder()
//                .username("android")
//                .password(passwordEncoder().encode("password"))
//                .roles("ADMIN").build();
//
//        return new InMemoryUserDetailsManager(userDetails);
//
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         return http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/public/**").permitAll();
                    request.requestMatchers("/users")
                            .hasAnyAuthority("USER", "ADMIN");
                    request.anyRequest().authenticated();
                })
                 .httpBasic(Customizer.withDefaults()).build();
//                 .formLogin(Customizer.withDefaults()).build()

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LdapTemplate ldapContextSource(){
        return new LdapTemplate(contextSource());
    }

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl("ldap://localhost:10389");
        ldapContextSource.setUserDn("uid=admin,ou=system");
        ldapContextSource.setPassword("secret");
        return ldapContextSource;
    }

    @Bean
    public AuthenticationManager authManager(BaseLdapPathContextSource baseLdapPathContextSource){
        LdapBindAuthenticationManagerFactory factory = new LdapBindAuthenticationManagerFactory(baseLdapPathContextSource);
        factory.setUserDnPatterns("cn={0},ou=users,ou=system");
        return factory.createAuthenticationManager();
    }
}
