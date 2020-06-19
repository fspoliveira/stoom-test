package br.com.stoom.google.service;

public class InvalidAddressInformation extends IllegalArgumentException {

    public InvalidAddressInformation() {
        super("Invalid address information to retrieve latitude and longitude from google");
    }
}
