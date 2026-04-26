package cl.duoc.worksi.exception;

public class InvalidRutException extends RuntimeException {
    public InvalidRutException(String rut) {
        super("RUT invalido: " + rut);
    }
}