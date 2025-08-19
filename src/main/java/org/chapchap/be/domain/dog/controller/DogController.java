package org.chapchap.be.domain.dog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.dog.dto.DogProfileRequest;
import org.chapchap.be.domain.dog.service.DogService;
import org.chapchap.be.global.response.ResponseBuilder;
import org.chapchap.be.global.response.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dogs")
@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @Operation(summary = "강아지 프로필 등록")
    @PostMapping("/profile")
    public ResponseEntity<ResponseMessage> registerDogs(
            Authentication auth,
            HttpServletRequest httpReq,
            @Valid @RequestBody DogProfileRequest req
    ) {
        String ownerEmail = auth.getName();
        int saved = dogService.registerDogsForUserEmail(ownerEmail, req);
        String msg = (saved == 0) ? "등록된 강아지가 없습니다." : ("강아지 프로필 " + saved + "건 등록 완료");
        return ResponseBuilder.ok(httpReq, msg);
    }
}
