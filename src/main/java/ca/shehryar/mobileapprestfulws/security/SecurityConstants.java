package ca.shehryar.mobileapprestfulws.security;

import ca.shehryar.mobileapprestfulws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_DATE = 864000000; // 10d
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000 * 60 * 60; // 1h
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";

    public static String getTokenSecret() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }
}
