package ccrm.exception;

public class DuplicateEnrollmentException extends Exception {
    public DuplicateEnrollmentException(String message) {
        super(message);
    }
    
    public DuplicateEnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}