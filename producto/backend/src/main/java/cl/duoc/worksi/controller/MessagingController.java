package cl.duoc.worksi.controller;

import cl.duoc.worksi.dto.messaging.CreateConversationRequest;
import cl.duoc.worksi.dto.messaging.SendMessageRequest;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.security.UserPrincipal;
import cl.duoc.worksi.service.MessagingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messaging")
public class MessagingController {
  private final MessagingService messagingService;

  public MessagingController(MessagingService messagingService) {
    this.messagingService = messagingService;
  }

  @PostMapping("/conversations")
  public ResponseEntity<?> createConversation(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody CreateConversationRequest body) {
    requireRecruiter(principal);
    return messagingService.createConversation(
        principal.getUser().getId(), body.getApplicationId(), body.getFirstMessage());
  }

  @GetMapping("/conversations")
  public ResponseEntity<?> listConversations(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "updated_at,desc") String sort) {
    UserRole role = principal.getUser().getRole();
    long userId = principal.getUser().getId();
    if (role == UserRole.RECRUITER) {
      return messagingService.listForRecruiter(userId, page, size, sort);
    }
    if (role == UserRole.CANDIDATE) {
      return messagingService.listForCandidate(userId, page, size, sort);
    }
    return ResponseEntity.status(403).build();
  }

  @GetMapping("/conversations/login-notice")
  public ResponseEntity<?> loginNotice(@AuthenticationPrincipal UserPrincipal principal) {
    requireCandidate(principal);
    return messagingService.getLoginNotice(principal.getUser().getId());
  }

  @PatchMapping("/conversations/{conversation_id}/dismiss-login-notice")
  public ResponseEntity<?> dismissLoginNotice(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("conversation_id") long conversationId) {
    requireCandidate(principal);
    return messagingService.dismissLoginNotice(principal.getUser().getId(), conversationId);
  }

  @GetMapping("/conversations/{conversation_id}")
  public ResponseEntity<?> getConversation(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("conversation_id") long conversationId) {
    UserRole role = principal.getUser().getRole();
    long userId = principal.getUser().getId();
    if (role == UserRole.RECRUITER) {
      return messagingService.getDetailForRecruiter(userId, conversationId);
    }
    if (role == UserRole.CANDIDATE) {
      return messagingService.getDetailForCandidate(userId, conversationId);
    }
    return ResponseEntity.status(403).build();
  }

  @GetMapping("/conversations/{conversation_id}/messages")
  public ResponseEntity<?> listMessages(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("conversation_id") long conversationId,
      @RequestParam(required = false) Long after_message_id,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "50") int size,
      @RequestParam(defaultValue = "sent_at,asc") String sort) {
    UserRole role = principal.getUser().getRole();
    if (role != UserRole.RECRUITER && role != UserRole.CANDIDATE) {
      return ResponseEntity.status(403).build();
    }
    return messagingService.listMessages(
        principal.getUser().getId(),
        role,
        conversationId,
        after_message_id,
        page,
        size,
        sort);
  }

  @PostMapping("/conversations/{conversation_id}/messages")
  public ResponseEntity<?> sendMessage(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable("conversation_id") long conversationId,
      @Valid @RequestBody SendMessageRequest body) {
    UserRole role = principal.getUser().getRole();
    if (role != UserRole.RECRUITER && role != UserRole.CANDIDATE) {
      return ResponseEntity.status(403).build();
    }
    return messagingService.sendMessage(
        principal.getUser().getId(), role, conversationId, body.getBody());
  }

  private static void requireRecruiter(UserPrincipal principal) {
    if (principal.getUser().getRole() != UserRole.RECRUITER) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Sin permisos");
    }
  }

  private static void requireCandidate(UserPrincipal principal) {
    if (principal.getUser().getRole() != UserRole.CANDIDATE) {
      throw new org.springframework.web.server.ResponseStatusException(
          org.springframework.http.HttpStatus.FORBIDDEN, "Sin permisos");
    }
  }
}
