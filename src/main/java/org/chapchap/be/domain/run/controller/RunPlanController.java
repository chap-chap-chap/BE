package org.chapchap.be.domain.run.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.run.dto.RunPlanRequest;
import org.chapchap.be.domain.run.dto.RunPlanResponse;
import org.chapchap.be.domain.run.service.RunPlanService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
@RequiredArgsConstructor
public class RunPlanController {

    private final RunPlanService runPlanService;

    @PostMapping("/plan")
    public RunPlanResponse plan(@Valid @RequestBody RunPlanRequest req) {
        return runPlanService.plan(req);
    }
}