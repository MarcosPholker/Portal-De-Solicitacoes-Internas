package com.portal.solicitacoes.internas.service;

import com.portal.solicitacoes.internas.dto.UserDTO;
import com.portal.solicitacoes.internas.dto.UserLoginDTO;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.repositories.UserRepository;
import com.portal.solicitacoes.internas.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User createUser(UserDTO userDTO){

        if(userRepositories.findByEmail(userDTO.email()).isPresent()){
            throw new RuntimeException("usuario ja existe com o email cadastrado");
        }

        User newUser = new User();
        newUser.setEmail(userDTO.email());
        newUser.setUsername(userDTO.username());
        newUser.setPassword(passwordEncoder.encode(userDTO.password()));

        return userRepositories.save(newUser);
    }

    public String login(UserLoginDTO loginDTO){
        User user = userRepositories.findByEmail(loginDTO.email()).orElseThrow(() -> new RuntimeException("email ou senha nao correspondem"));
        if(!passwordEncoder.matches(loginDTO.password(), user.getPassword())){
            throw new RuntimeException("email ou senha nao correspondem");
        }
        return tokenService.createToken(user.getId(), user.getEmail());
    }
}
