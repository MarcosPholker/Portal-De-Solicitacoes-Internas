package com.portal.solicitacoes.internas.controller;

import com.portal.solicitacoes.internas.dto.UserCreateDTO;
import com.portal.solicitacoes.internas.dto.UserLoginDTO;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO){
        userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserLoginDTO userLoginDTO){
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }
}
