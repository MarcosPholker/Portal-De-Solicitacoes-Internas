package com.portal.solicitacoes.internas.service;

import com.portal.solicitacoes.internas.dto.UserCreateDTO;
import com.portal.solicitacoes.internas.dto.UserLoginDTO;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.exception.EmailAlreadyExistsException;
import com.portal.solicitacoes.internas.repositories.UserRepository;
import com.portal.solicitacoes.internas.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.portal.solicitacoes.internas.exception.InvalidCredentialsException;

@Service
public class UserService {
    private final UserRepository userRepositories;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserService(UserRepository userRepositories, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepositories = userRepositories;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public User createUser(UserCreateDTO userCreateDTO){

        if(userRepositories.existsByEmail(userCreateDTO.email())){
            throw new EmailAlreadyExistsException("Email já cadastrado!");
        }

        User newUser = new User();
        newUser.setEmail(userCreateDTO.email());
        newUser.setUsername(userCreateDTO.username());
        newUser.setPassword(passwordEncoder.encode(userCreateDTO.password()));

        return userRepositories.save(newUser);
    }

    public String login(UserLoginDTO loginDTO){
        User user = userRepositories.findByEmail(loginDTO.email()).orElseThrow(() -> new InvalidCredentialsException("email ou senha nao correspondem"));
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())){
            throw new InvalidCredentialsException("email ou senha nao correspondem");
        }
        return tokenService.createToken(user.getId(), user.getEmail());
    }
}
