package cl.duoc.worksi.service;

import org.springframework.stereotype.Service;
import cl.duoc.worksi.exception.EmailAlreadyExistsException;
import cl.duoc.worksi.repository.UserRepository;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void validateEmailAvailable(String email) {
    if (userRepository.existsByEmailIgnoreCase(email)) {
      throw new EmailAlreadyExistsException(email);
    }
  }
}
