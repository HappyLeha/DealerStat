package com.example.demo.service;
import com.example.demo.dto.NewPasswordDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
   void createUser(UserDTO userDTO);
   void createCode(String email);
   void confirmUser(String token);
   void confirmCode(NewPasswordDTO newPasswordDTO);
   User getUser(int id);
   User getUserByEmailAndEnabled(String email);
   User getByEmailAndPassword(String email, String password);
   List<User> getAllUsers();
   List<User> getAllReaders();
   List<User> getAllTraders();
   List<User> getAllTradersByGames(List<Integer> idList);
   List<User> filterTraders(List<User> users, double max,
                            double min, int skip, int limit);
   boolean isAdmin(User user);
   void updateUser(User user, UserDTO userDTO);
   void changeRole(User user, String roleName);
   void deleteUser(User user);
   void calculateRating(User user);
}
