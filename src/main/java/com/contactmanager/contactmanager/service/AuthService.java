package com.contactmanager.contactmanager.service;
import com.contactmanager.contactmanager.entity.User;
import com.contactmanager.contactmanager.entity.type.AuthProviderType;
import com.contactmanager.contactmanager.exception.UserExistException;
import com.contactmanager.contactmanager.payload.requestDto.LoginRequest;
import com.contactmanager.contactmanager.payload.requestDto.SignUpRequest;
import com.contactmanager.contactmanager.payload.requestDto.SignUpResponse;
import com.contactmanager.contactmanager.payload.responseDto.ApiError;
import com.contactmanager.contactmanager.payload.responseDto.ApiResponse;
import com.contactmanager.contactmanager.payload.responseDto.AuthResponse;
import com.contactmanager.contactmanager.payload.responseDto.ResponseUtil;
import com.contactmanager.contactmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public ApiResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException(loginRequest.getUsername()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        String token = authUtil.generateAccessToken(user);

        if(authentication.isAuthenticated()){
           AuthResponse authResponse =  AuthResponse.builder()
                    .name(user.getName())
                    .email(user.getUsername())
                    .accessToken(token)
//                    .refreshToken("refreshToken")
//                    .isVerified(Boolean.TRUE)
                    .success(Boolean.TRUE)
                    .role("ADMIN")
                    .message("Sign in successful")
                    .build();
//            Integer statusCode, String message, Object data, Object metadata
            return ResponseUtil.success(200,"Sign in successful", authResponse,null);
        }

        AuthResponse authResponse = AuthResponse.builder()
                .message("User Not Authenticated!")
                .success(false)
                .build();

        return ResponseUtil.error(401,"User Not Authenticated!",authResponse, null,null);
    }
    public User signUpInternal(SignUpRequest signupRequestDto, AuthProviderType authProviderType, String providerId) {
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);
        if(user != null)
            throw new UserExistException("User already exists");

        user = User.builder()
                .username(signupRequestDto.getUsername())
                .name(signupRequestDto.getName())
                .providerId(providerId)
                .providerType(authProviderType)
//                .roles(signupRequestDto.getRoles()) // Role.PATIENT
                .build();

        if(authProviderType == AuthProviderType.EMAIL) {
            user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        }
        user = userRepository.save(user);
        return user;
    }

    public SignUpResponse signup(SignUpRequest signupRequestDto) {
        User user = signUpInternal(signupRequestDto, AuthProviderType.EMAIL, null);
        return new SignUpResponse(user.getId(), user.getUsername());
    }
    @Transactional
    public ApiResponse handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User emailUser = userRepository.findByUsername(email).orElse(null);

        if (user == null && emailUser == null) {
            // signup flow:
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signUpInternal(new SignUpRequest(name, username, name, Set.of("Admin")), providerType, providerId);
        } else if (user != null) {
            if (email != null && !email.isBlank() && !email.equals(user.getUsername())) {
                user.setUsername(email);
                user.setName(name);
                userRepository.save(user);
            }
        } else {
            throw new BadCredentialsException("This email is already registered with provider " + emailUser.getProviderType());
        }
        String token = authUtil.generateAccessToken(user);
        AuthResponse authResponse = AuthResponse.builder()
                .name(user.getName())
                .email(user.getUsername())
                .accessToken(token)
//                    .refreshToken("refreshToken")
//                    .isVerified(Boolean.TRUE)
                .success(Boolean.TRUE)
                .role("ADMIN")
                .message("Sign in successful")
                .build();

//        return ResponseEntity.ok(authResponse);
        return ResponseUtil.success(HttpStatus.UNAUTHORIZED.value(),"Sign in successfully",authResponse,null);
    }
}
