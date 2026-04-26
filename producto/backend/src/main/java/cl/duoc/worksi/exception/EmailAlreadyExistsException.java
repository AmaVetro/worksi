package cl.duoc.worksi.exception;

public class EmailAlreadyExistsException extends RuntimeException {
  public EmailAlreadyExistsException(String email) {
    super("El correo ya existe: " + email);
  }
}
