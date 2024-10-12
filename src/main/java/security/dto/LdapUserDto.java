package security.dto;

import lombok.Getter;
import lombok.Setter;
import security.model.User;
//import security.model.User;

@Getter
@Setter
public class LdapUserDto extends User {
    private String uid;
    private String firstName;
    private String lastName;
}
