package com.example.ecollege.api.exceptions;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class RestMessage {
    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private Map<String, String> messages;


    public RestMessage(HttpStatus status, String message, Map<String, String> messages) {

        this.status = status;
        LocalDateTime localDateTime = LocalDateTime.now();
        this.timestamp = localDateTime.minusNanos(localDateTime.getNano()).minusSeconds(localDateTime.getSecond());
        this.message = message;
        this.messages = messages;
    }

}