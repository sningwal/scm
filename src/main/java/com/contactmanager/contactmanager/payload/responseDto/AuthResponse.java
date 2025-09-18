package com.contactmanager.contactmanager.payload.responseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;
    private Boolean isVerified;
    private String role;
    private Boolean success;
    private String message;
}
