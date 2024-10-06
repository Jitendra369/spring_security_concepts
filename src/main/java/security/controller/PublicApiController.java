package security.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/public/userInfo")
public class PublicApiController {


//    spring expression Language
    @Value("#{systemProperties['user.name']}")
    private String username;

    @GetMapping
    public String getUserInformation(){
//      return "public user";
        try {
            double val = Double.parseDouble("w");
        }catch (NumberFormatException  e){
            throw new NumberFormatException(("Invalid Operator: Invalid vatiables"));
        }

        return username;
    }
}
