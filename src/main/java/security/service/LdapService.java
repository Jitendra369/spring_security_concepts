package security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    public List<String> getAllNames(){
        String base = "ou=users,ou=system";
        String filter = "(ObjectClass=person)";

        return ldapTemplate.search(base, filter, new AttributesMapper<String>() {
            @Override
            public String mapFromAttributes(Attributes attributes) throws NamingException {
                return attributes.get("uid").get().toString();
            }
        });
    }
}
