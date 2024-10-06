package security.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.dto.LdapUserDto;
import security.service.LdapService;

import javax.naming.NameAlreadyBoundException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ldap")
public class LdapServiceController {

    private final LdapService ldapService;

    @GetMapping
    public List<LdapUserDto> getAllLdapUsers() {
        return ldapService.getAllUserInformation();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody LdapUserDto ldapUserDto) {
        ldapService.createUser(ldapUserDto);
//        try {
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        } catch (NameAlreadyBoundException e) {
//            throw new UserNameAlreadyExistsException("username already exists", e);
//        }s
        return ResponseEntity.ok("user created ");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    class UserNameAlreadyExistsException extends RuntimeException{
        public UserNameAlreadyExistsException(String message, Throwable cause){
            super(message, cause);
        }
    }
}
