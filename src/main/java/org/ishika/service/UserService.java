package org.ishika.service;

import java.util.List;
import java.util.UUID;
import org.ishika.model.User;
import org.ishika.request.UserRequest;
import org.ishika.response.UserResponse;

public interface UserService {
  User createUser(UserRequest request);
  List<UserResponse> getAllUsers();
  UserResponse getUserById(UUID id);
  UserResponse updateUser(UUID id, UserRequest request);
  void deleteUser(UUID id);
}
