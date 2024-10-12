package security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import security.dto.MessageResponseXml;
import security.model.User;
import security.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/mapping")
public class MyXmlResponseController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/xml", produces = "application/xml")
    public ResponseEntity<?> getData(){
        MessageResponseXml repsonse = new MessageResponseXml("hello world");

        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
//        return ResponseEntity.ok(repsonse);
    }
}
