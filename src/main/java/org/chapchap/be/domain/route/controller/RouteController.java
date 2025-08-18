package org.chapchap.be.domain.route.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.chapchap.be.domain.route.dto.RouteComputeRequest;
import org.chapchap.be.domain.route.dto.RouteComputeResponse;
import org.chapchap.be.domain.route.service.GoogleRoutesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/route")
public class RouteController {

    private final GoogleRoutesService routes;

    @PostMapping("/compute")
    public RouteComputeResponse compute(@RequestBody @Valid RouteComputeRequest req) {
        return routes.computeRoute(req);
    }

    @PostMapping("/walk")
    public RouteComputeResponse walk(@RequestBody @Valid RouteComputeRequest req) {
        return routes.computeRoute(new RouteComputeRequest(req.origin(), req.destination(), "WALK"));
    }

    @PostMapping("/drive")
    public RouteComputeResponse drive(@RequestBody @Valid RouteComputeRequest req) {
        return routes.computeRoute(new RouteComputeRequest(req.origin(), req.destination(), "DRIVE"));
    }
}