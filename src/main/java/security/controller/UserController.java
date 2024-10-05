package security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import security.dto.ResetPasswordDto;
import security.model.User;
import security.service.UserService;

import java.security.Principal;
import java.util.List;

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public User addUserDetails(@RequestBody User user){
        return this.userService.saveUser(user);
    }

    @GetMapping("/all")
    public List<User> findAllUser(){
        return this.userService.getAllUsers();
    }
    @GetMapping("/curr-user")
    public String getCurrentUser(Principal principal){
        return principal.getName();
    }

    @PostMapping("/resetPass")
    public ResetPasswordDto<String> resetPassword(@RequestBody ResetPasswordDto<String> resetPasswordDto){
        return userService.resetPasswordV1(resetPasswordDto.getOldPassword(),resetPasswordDto.getNewPassword());
    }

}
