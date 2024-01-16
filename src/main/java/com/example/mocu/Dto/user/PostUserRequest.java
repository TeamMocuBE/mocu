package com.example.mocu.Dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class PostUserRequest {
    @Email(message = "email: 이메일 형식이어야 합니다")
    @NotBlank(message = "email: {NotBlank}")
    private String email;

    @NotBlank(message = "name: {NotBlank}")
    private String name;

    @NotBlank(message = "provider: {NotBlank}")
    private String provider;

    @Nullable
    private String nickname;

    @Nullable
    private String profileImage;
}
