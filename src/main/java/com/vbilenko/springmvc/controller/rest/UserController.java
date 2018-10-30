package com.vbilenko.springmvc.controller.rest;

import com.vbilenko.springmvc.model.User;
import com.vbilenko.springmvc.model.UserProfile;
import com.vbilenko.springmvc.service.UserService;
import javafx.beans.binding.SetExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;


@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {this.userService = userService;}


    /**
     * This method will list all existing users.
     */
    @GetMapping
    public List<User> listUsers() {
        return userService.findAllUsers();
    }

    /**
     * This method will retrieve specific user by Id.
     */
    @GetMapping("/{id}")
    public User retrieveUser(@PathVariable int id) {
        Optional<User> user = Optional.ofNullable(userService.findById(id));
        if (!user.isPresent())
            System.out.println("Not Found");
        return user.get();
    }

    /**
     * This method will retrieve specific user by username (sso).
     */
    @GetMapping("/sso/{sso}")
    public User retrieveUser(@PathVariable String sso) {
        Optional<User> user = Optional.ofNullable(userService.findBySSO(sso));
        if (!user.isPresent())
            System.out.println("Not Found");
        return user.get();
    }

    /**
     * This method will delete an user by it's ID value.
     */
    @DeleteMapping(value = "{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStudent(@RequestBody User user, @PathVariable int id) {
        Optional<User> studentOptional = Optional.ofNullable(userService.findById(id));
        if (!studentOptional.isPresent())
            return ResponseEntity.notFound().build();

        //Keep "User" profile by default for simplicity
        UserProfile userProfile = new UserProfile();
        userProfile.setType("USER");
        userProfile.setId(1);

        Set<UserProfile> userProfiles = new HashSet<UserProfile>() {};

        userProfiles.add(userProfile);
        user.setUserProfiles(userProfiles);
        user.setId(id);
        userService.updateUser(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Object> createStudent(@RequestBody User user) {

        //New User has a "User" profile by default for simplicity
        UserProfile userProfile = new UserProfile();
        userProfile.setType("USER");
        userProfile.setId(1);

        Set<UserProfile> userProfiles = new HashSet<UserProfile>() {};
        userProfiles.add(userProfile);
        user.setUserProfiles(userProfiles);

        User savedUser = userService.saveUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();

    }
}
