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
    ROOM_NOT_AVAILABLE(409, "The room is not available", HttpStatus.CONFLICT),
    SHOW_TIME_SEAT_EXISTED(409, "Showtime seat existed", HttpStatus.CONFLICT),
    SHOW_TIME_SEAT_NOT_EXISTED(409, "Showtime seat not existed", HttpStatus.NOT_FOUND),
    SEAT_ALREADY_HELD_OR_BOOKED(409, "Seat already held or booked", HttpStatus.CONFLICT),
    TICKET_NOT_EXISTED(404, "Ticket not existed", HttpStatus.NOT_FOUND),
    UNABLE_TO_PAY(400, "I can not pay for seat", HttpStatus.BAD_REQUEST),
    INVALID_TITLE_FORMAT(400, "Title invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(400, "Password do not match", HttpStatus.BAD_REQUEST),
    TICKET_SOLD_OUT(400, "Tickets are sold out", HttpStatus.NOT_FOUND),

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
