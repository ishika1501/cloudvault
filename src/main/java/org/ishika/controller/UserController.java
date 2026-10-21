package org.ishika.controller;


import java.util.List;
import java.util.UUID;
import org.ishika.model.User;
import org.ishika.request.UserRequest;
import org.ishika.response.UserResponse;
import org.ishika.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class UserController {

  @Autowired
  UserService userService;

  @PostMapping
  User createUser(@RequestBody UserRequest userRequest){
    return userService.createUser(userRequest);
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable UUID id) {
    return userService.getUserById(id);
  }

  @PutMapping("/{id}")
  public UserResponse updateUser(@PathVariable UUID id, @RequestBody UserRequest request) {
    return userService.updateUser(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }

}
