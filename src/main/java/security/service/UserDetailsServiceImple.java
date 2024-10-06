package security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import security.model.User;
import security.repo.UserRepo;

import java.util.ArrayList;
import java.util.Optional;

//todo : uncomment for DAO authentication
//@Component
@RequiredArgsConstructor
public class UserDetailsServiceImple implements UserDetailsService {

    private final UserRepo userRepo;

//     what if we have thumb scan for user valiadtion
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()){
            User savedUser = userOptional.get();
// Note
            /*public User(String username, String password, Collection<? extends GrantedAuthority > authorities) {
                this(username, password, true, true, true, true, authorities);
            }*/

            return new org.springframework.security.core.userdetails.User(
                    savedUser.getUsername(),
                    savedUser.getPassword(),
                    new ArrayList<>()
                    );
//            return userOptional.get();
        }else {
            return null;
        }
    }
}
