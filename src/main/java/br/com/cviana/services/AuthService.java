package br.com.cviana.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.stereotype.Service;

import br.com.cviana.controllers.AuthController;
import br.com.cviana.data.dto.security.AccountCredentialsDTO;
import br.com.cviana.data.dto.security.TokenDTO;
import br.com.cviana.exceptions.BadRequestException;
import br.com.cviana.exceptions.RequiredObjectIsNullException;
import br.com.cviana.mapper.ObjectMapper;
import br.com.cviana.models.User;
import br.com.cviana.repository.UserRepository;
import br.com.cviana.security.jwt.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired private AuthenticationManager manager;
    @Autowired private JwtTokenProvider provider;
    @Autowired private UserRepository repository;

    private static void enrichWithLinks(AccountCredentialsDTO dto) {
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).signin(dto)).withRel("signin").withTitle("POST"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).refresh(dto.getUsername(), "{refreshToken}")).withRel("refresh").withTitle("PUT"));
		dto.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).create(dto)).withRel("createUser").withTitle("POST"));
	}

    public ResponseEntity<TokenDTO> signin(AccountCredentialsDTO credentials) {
        manager.authenticate( new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        var user = repository.findByUsername(credentials.getUsername());
        if(user == null) throw new UsernameNotFoundException("Username ["+credentials.getUsername()+"] not found");
        var token = provider.createAccessToken(credentials.getUsername(), user.getRoles());
        enrichWithLinks(credentials);
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refresh(String username, String refreshToken) {
        var user = repository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException("Username ["+username+"] not found");
        var token = provider.refreshToken(refreshToken);
        enrichWithLinks(ObjectMapper.parseObject(user, AccountCredentialsDTO.class));
        return ResponseEntity.ok(token);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO credentials) {
        boolean usernameTest = credentials.getUsername() == null || credentials.getUsername().isBlank();
        boolean passwordTest = credentials.getPassword() == null || credentials.getPassword().isBlank();
        if(usernameTest || passwordTest)
            throw new RequiredObjectIsNullException("User name and password must not be null nor blank. Check values and try again.");
        if(repository.findByUsername(credentials.getUsername()) != null)
            throw new BadRequestException("User name ["+credentials.getUsername()+"] already in use. Type a different name and try again.");
        
        var entity = new User();
        entity.setFullName(credentials.getFullName());
        entity.setUsername(credentials.getUsername());
        entity.setPassword(generateHashedPassword(credentials.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
        repository.save(entity);
        credentials.setPassword("***");
        enrichWithLinks(credentials);
        return ObjectMapper.parseObject(repository.save(entity), AccountCredentialsDTO.class);
    }

    private String generateHashedPassword(String password) {
        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
        encoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        return encoder.encode(password);
    }

}
