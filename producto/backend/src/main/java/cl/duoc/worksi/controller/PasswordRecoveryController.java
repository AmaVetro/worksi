package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.auth.PasswordRecoveryRequestDto;
import cl.duoc.worksi.dto.auth.PasswordRecoveryResetDto;
import cl.duoc.worksi.dto.auth.PasswordRecoveryVerifyDto;
import cl.duoc.worksi.service.PasswordRecoveryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordRecoveryController {
  private final PasswordRecoveryService passwordRecoveryService;

  public PasswordRecoveryController(PasswordRecoveryService passwordRecoveryService) {
    this.passwordRecoveryService = passwordRecoveryService;
  }

  @PostMapping("/api/v1/auth/password-recovery/request")
  public ResponseEntity<?> request(@Valid @RequestBody PasswordRecoveryRequestDto body) {
    return passwordRecoveryService.requestCode(body.getEmail());
  }

  @PostMapping("/api/v1/auth/password-recovery/verify")
  public ResponseEntity<?> verify(@Valid @RequestBody PasswordRecoveryVerifyDto body) {
    return passwordRecoveryService.verifyCode(body.getEmail(), body.getCode());
  }

  @PostMapping("/api/v1/auth/password-recovery/reset")
  public ResponseEntity<?> reset(@Valid @RequestBody PasswordRecoveryResetDto body) {
    return passwordRecoveryService.resetPassword(
        body.getEmail(), body.getRecoveryToken(), body.getNewPassword());
  }
}
