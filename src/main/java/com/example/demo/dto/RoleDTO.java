package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleDTO {

    @NotBlank(message = "role shouldn't be empty")
    @Pattern(regexp = "^ROLE_TRADER$|^ROLE_READER$",
            message = "role should be ROLE_TRADER OR ROLE_READER")
    private String role;
}
