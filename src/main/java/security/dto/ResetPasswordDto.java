package security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import security.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto<T> {
    private T oldPassword;
    private T newPassword;
    private T passwordChangeMessage;
}
