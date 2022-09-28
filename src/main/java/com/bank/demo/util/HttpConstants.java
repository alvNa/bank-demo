package com.bank.demo.util;

public final class HttpConstants {

    private HttpConstants() {
        throw new IllegalStateException(String.valueOf(this.getClass()));
    }

    public static final String API_KEY_HEADER = "Api-Key";
    public static final String TIME_ZONE_HEADER = "X-Time-Zone";
    public static final String AUTH_SCHEMA_HEADER = "Auth-Schema";

    public static final String ACCOUNT_BASE = "/api/gbs/banking/v4.0";
}
