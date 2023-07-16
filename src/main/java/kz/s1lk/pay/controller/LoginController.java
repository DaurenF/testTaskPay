package kz.s1lk.pay.controller;

import kz.s1lk.pay.exception.IncorrectCredentialsException;
import kz.s1lk.pay.model.dto.ErrorResponse;
import kz.s1lk.pay.model.dto.LoginDto;
import kz.s1lk.pay.util.HttpExceptionHandler;
import kz.s1lk.pay.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final HttpExceptionHandler exceptionHandler;

    @PostMapping("/login")      //todo encryption
    public ResponseEntity<Map<String, String>> performLogin(@RequestBody LoginDto loginDto) {
        log.info("Client login request: {}", loginDto.toString());
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            throw new IncorrectCredentialsException("Incorrect credentials!", HttpStatus.UNAUTHORIZED.value());
        }
        log.info("Successful login: {}", loginDto.getEmail());
        String token = jwtUtil.generateToken(loginDto.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return exceptionHandler.handleException(e);
    }
}
