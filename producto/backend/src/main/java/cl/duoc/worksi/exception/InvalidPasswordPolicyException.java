package cl.duoc.worksi.exception;

public class InvalidPasswordPolicyException extends RuntimeException {
    public InvalidPasswordPolicyException() {
        super("La contraseña no cumple la politica requerida.");
    }
}