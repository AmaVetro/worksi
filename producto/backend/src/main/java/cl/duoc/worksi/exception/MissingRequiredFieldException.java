package cl.duoc.worksi.exception;

public class MissingRequiredFieldException extends RuntimeException {
    private final String field;

    public MissingRequiredFieldException(String field) {
        super("Completa este campo");
        this.field = field;
    }

    public String getField() {
        return field;
    }
}