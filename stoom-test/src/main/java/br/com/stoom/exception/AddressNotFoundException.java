package br.com.stoom.exception;

public class AddressNotFoundException extends IllegalArgumentException {

    public AddressNotFoundException() {
        super("Address not found");
    }
}
