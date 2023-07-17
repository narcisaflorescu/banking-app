package com.ing.tech.homework.resource;

import com.ing.tech.homework.dto.AuthenticationDto;
import com.ing.tech.homework.dto.UserDtoPost;
import com.ing.tech.homework.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;


@Controller
@RequestMapping("/usersController")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    // ------------------------------ SECURITY---------------------------------------------------
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDtoPost userDto) {
        userService.create(userDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationDto> createSession(@RequestBody @Valid UserDtoPost userDto) {
        return ResponseEntity.ok().body(new AuthenticationDto(userService.authenticate(userDto)));
    }
}
