package cl.duoc.worksi.exception;

public class InvalidCommuneRegionException extends RuntimeException {
    public InvalidCommuneRegionException() {
        super("Comuna no pertenece a la region");
    }
}
