package cl.duoc.worksi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.worksi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmailIgnoreCase(String email);
}
