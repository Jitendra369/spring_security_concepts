package security.service;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import security.dto.LdapUserDto;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    private static final String LDAP_BASE_URL = "ou=users,ou=system";

    public List<String> getAllNames() {
        String base = "ou=users,ou=system";
        String filter = "(ObjectClass=person)";

        return ldapTemplate.search(base, filter, new AttributesMapper<String>() {
            @Override
            public String mapFromAttributes(Attributes attributes) throws NamingException {
                return attributes.get("cn").get().toString();
            }
        });
    }

    public List<LdapUserDto> getAllUserInformation() {
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
                ldapUserDto.setFirstName(attributes.get("cn") != null ? attributes.get("cn").get().toString() : "");
                ldapUserDto.setLastName(attributes.get("sn") != null ? attributes.get("sn").get().toString() : "");
                ldapUserDto.setEmail(attributes.get("mail") != null ? attributes.get("mail").get().toString() : "");
//                todo: add other fields, email city , state ...
//                ldapUserDto.setFirstName(attributes.get("cn")!= null ? attributes.get("cn").get().toString() : "");
                return ldapUserDto;
            }
        });
    }

    public void createUser(LdapUserDto ldapUserDto) {
        LdapName dn = LdapNameBuilder.newInstance("ou=users, ou=system")
                .add("cn", ldapUserDto.getFirstName())  // adding common name
                .build();

        Attributes attributes = new BasicAttributes();
        attributes.put("objectClass", "inetOrgPerson");
        attributes.put("cn", ldapUserDto.getFirstName());
        attributes.put("sn", ldapUserDto.getLastName());
        attributes.put("uid", ldapUserDto.getUsername());
        attributes.put("mail", ldapUserDto.getEmail());
        ldapTemplate.bind(dn, null, attributes);
    }

    public List<String> getAllGroupNames() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        List<SearchResult> resultList = ldapTemplate.search(
                LDAP_BASE_URL,
                "(objectClass=groupOfNames)",
                searchControls,
                (AttributesMapper<SearchResult>) ctx -> (SearchResult) ctx);

        // Extract group names from results

        if (resultList != null && resultList.size() > 0) {
            return resultList.stream().map(result -> result.getNameInNamespace()).collect(Collectors.toList());
        }
        log.info("No user group found");
        return new ArrayList<>();
    }

    public String findByCommonName(String commonName) {
        return ldapTemplate.search(
                LDAP_BASE_URL,
                "cn=" + commonName + ")",
                (Attributes attrs) -> attrs.get("cn").get().toString()
        ).stream().findFirst().orElse(null);
    }

    public List<String> getAllGroupNamesV1() {

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        List<String> results = ldapTemplate.search(
                LDAP_BASE_URL, // Base DN from configuration
                "(objectClass=organizationalUnit)", // Filter to find OUs
                searchControls,
                new AttributesMapper<String>() {
                    @Override
                    public String mapFromAttributes(Attributes attributes) throws NamingException {
                        return attributes.get("ou").get().toString();
                    }
                }
        );

        return results;
    }

    public List<String> getAllGroupNamesUsingContextSource() {

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        List<String> results = ldapTemplate.search(
                LDAP_BASE_URL, // Base DN from configuration
                "(objectClass=organizationalUnit)", // Filter to find OUs
                searchControls,
                new ContextMapper<String>() {
                    @Override
                    public String mapFromContext(Object o) throws NamingException {
                        DirContextAdapter adaptor = (DirContextAdapter) o;
                        return adaptor.getNameInNamespace();
                    }
                }
        );

        return results;
    }

    public HashMap<String, List<String>> getOuWithUser(){
        HashMap<String, List<String>> ouWithUsersMap = new HashMap<>();

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

//        fetch all the ou's
        List<String> ouList = ldapTemplate.search(
                LDAP_BASE_URL,
                "(objectClass=organizationalUnit)",
                searchControls,
                new ContextMapper<String>() {
                    @Override
                    public String mapFromContext(Object ctx) throws NamingException {
                        DirContextAdapter dirContextAdapter = (DirContextAdapter) ctx;
                        return dirContextAdapter.getStringAttribute("ou");
                    }
                }
        );

        if (ouList != null && !ouList.isEmpty()){
            ouList.forEach(s -> {
                List<String> userInOrganizationUnit = getUserInOrganizationUnit(s);
                ouWithUsersMap.put(s, userInOrganizationUnit);
            });
        }

        return ouWithUsersMap;
    }

//    public List<String> getUserInOrganizationUnit(String ou) {
//        List<String> userList = new ArrayList<>();
//        SearchControls searchControls = new SearchControls();
//        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//
//        List<DirContextAdapter> result = ldapTemplate.search(
//                "ou="+ou+","+"ou=system",
//                "(objectClass=person)",
//                searchControls,
//                new ContextMapper<DirContextAdapter>() {
//                    @Override
//                    public DirContextAdapter mapFromContext(Object ctx) throws NamingException {
//                        return (DirContextAdapter) ctx;
//                    }
//                }
//        );
//
//        if (result != null && !result.isEmpty()) {
//            result.forEach(value -> {
//                userList.add(value.getStringAttribute("cn"));
//            });
//        }
//        return userList;
//    }

    private List<String> getUserInOrganizationUnit(String ou) {
        List<String> userList = new ArrayList<>();

        // Define search controls to look for users in the specific OU
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Ensure we search the entire subtree

        // Construct the base DN for the OU
        String baseDn = "ou=" + ou + "," + "ou=system"; // Ensure this is correct
        System.out.println("Searching for users in: " + baseDn); // Debugging statement

        // Search for users within the specified OU
        List<DirContextAdapter> results = ldapTemplate.search(
                baseDn, // Base DN for the OU
                "(objectClass=person)", // Filter to find users
                searchControls,
                (ContextMapper<DirContextAdapter>) ctx -> (DirContextAdapter) ctx // Cast to DirContextAdapter
        );

        // Extract CN from results
        for (DirContextAdapter result : results) {
            String userName = result.getStringAttribute("cn"); // Get the common name (CN)
            if (userName != null) {
                userList.add(userName); // Add CN to userList
            }
        }

        return userList;
    }
}
