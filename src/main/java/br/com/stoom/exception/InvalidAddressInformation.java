package br.com.stoom.exception;

public class InvalidAddressInformation extends IllegalArgumentException {

    public InvalidAddressInformation() {
        super("Invalid address information to retrieve latitude and longitude from google");
    }
}
