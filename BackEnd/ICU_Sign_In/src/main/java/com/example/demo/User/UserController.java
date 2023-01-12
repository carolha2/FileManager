package com.example.demo.User;


import com.example.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/admin")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/{name}",method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable String name){
        logger.info(name);
        User user= userRepository.findByUsername(name);
        userRepository.delete(user);
        ResponseEntity <User> responseEntity = new ResponseEntity<User>(user, HttpStatus.OK);
        logger.info(String.valueOf(responseEntity));
        return responseEntity;
    }

}
