package com.example.demo.controller;
import com.example.demo.dto.RoleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.NotEnoughRightException;
import com.example.demo.service.PostService;
import com.example.demo.service.UserService;
import com.example.demo.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
@Validated
public class UserController {
    private UserService userService;
    private PostService postService;

    @Autowired
    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") int id, Principal principal) {
        User user = userService.getUser(id);
        User principalUser = userService.getUserByEmailAndEnabled(principal.getName());
        UserDTO userDTO;

        userService.calculateRating(user);
        userDTO = Mapper.convertToUserDTO(user);
        if (userService.isAdmin(principalUser) || principalUser.getId() == id) {
            userDTO.setEmail(user.getEmail());
        }
        return userDTO;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return Mapper.convertToListUserDTO(userService.getAllUsers());
    }

    @GetMapping("/readers")
    public List<UserDTO> getAllReaders() {
        return Mapper.convertToListUserDTO(userService.getAllReaders());
    }

    @GetMapping("/traders")
    public List<UserDTO> getAllTraders(@RequestParam(required = false, defaultValue = "5")
            @DecimalMax(value = "5.0",
            message = "min parameter should be less or equal than 5.0") double max,
            @RequestParam(required = false, defaultValue = "1") @DecimalMin(value = "1.0",
            message = "min parameter should be more or equal than 1.0") double min,
            @RequestParam(required = false) String games,
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "skip parameter should be more or equal than 0") int skip,
            @RequestParam(required = false, defaultValue = "0")
            @Min(value = 0, message = "limit parameter should be more or equal than 0") int limit) {
        String[] split;
        List<Integer> idList;
        List<User> users;

        if (min > max) {
            throw new ValidationException("min parameter should be less than max!");
        }
        try {
            split = games.split(",");
            idList = postService.getGameIdByName(split);
            if (idList == null) {
                return new ArrayList<UserDTO>();
            }
            users = userService.getAllTradersByGames(idList);
        }
        catch (NullPointerException e) {
            users = userService.getAllTraders();
            System.out.println(users);
        }
        users = userService.filterTraders(users, max, min, skip, limit);
        return Mapper.convertToListUserDTO(users);
    }

    @PutMapping("/my")
    public void updateUser(@RequestBody @Valid UserDTO userDTO,
                           Principal principal) {
        User user = userService.getUserByEmailAndEnabled(principal.getName());

        userService.updateUser(user, userDTO);
    }

    @PatchMapping("/{id}/change-role")
    public void changeRole(@PathVariable("id") int id,
                           @RequestBody @Valid RoleDTO roleDTO) {
        User user = userService.getUser(id);

        if (userService.isAdmin(user)) {
            throw new NotEnoughRightException("You can't change role of admin!");
        }
        userService.changeRole(user, roleDTO.getRole());
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") int id, Principal principal) {
        User user = userService.getUser(id);
        User principalUser = userService.getUserByEmailAndEnabled(principal.getName());

        if (!userService.isAdmin(principalUser) && !principalUser.equals(user)) {
            throw new NotEnoughRightException("You can't delete this user!");
        }
        if (userService.isAdmin(user)) {
            throw new NotEnoughRightException("You can't delete admin!");
        }
        userService.deleteUser(user);
    }
}
