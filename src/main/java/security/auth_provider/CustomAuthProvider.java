package security.auth_provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import security.service.UserDetailsServiceImple;

//@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthProvider implements AuthenticationProvider {


    private final UserDetailsServiceImple userDetailsServiceImple;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userProvidedPassword = authentication.getCredentials().toString();
        log.info("User details: " + authentication.getDetails());
        var user = userDetailsServiceImple.loadUserByUsername(authentication.getName());
        if (user != null && passwordEncoder.matches(userProvidedPassword, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials());
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
