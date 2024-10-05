package security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/public/userInfo")
public class PublicApiController {

    @GetMapping
    public String getUserInformation(){
      return "public user";
    }
}
