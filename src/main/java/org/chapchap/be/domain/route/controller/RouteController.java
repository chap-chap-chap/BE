package org.chapchap.be.domain.route.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.route.dto.RouteRequest;
import org.chapchap.be.domain.route.dto.RouteResponse;
import org.chapchap.be.domain.route.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Route")
@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @Operation(summary = "산책 경로")
    @PostMapping("/walk")
    public ResponseEntity<RouteResponse> walk(@RequestBody RouteRequest req) {
        return ResponseEntity.ok(routeService.computeWalk(req));
    }
}
