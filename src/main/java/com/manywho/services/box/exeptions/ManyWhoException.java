package com.manywho.services.box.exeptions;

public class ManyWhoException extends RuntimeException {
    private int statusCode;
    private String reasonPhrase;

    public ManyWhoException(int statusCode, String reasonPhrase) {
        super("There was a problem with the call to ManyWho: " + reasonPhrase);

        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
