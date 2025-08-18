package org.chapchap.be.domain.route.controller;

import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.route.dto.RouteRequest;
import org.chapchap.be.domain.route.dto.RouteResponse;
import org.chapchap.be.domain.route.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/walk")
    public ResponseEntity<RouteResponse> walk(@RequestBody RouteRequest req) {
        return ResponseEntity.ok(routeService.computeWalk(req));
    }
}
