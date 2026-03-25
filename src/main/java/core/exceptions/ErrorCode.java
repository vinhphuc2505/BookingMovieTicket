package core.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    KEY_INVALID(500, "Key invalid", HttpStatus.NOT_FOUND),
    FIELD_IS_NOT_EMPTY(400, "You must not leave this field blank", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(400, "Email invalid", HttpStatus.BAD_REQUEST),
    USER_OR_EMAIL_EXISTED(409, "User or email existed", HttpStatus.CONFLICT),
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    MOVIE_NOT_EXISTED(404, "Movie not existed", HttpStatus.NOT_FOUND),
    ROOM_NOT_EXISTED(404, "Room not existed", HttpStatus.NOT_FOUND),
    SEAT_EXISTED(409, "Seat number existed", HttpStatus.CONFLICT),
    SEAT_NOT_EXISTED(409, "Seat number not existed", HttpStatus.NOT_FOUND),
    SHOW_TIME_NOT_EXISTED(409, "Show time not existed", HttpStatus.NOT_FOUND),
    ROOM_NOT_AVAILABLE(409, "The room is not available", HttpStatus.CONFLICT)

    ;

    private final int errorCode;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(int errorCode, String message, HttpStatusCode httpStatusCode) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
