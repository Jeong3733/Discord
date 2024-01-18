package discord.api.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "4001", "JWT EXCEPTION"),
    JWT_AUTHORIZATION_HEADER_INVALID(HttpStatus.BAD_REQUEST, "4002", "INVALID AUTHORIZATION HEADER"),
    JWT_UNSUPPORTED(HttpStatus.BAD_REQUEST, "4003", "UNSUPPORTED JWT TOKEN"),
    JWT_ILLEGAL_CLAIM(HttpStatus.BAD_REQUEST, "4004", "ILLEGAL JWT CLAIM"),
    JWT_REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "4005", "INVALID REFRESH TOKEN"),
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "4006", "DUPLICATED EMAIL"),
    VERIFICATION_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "4007", "VERIFICATION TOKEN NOT FOUND"),


    // 401 Unauthorized
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, "4011", "MALFORMED JWT"),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "4012", "INVALID JWT SIGNATURE"),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "4013", "EXPIRED JWT TOKEN"),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "4014", "LOGIN FAILED"),

    // 404 Not Found
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "EMAIL NOT FOUND"),
    PASSWORD_INVALID(HttpStatus.NOT_FOUND, "4041", "PASSWORD INVALID"),

    // 500 Internal Server Error
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "5001", "MAIL SEND FAIL"),
    VERIFICATION_TOKEN_DUPLICATION(HttpStatus.INTERNAL_SERVER_ERROR, "5002", "VERIFICATION TOKEN DUPLICATION"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5003", "INTERNAL SERVER ERROR");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
