package security.service;

import org.antlr.v4.runtime.atn.AtomTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import security.dto.LdapUserDto;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
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
                return attributes.get("cn").get().toString();
            }
        });
    }

    public List<LdapUserDto> getAllUserInformation(){
        String base = "ou=users,ou=system";
        String filter = "(ObjectClass=person)";
//        cn: vikas
//        sn: gupta
//        objectClass: inetOrgPerson
//        objectClass: organizationalPerson
//        objectClass: person
//        objectClass: top

        return ldapTemplate.search(base, filter, new AttributesMapper<LdapUserDto>() {
            @Override
            public LdapUserDto mapFromAttributes(Attributes attributes) throws NamingException {
                LdapUserDto ldapUserDto = new LdapUserDto();
                ldapUserDto.setFirstName(attributes.get("cn")!= null ? attributes.get("cn").get().toString() : "");
                ldapUserDto.setLastName(attributes.get("sn")!= null ? attributes.get("sn").get().toString() : "");
                ldapUserDto.setEmail(attributes.get("mail")!= null ? attributes.get("mail").get().toString() : "");
//                todo: add other fields, email city , state ...
//                ldapUserDto.setFirstName(attributes.get("cn")!= null ? attributes.get("cn").get().toString() : "");
                return ldapUserDto;
            }
        });
    }

    public void createUser(LdapUserDto ldapUserDto){
            LdapName dn = LdapNameBuilder.newInstance("ou=users, ou=system")
                .add("cn", ldapUserDto.getFirstName())  // adding common name
                .build();

        Attributes attributes = new BasicAttributes();
        attributes.put("objectClass","inetOrgPerson");
        attributes.put("cn",ldapUserDto.getFirstName());
        attributes.put("sn", ldapUserDto.getLastName());
        attributes.put("uid", ldapUserDto.getUsername());
        attributes.put("mail",ldapUserDto.getEmail());
        ldapTemplate.bind(dn,null, attributes);
    }
}
