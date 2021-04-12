package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.jwt.JwtProvider;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {
    private UserService userService;
    private JwtProvider jwtProvider;

    @Autowired
    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/confirm/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmUser(@PathVariable("token") String token) {
        userService.confirmUser(token);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse auth(@RequestBody @Valid AuthRequest request) {
        User user = userService.getByEmailAndPassword(request.getLogin(),
                request.getPassword());
        String token = jwtProvider.generateToken(user.getEmail());

        return new AuthResponse(token);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody @Valid UserDTO userDTO) {
        userService.createUser(userDTO);
    }

    @PostMapping("/forgot_password")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCode(@RequestBody @Valid EmailDTO emailDTO) {
        userService.createCode(emailDTO.getEmail());
    }

    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.OK)
    public void confirmCode(@RequestBody @Valid NewPasswordDTO newPasswordDTO) {
        userService.confirmCode(newPasswordDTO);
    }
}
