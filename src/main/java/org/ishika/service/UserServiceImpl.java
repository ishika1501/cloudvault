package org.ishika.service;

import static org.ishika.utils.Constant.HOME_DIRECTORY;

import java.util.List;
import java.util.UUID;
import org.ishika.model.User;
import org.ishika.repository.UserRepository;
import org.ishika.request.FolderRequest;
import org.ishika.request.LoginRequest;
import org.ishika.request.UserRequest;
import org.ishika.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.ishika.security.JwtService;
import org.ishika.response.LoginResponse;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  FolderService folderService;

  @Autowired
  JwtService jwtService;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


  @Override
  public UserResponse createUser(UserRequest request) {
    String email = request.getEmail().trim().toLowerCase();

    if (userRepository.existsByEmail(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
    }

    User user = prepareUser(request);
    user.setEmail(email);
    User savedUser = userRepository.save(user);
    postUserSaveTask(savedUser);
    return toResponse(savedUser);
  }

  @Override
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Override
  public UserResponse getUserById(UUID id) {
    User user = findUser(id);
    return toResponse(user);
  }

  @Override
  public UserResponse updateUser(UUID id, UserRequest request) {
    User user = findUser(id);
    user.setEmail(request.getEmail());
    user.setDisplayName(request.getDisplayName());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    User updatedUser = userRepository.save(user);
    return toResponse(updatedUser);
  }

  @Override
  public void deleteUser(UUID id) {
    User user = findUser(id);
    userRepository.delete(user);
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    String email = request.getEmail().trim().toLowerCase();
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

    if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    String token = jwtService.generateToken(user.getId(), user.getEmail());
    System.out.println("LOGIN SUCCESSFUL");
    return new LoginResponse(user.getId(), user.getEmail(), user.getDisplayName(), token);
  }

  private void postUserSaveTask(final User user) {
    folderService.createFolder(prepareDefaultFolder(user));
  }

  private FolderRequest prepareDefaultFolder(final User user) {
    FolderRequest folderRequest = new FolderRequest();
    folderRequest.setFolderName(HOME_DIRECTORY);
    folderRequest.setUserId(user.getId());
    return folderRequest;
  }

  private User prepareUser(UserRequest request) {
    User user = new User();
    user.setEmail(request.getEmail());
    user.setDisplayName(request.getDisplayName());
    user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
    return user;
  }

  private User findUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  private UserResponse toResponse(User user) {
    return new UserResponse(user.getId(), user.getEmail(), user.getDisplayName());
  }

  @Override
  public UserResponse getUserByIdOrThrow(UUID id) {
    UserResponse user = getUserById(id);
    if (user == null) {
      throw new RuntimeException("user not found");
    }
    return user;
  }
}
