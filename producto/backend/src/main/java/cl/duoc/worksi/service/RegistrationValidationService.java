package cl.duoc.worksi.service;

import cl.duoc.worksi.dto.CandidateRegistrationValidateRequest;
import cl.duoc.worksi.exception.InvalidCommuneRegionException;
import cl.duoc.worksi.exception.InvalidEmailFormatException;
import cl.duoc.worksi.exception.InvalidPasswordPolicyException;
import cl.duoc.worksi.exception.InvalidRutException;
import cl.duoc.worksi.exception.MissingRequiredFieldException;
import cl.duoc.worksi.repository.CommuneRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationValidationService {
  private final CommuneRepository communeRepository;
  private final UserService userService;

  public RegistrationValidationService(
      CommuneRepository communeRepository, UserService userService) {
    this.communeRepository = communeRepository;
    this.userService = userService;
  }

  public void validate(CandidateRegistrationValidateRequest request) {
    validateRequiredFields(request);
    validateEmailFormat(request.getEmail());
    validatePasswordPolicy(request.getPassword());
    validateRut(request.getRut());
    validateCommuneBelongsToRegion(request.getCommuneId(), request.getRegionId());
    userService.validateEmailAvailable(request.getEmail());
  }

  private void validateRequiredFields(CandidateRegistrationValidateRequest request) {
    if (isBlank(request.getEmail())) {
      throw new MissingRequiredFieldException("email");
    }
    if (isBlank(request.getPassword())) {
      throw new MissingRequiredFieldException("password");
    }
    if (isBlank(request.getFirstName())) {
      throw new MissingRequiredFieldException("first_name");
    }
    if (isBlank(request.getLastNamePaternal())) {
      throw new MissingRequiredFieldException("last_name_paternal");
    }
    if (isBlank(request.getLastNameMaternal())) {
      throw new MissingRequiredFieldException("last_name_maternal");
    }
    if (isBlank(request.getPhone())) {
      throw new MissingRequiredFieldException("phone");
    }
    if (isBlank(request.getRut())) {
      throw new MissingRequiredFieldException("rut");
    }
    if (isBlank(request.getDocumentNumber())) {
      throw new MissingRequiredFieldException("document_number");
    }
    if (request.getRegionId() == null) {
      throw new MissingRequiredFieldException("region_id");
    }
    if (request.getCommuneId() == null) {
      throw new MissingRequiredFieldException("commune_id");
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void validatePasswordPolicy(String password) {
    boolean hasMinLength = password.length() >= 12;
    boolean hasUppercase = password.matches(".*[A-Z].*");
    boolean hasLowercase = password.matches(".*[a-z].*");
    boolean hasDigit = password.matches(".*\\d.*");
    boolean hasSymbol = password.matches(".*[^a-zA-Z0-9].*");

    if (!(hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSymbol)) {
      throw new InvalidPasswordPolicyException();
    }
  }

  private void validateEmailFormat(String email) {
    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
      throw new InvalidEmailFormatException();
    }
  }

  private void validateCommuneBelongsToRegion(Long communeId, Long regionId) {
    if (!communeRepository.existsByIdAndRegionId(communeId, regionId)) {
      throw new InvalidCommuneRegionException();
    }
  }

  private void validateRut(String rut) {
    if (!isValidRut(rut)) {
      throw new InvalidRutException(rut);
    }
  }

  private boolean isValidRut(String rut) {
    if (rut == null) {
      return false;
    }

    String normalized = rut.replace(".", "").replace("-", "").trim().toUpperCase();
    if (normalized.length() < 2) {
      return false;
    }

    String numberPart = normalized.substring(0, normalized.length() - 1);
    char dv = normalized.charAt(normalized.length() - 1);

    if (!numberPart.matches("\\d+")) {
      return false;
    }

    int sum = 0;
    int multiplier = 2;
    for (int i = numberPart.length() - 1; i >= 0; i--) {
      sum += Character.getNumericValue(numberPart.charAt(i)) * multiplier;
      multiplier = (multiplier == 7) ? 2 : multiplier + 1;
    }

    int mod11 = 11 - (sum % 11);
    char expectedDv;
    if (mod11 == 11) {
      expectedDv = '0';
    } else if (mod11 == 10) {
      expectedDv = 'K';
    } else {
      expectedDv = Character.forDigit(mod11, 10);
    }

    return dv == expectedDv;
  }
}
