package backend.StockMicroservice.exception;

public class ExternalServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExternalServiceException(String serviceName, Throwable cause) {
        super("\n\nFalha na comunicação com " + serviceName + ": " + cause.getMessage(), cause);
    }
}
