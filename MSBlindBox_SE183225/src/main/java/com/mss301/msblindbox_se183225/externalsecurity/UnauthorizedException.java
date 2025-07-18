package com.mss301.msblindbox_se183225.externalsecurity;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
