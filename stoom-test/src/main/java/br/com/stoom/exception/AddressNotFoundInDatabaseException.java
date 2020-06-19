package br.com.stoom.exception;

public class AddressNotFoundInDatabaseException extends IllegalArgumentException {

    public AddressNotFoundInDatabaseException() {
        super("Address not found");
    }
}
