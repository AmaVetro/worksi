package cl.duoc.worksi.exception;

public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException() {
        super("Formato invalido");
    }
}
