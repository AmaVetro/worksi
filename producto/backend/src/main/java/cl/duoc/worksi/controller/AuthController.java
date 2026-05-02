package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.auth.LoginRequest;
import cl.duoc.worksi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/api/v1/auth/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request.getEmail(), request.getPassword());
  }
}
