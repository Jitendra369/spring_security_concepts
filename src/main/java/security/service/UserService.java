package security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import security.dto.ResetPasswordDto;
import security.model.User;
import security.repo.UserRepo;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        user.setPassword(!user.getPassword().isEmpty() ? passwordEncoder.encode(user.getPassword()) : user.getPassword());
        return userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

// reset the password
    public ResetPasswordDto<String> resetPasswordV1(String oldPassword, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            String loggedInUserName = getCurrentLoggedInUserName();
            String savePassword ="";
            if (loggedInUserName != null && !loggedInUserName.isEmpty()) {
                Optional<User> userOptional = userRepo.findByUsername(loggedInUserName);
                if (userOptional.isPresent()) {
                    savePassword = userOptional.get().getPassword();
                }
            }
            boolean isOldPasswordIsCorrect = passwordEncoder.matches(oldPassword, savePassword);
            boolean isNewPasswordSameAsOld = oldPassword.equals(newPassword);
            if (isOldPasswordIsCorrect && !isNewPasswordSameAsOld) {
//                update the password
                Optional<User> savedUser = userRepo.findByUsername(getCurrentLoggedInUserName());
                if (savedUser.isPresent()) {
                    User user = savedUser.get();
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepo.save(user);
                }
            }
            ResetPasswordDto<String> response = new ResetPasswordDto<String>();
            response.setPasswordChangeMessage("old password is incorrect or new password is same!");
            return response;
        }
        return null;
    }

//     get current loggedIn user username
    private String getCurrentLoggedInUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

}
