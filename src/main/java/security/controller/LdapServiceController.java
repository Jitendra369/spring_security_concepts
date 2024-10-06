package security.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import security.dto.LdapUserDto;
import security.service.LdapService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ldap")
public class LdapServiceController {

    private final LdapService ldapService;

    @GetMapping
    public List<LdapUserDto> getAllLdapUsers(){
        return ldapService.getAllUserInformation();
    }
}
