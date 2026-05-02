package cl.duoc.worksi.security;

import cl.duoc.worksi.entity.User;
import cl.duoc.worksi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WorksiUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public WorksiUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmailIgnoreCase(email.trim())
            .orElseThrow(() -> new UsernameNotFoundException(email));
    return new UserPrincipal(user);
  }

  public UserDetails loadUserById(long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("id:" + id));
    return new UserPrincipal(user);
  }
}
