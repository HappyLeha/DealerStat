package com.example.demo.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Calendar;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {

    private int id;

    @NotBlank(message = "title shouldn't be empty")
    private String title;

    @NotBlank(message = "text shouldn't be empty")
    private String text;

    @NotBlank(message = "status shouldn't be empty")
    @Pattern(regexp = "^PUBLIC$|^DRAFT$",
            message = "status should be PUBLIC OR DRAFT")
    private String status;

    private int authorId;

    private Calendar createdAt;

    private Calendar updatedAt;

    private List<GameDTO> games;
}
