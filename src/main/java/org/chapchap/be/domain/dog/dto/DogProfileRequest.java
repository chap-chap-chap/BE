package org.chapchap.be.domain.dog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "강아지 프로필 등록 요청")
public record DogProfileRequest(
        @Schema(description = "등록할 강아지 목록", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull @Size(min = 1, message = "최소 1마리는 등록해야 합니다.")
        List<@Valid DogDto> dogs
) {
    @Schema(description = "강아지 프로필 등록 단위 DTO")
    public record DogDto(
            @Schema(description = "이름", example = "콩이") @NotBlank String name,
            @Schema(description = "품종", example = "푸들") String breed,
            @Schema(description = "체중(kg)", example = "6.2") @Positive Double weightKg,
            @Schema(description = "나이(개월)", example = "24") @Positive Integer ageMonths
    ) {}
}