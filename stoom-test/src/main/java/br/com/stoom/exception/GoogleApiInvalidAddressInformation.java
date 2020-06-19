package br.com.stoom.exception;

public class GoogleApiInvalidAddressInformation extends IllegalArgumentException {

    public GoogleApiInvalidAddressInformation() {
        super("Invalid address information to retrieve latitude and longitude from google");
    }
}
