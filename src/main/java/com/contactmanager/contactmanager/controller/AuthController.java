package com.contactmanager.contactmanager.controller;

import com.contactmanager.contactmanager.payload.requestDto.LoginRequest;
import com.contactmanager.contactmanager.payload.requestDto.SignUpRequest;
import com.contactmanager.contactmanager.payload.responseDto.ApiResponse;
import com.contactmanager.contactmanager.payload.responseDto.AuthResponse;
import com.contactmanager.contactmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.contactmanager.contactmanager.exception.StatusCode.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequestDto) {
            return ResponseEntity.ok(authService.login(loginRequestDto));
        }
        @PostMapping("/signup")
        public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signupRequestDto) {
            return ResponseEntity.ok(authService.signup(signupRequestDto));
        }
}
