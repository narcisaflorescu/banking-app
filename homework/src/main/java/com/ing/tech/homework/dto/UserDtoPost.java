package com.ing.tech.homework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDtoPost {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    private String authorizationRoles;

}
