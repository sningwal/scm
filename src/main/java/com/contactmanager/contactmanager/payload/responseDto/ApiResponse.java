package com.contactmanager.contactmanager.payload.responseDto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * This class defines the schema of the response. It is used to encapsulate data prepared by
 * the server side, this object will be serialized to JSON before sent back to the client end.
 */
/****/
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String status; // Two values: success means success, error means not success
    private String message; // Response message
    private Object data; // The response payload
    private Object errors;
    private Integer statusCode; // Status code. e.g., 200
    private Object metadata;
    private Instant timestamp = Instant.now();

//    public Result(boolean status, Integer statusCode, String message) {
//        this.status = status;
//        this.statusCode = statusCode;
//        this.message = message;
//    }
//
//    public Result(boolean flag, Integer code, String message, Object data) {
//        this.flag = flag;
//        this.code = code;
//        this.message = message;
//        this.data = data;
//    }
}