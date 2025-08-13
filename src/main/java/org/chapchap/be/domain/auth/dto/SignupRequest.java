package org.chapchap.be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "사용자 회원가입 요청 DTO")
public record SignupRequest(
        @Schema(description = "이메일", example = "test@example.com")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @Schema(description = "비밀번호", example = "password123")
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @Schema(description = "이름", example = "testuser")
        @NotBlank(message = "이름은 필수입니다.")
        String name
) {}
