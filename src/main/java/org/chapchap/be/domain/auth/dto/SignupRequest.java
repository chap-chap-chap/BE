package org.chapchap.be.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

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
        String name,

        @Schema(description = "프로필", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Valid
        ProfileDto profile
) {
        @Schema(description = "프로필")
        public record ProfileDto(
                @Schema(description = "체중(kg)", example = "68.5") @Positive Double weightKg,
                @Schema(description = "키(cm)", example = "175") @Positive Integer heightCm,
                @Schema(description = "나이(만)", example = "30") @Positive Integer age,
                @Schema(description = "성별(MALE/FEMALE)", example = "MALE") String sex // "MALE" | "FEMALE"
        ) {}
}
