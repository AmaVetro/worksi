package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.PageResponse;
import cl.duoc.worksi.dto.messaging.CandidateConversationDetailResponse;
import cl.duoc.worksi.dto.messaging.CandidateConversationListItemResponse;
import cl.duoc.worksi.dto.messaging.CreateConversationResponse;
import cl.duoc.worksi.dto.messaging.DismissLoginNoticeResponse;
import cl.duoc.worksi.dto.messaging.FirstMessageResponse;
import cl.duoc.worksi.dto.messaging.LoginNoticeItemResponse;
import cl.duoc.worksi.dto.messaging.LoginNoticeResponse;
import cl.duoc.worksi.dto.messaging.MessageItemResponse;
import cl.duoc.worksi.dto.messaging.RecruiterConversationDetailResponse;
import cl.duoc.worksi.dto.messaging.RecruiterConversationListItemResponse;
import cl.duoc.worksi.entity.Application;
import cl.duoc.worksi.entity.CandidateProfile;
import cl.duoc.worksi.entity.Conversation;
import cl.duoc.worksi.entity.ConversationMessage;
import cl.duoc.worksi.entity.Job;
import cl.duoc.worksi.entity.enums.ApplicationStatus;
import cl.duoc.worksi.entity.enums.JobStatus;
import cl.duoc.worksi.entity.enums.MessageSenderRole;
import cl.duoc.worksi.entity.enums.UserRole;
import cl.duoc.worksi.repository.ApplicationRepository;
import cl.duoc.worksi.repository.CandidateProfileRepository;
import cl.duoc.worksi.repository.ConversationMessageRepository;
import cl.duoc.worksi.repository.ConversationRepository;
import cl.duoc.worksi.repository.JobRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MessagingService {
  private static final List<ApplicationStatus> VISIBLE_APPLICATION =
      List.of(ApplicationStatus.APPLIED, ApplicationStatus.VIEWED);
  private static final int FIRST_MESSAGE_MAX = 200;
  private static final int MESSAGE_MAX = 500;
  private static final int PREVIEW_MAX = 80;

  private final ConversationRepository conversationRepository;
  private final ConversationMessageRepository messageRepository;
  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final CandidateProfileRepository candidateProfileRepository;

  public MessagingService(
      ConversationRepository conversationRepository,
      ConversationMessageRepository messageRepository,
      ApplicationRepository applicationRepository,
      JobRepository jobRepository,
      CandidateProfileRepository candidateProfileRepository) {
    this.conversationRepository = conversationRepository;
    this.messageRepository = messageRepository;
    this.applicationRepository = applicationRepository;
    this.jobRepository = jobRepository;
    this.candidateProfileRepository = candidateProfileRepository;
  }

  @Transactional
  public ResponseEntity<CreateConversationResponse> createConversation(
      long recruiterUserId, long applicationId, String firstMessageRaw) {
    String firstMessage = normalizeBody(firstMessageRaw, FIRST_MESSAGE_MAX, "first_message");
    Application app =
        applicationRepository
            .findById(applicationId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada"));
    if (!VISIBLE_APPLICATION.contains(app.getStatus())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrada");
    }
    Job job = requireRecruiterJob(recruiterUserId, app.getJobId());
    if (conversationRepository
        .findByRecruiterUserIdAndCandidateUserId(recruiterUserId, app.getCandidateUserId())
        .isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe una conversacion con este candidato");
    }
    Conversation conversation =
        Conversation.create(
            app.getId(),
            recruiterUserId,
            app.getCandidateUserId(),
            job.getCompanyId(),
            job.getId());
    conversation = conversationRepository.save(conversation);
    ConversationMessage message =
        ConversationMessage.create(
            conversation.getId(),
            recruiterUserId,
            MessageSenderRole.RECRUITER,
            firstMessage);
    message = messageRepository.save(message);
    conversation.markRecruiterRead(message.getId());
    conversation.touchUpdated();
    conversationRepository.save(conversation);
    Instant created = toInstant(conversation.getCreatedAt());
    Instant sent = toInstant(message.getSentAt());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            new CreateConversationResponse(
                conversation.getId(),
                conversation.getApplicationId(),
                conversation.getCandidateUserId(),
                conversation.getCompanyId(),
                job.getCompanyCommercialName(),
                conversation.getRecruiterUserId(),
                created,
                new FirstMessageResponse(message.getId(), message.getBody(), sent)));
  }

  public ResponseEntity<PageResponse<RecruiterConversationListItemResponse>> listForRecruiter(
      long recruiterUserId, int page, int size, String sort) {
    Pageable pageable = buildConversationPageable(page, size, sort);
    Page<Conversation> result =
        conversationRepository.findByRecruiterUserId(recruiterUserId, pageable);
    List<RecruiterConversationListItemResponse> items = mapRecruiterListItems(result.getContent());
    return ResponseEntity.ok(
        new PageResponse<>(
            items,
            pageable.getPageNumber() + 1,
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()));
  }

  public ResponseEntity<PageResponse<CandidateConversationListItemResponse>> listForCandidate(
      long candidateUserId, int page, int size, String sort) {
    Pageable pageable = buildConversationPageable(page, size, sort);
    Page<Conversation> result =
        conversationRepository.findByCandidateUserId(candidateUserId, pageable);
    List<CandidateConversationListItemResponse> items = mapCandidateListItems(result.getContent());
    return ResponseEntity.ok(
        new PageResponse<>(
            items,
            pageable.getPageNumber() + 1,
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()));
  }

  public ResponseEntity<LoginNoticeResponse> getLoginNotice(long candidateUserId) {
    Optional<Conversation> opt =
        conversationRepository.findFirstByCandidateUserIdAndLoginNoticeDismissedFalseOrderByCreatedAtDesc(
            candidateUserId);
    if (opt.isEmpty()) {
      return ResponseEntity.ok(new LoginNoticeResponse(null));
    }
    Conversation c = opt.get();
    Job job =
        jobRepository
            .findById(c.getJobId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada"));
    String preview =
        messageRepository
            .findFirstByConversationIdOrderByIdAsc(c.getId())
            .map(m -> preview(m.getBody()))
            .orElse("");
    return ResponseEntity.ok(
        new LoginNoticeResponse(
            new LoginNoticeItemResponse(
                c.getId(),
                job.getCompanyCommercialName(),
                job.getTitle(),
                preview,
                job.getSalaryOffered())));
  }

  @Transactional
  public ResponseEntity<DismissLoginNoticeResponse> dismissLoginNotice(
      long candidateUserId, long conversationId) {
    Conversation c = requireConversation(conversationId);
    if (c.getCandidateUserId() != candidateUserId) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permisos sobre la conversacion");
    }
    c.dismissLoginNotice();
    conversationRepository.save(c);
    return ResponseEntity.ok(new DismissLoginNoticeResponse(c.getId(), true));
  }

  @Transactional
  public ResponseEntity<RecruiterConversationDetailResponse> getDetailForRecruiter(
      long recruiterUserId, long conversationId) {
    Conversation c = requireParticipant(recruiterUserId, UserRole.RECRUITER, conversationId);
    markRecruiterReadToLatest(c);
    Job job = jobRepository.findById(c.getJobId()).orElseThrow();
    CandidateProfile profile =
        candidateProfileRepository
            .findById(c.getCandidateUserId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));
    return ResponseEntity.ok(
        new RecruiterConversationDetailResponse(
            c.getId(),
            c.getCandidateUserId(),
            displayName(profile),
            c.getApplicationId(),
            c.getJobId(),
            job.getTitle(),
            toInstant(c.getCreatedAt())));
  }

  @Transactional
  public ResponseEntity<CandidateConversationDetailResponse> getDetailForCandidate(
      long candidateUserId, long conversationId) {
    Conversation c = requireParticipant(candidateUserId, UserRole.CANDIDATE, conversationId);
    markCandidateReadToLatest(c);
    Job job = jobRepository.findById(c.getJobId()).orElseThrow();
    return ResponseEntity.ok(
        new CandidateConversationDetailResponse(
            c.getId(),
            c.getCompanyId(),
            job.getCompanyCommercialName(),
            c.getApplicationId(),
            c.getJobId(),
            job.getTitle(),
            toInstant(c.getCreatedAt())));
  }

  @Transactional
  public ResponseEntity<PageResponse<MessageItemResponse>> listMessages(
      long userId,
      UserRole role,
      long conversationId,
      Long afterMessageId,
      int page,
      int size,
      String sort) {
    Conversation conversation = requireParticipant(userId, role, conversationId);
    if (role == UserRole.RECRUITER) {
      markRecruiterReadToLatest(conversation);
    } else if (role == UserRole.CANDIDATE) {
      markCandidateReadToLatest(conversation);
    }
    int sz = Math.min(100, Math.max(1, size));
    List<MessageItemResponse> items;
    long total;
    int totalPages;
    int pageNum;
    if (afterMessageId != null) {
      Pageable poll =
          PageRequest.of(0, sz, Sort.by(Sort.Direction.ASC, "id"));
      List<ConversationMessage> polled =
          messageRepository.findByConversationIdAndIdGreaterThanOrderByIdAsc(
              conversationId, afterMessageId, poll);
      items = polled.stream().map(this::toMessageItem).toList();
      total = items.size();
      totalPages = items.isEmpty() ? 0 : 1;
      pageNum = 1;
    } else {
      int p = Math.max(1, page);
      Pageable pageable = PageRequest.of(p - 1, sz, parseMessageSort(sort));
      Page<ConversationMessage> result =
          messageRepository.findByConversationId(conversationId, pageable);
      items = result.getContent().stream().map(this::toMessageItem).toList();
      total = result.getTotalElements();
      totalPages = result.getTotalPages();
      pageNum = p;
    }
    return ResponseEntity.ok(new PageResponse<>(items, pageNum, sz, total, totalPages));
  }

  @Transactional
  public ResponseEntity<MessageItemResponse> sendMessage(
      long userId, UserRole role, long conversationId, String bodyRaw) {
    Conversation c = requireParticipant(userId, role, conversationId);
    String body = normalizeBody(bodyRaw, MESSAGE_MAX, "body");
    MessageSenderRole senderRole =
        role == UserRole.RECRUITER ? MessageSenderRole.RECRUITER : MessageSenderRole.CANDIDATE;
    ConversationMessage message =
        ConversationMessage.create(conversationId, userId, senderRole, body);
    message = messageRepository.save(message);
    c.touchUpdated();
    conversationRepository.save(c);
    return ResponseEntity.status(HttpStatus.CREATED).body(toMessageItem(message));
  }

  private Job requireRecruiterJob(long recruiterUserId, long jobId) {
    Job job =
        jobRepository
            .findById(jobId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada"));
    if (job.getPublishedByUserId() == null
        || !job.getPublishedByUserId().equals(recruiterUserId)
        || job.getStatus() == JobStatus.DELETED) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Oferta no encontrada");
    }
    return job;
  }

  private Conversation requireConversation(long conversationId) {
    return conversationRepository
        .findById(conversationId)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversacion no encontrada"));
  }

  private Conversation requireParticipant(long userId, UserRole role, long conversationId) {
    Conversation c = requireConversation(conversationId);
    if (role == UserRole.RECRUITER && c.getRecruiterUserId() != userId) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permisos sobre la conversacion");
    }
    if (role == UserRole.CANDIDATE && c.getCandidateUserId() != userId) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permisos sobre la conversacion");
    }
    return c;
  }

  private List<RecruiterConversationListItemResponse> mapRecruiterListItems(
      List<Conversation> conversations) {
    if (conversations.isEmpty()) {
      return List.of();
    }
    Map<Long, Job> jobs = loadJobs(conversations);
    Map<Long, CandidateProfile> profiles = loadProfiles(conversations);
    Map<Long, Application> applications = loadApplications(conversations);
    return conversations.stream()
        .map(
            c -> {
              Job job = jobs.get(c.getJobId());
              CandidateProfile profile = profiles.get(c.getCandidateUserId());
              Application application = applications.get(c.getApplicationId());
              LastMessageMeta meta = lastMessageMeta(c.getId());
              int unread =
                  recruiterUnreadCount(c.getId(), c.getRecruiterLastReadMessageId());
              Double matchScore =
                  application != null && application.getMatchScore() != null
                      ? application.getMatchScore().doubleValue()
                      : null;
              return new RecruiterConversationListItemResponse(
                  c.getId(),
                  c.getCandidateUserId(),
                  profile != null ? displayName(profile) : "",
                  c.getApplicationId(),
                  c.getJobId(),
                  job != null ? job.getTitle() : "",
                  meta.preview(),
                  meta.sentAt(),
                  toInstant(c.getUpdatedAt()),
                  unread,
                  matchScore);
            })
        .toList();
  }

  private List<CandidateConversationListItemResponse> mapCandidateListItems(
      List<Conversation> conversations) {
    if (conversations.isEmpty()) {
      return List.of();
    }
    Map<Long, Job> jobs = loadJobs(conversations);
    Map<Long, Application> applications = loadApplications(conversations);
    return conversations.stream()
        .map(
            c -> {
              Job job = jobs.get(c.getJobId());
              Application application = applications.get(c.getApplicationId());
              LastMessageMeta meta = lastMessageMeta(c.getId());
              int unread =
                  candidateUnreadCount(c.getId(), c.getCandidateLastReadMessageId());
              Double matchScore =
                  application != null && application.getMatchScore() != null
                      ? application.getMatchScore().doubleValue()
                      : null;
              return new CandidateConversationListItemResponse(
                  c.getId(),
                  c.getCompanyId(),
                  job != null ? job.getCompanyCommercialName() : "",
                  c.getApplicationId(),
                  c.getJobId(),
                  job != null ? job.getTitle() : "",
                  meta.preview(),
                  meta.sentAt(),
                  toInstant(c.getUpdatedAt()),
                  unread,
                  matchScore);
            })
        .toList();
  }

  private Map<Long, Job> loadJobs(List<Conversation> conversations) {
    Set<Long> ids = conversations.stream().map(Conversation::getJobId).collect(Collectors.toSet());
    return jobRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Job::getId, Function.identity()));
  }

  private Map<Long, CandidateProfile> loadProfiles(List<Conversation> conversations) {
    Set<Long> ids =
        conversations.stream().map(Conversation::getCandidateUserId).collect(Collectors.toSet());
    return candidateProfileRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(CandidateProfile::getUserId, Function.identity()));
  }

  private Map<Long, Application> loadApplications(List<Conversation> conversations) {
    Set<Long> ids =
        conversations.stream().map(Conversation::getApplicationId).collect(Collectors.toSet());
    return applicationRepository.findAllById(ids).stream()
        .collect(Collectors.toMap(Application::getId, Function.identity()));
  }

  private LastMessageMeta lastMessageMeta(long conversationId) {
    return messageRepository
        .findTopByConversationIdOrderByIdDesc(conversationId)
        .map(m -> new LastMessageMeta(preview(m.getBody()), toInstant(m.getSentAt())))
        .orElse(new LastMessageMeta("", Instant.EPOCH));
  }

  private MessageItemResponse toMessageItem(ConversationMessage m) {
    return new MessageItemResponse(
        m.getId(),
        m.getConversationId(),
        m.getSenderUserId(),
        m.getSenderRole().name(),
        m.getBody(),
        toInstant(m.getSentAt()));
  }

  private static String displayName(CandidateProfile p) {
    StringBuilder sb = new StringBuilder();
    if (p.getFirstName() != null && !p.getFirstName().isBlank()) {
      sb.append(p.getFirstName().trim());
    }
    if (p.getLastNamePaternal() != null && !p.getLastNamePaternal().isBlank()) {
      if (!sb.isEmpty()) {
        sb.append(' ');
      }
      sb.append(p.getLastNamePaternal().trim());
    }
    return sb.isEmpty() ? "Candidato" : sb.toString();
  }

  private static String preview(String body) {
    if (body == null) {
      return "";
    }
    String t = body.trim();
    if (t.length() <= PREVIEW_MAX) {
      return t;
    }
    return t.substring(0, PREVIEW_MAX - 3) + "...";
  }

  private static String normalizeBody(String raw, int max, String field) {
    if (raw == null) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "El campo " + field + " es obligatorio");
    }
    String trimmed = raw.trim();
    if (trimmed.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "El campo " + field + " es obligatorio");
    }
    if (trimmed.length() > max) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY,
          "El campo " + field + " no puede superar " + max + " caracteres");
    }
    return trimmed;
  }

  private static Instant toInstant(LocalDateTime dt) {
    if (dt == null) {
      return Instant.EPOCH;
    }
    return dt.atZone(ZoneOffset.UTC).toInstant();
  }

  private static Pageable buildConversationPageable(int page, int size, String sort) {
    int p = Math.max(1, page);
    int sz = Math.min(100, Math.max(1, size));
    return PageRequest.of(p - 1, sz, parseConversationSort(sort));
  }

  private static Sort parseConversationSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.DESC, "updatedAt");
    }
    String[] parts = raw.split(",");
    String field = parts[0].trim();
    if (field.equals("updated_at")) {
      field = "updatedAt";
    }
    Sort.Direction dir =
        parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
    return Sort.by(dir, field);
  }

  private static Sort parseMessageSort(String raw) {
    if (raw == null || raw.isBlank()) {
      return Sort.by(Sort.Direction.ASC, "sentAt");
    }
    String[] parts = raw.split(",");
    String field = parts[0].trim();
    if (field.equals("sent_at")) {
      field = "sentAt";
    }
    Sort.Direction dir =
        parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
    return Sort.by(dir, field);
  }

  private int recruiterUnreadCount(long conversationId, long lastReadMessageId) {
    return (int)
        messageRepository.countByConversationIdAndSenderRoleAndIdGreaterThan(
            conversationId, MessageSenderRole.CANDIDATE, lastReadMessageId);
  }

  private int candidateUnreadCount(long conversationId, long lastReadMessageId) {
    return (int)
        messageRepository.countByConversationIdAndSenderRoleAndIdGreaterThan(
            conversationId, MessageSenderRole.RECRUITER, lastReadMessageId);
  }

  private void markRecruiterReadToLatest(Conversation conversation) {
    messageRepository
        .findTopByConversationIdOrderByIdDesc(conversation.getId())
        .ifPresent(
            latest -> {
              conversation.markRecruiterRead(latest.getId());
              conversationRepository.save(conversation);
            });
  }

  private void markCandidateReadToLatest(Conversation conversation) {
    messageRepository
        .findTopByConversationIdOrderByIdDesc(conversation.getId())
        .ifPresent(
            latest -> {
              conversation.markCandidateRead(latest.getId());
              conversationRepository.save(conversation);
            });
  }

  private record LastMessageMeta(String preview, Instant sentAt) {}
}
