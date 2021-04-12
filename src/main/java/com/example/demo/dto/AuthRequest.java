package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @NotBlank(message = "email shouldn't be empty")
    @Email(message = "field email should be email")
    private String login;

    @NotBlank(message = "password shouldn't be empty")
    private String password;
}
