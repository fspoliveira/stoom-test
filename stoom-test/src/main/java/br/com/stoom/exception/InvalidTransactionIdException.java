package br.com.stoom.exception;

public class InvalidTransactionIdException extends RuntimeException {

    public static final String MESSAGE = "Invalid Request. A valid one must have a transaction id in header!";

    public InvalidTransactionIdException() {
        super(MESSAGE);
    }
}
