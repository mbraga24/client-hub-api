package com.clienthub;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloudController {
    record Cloud(boolean inTheSky, String message) {}

    @GetMapping("/cloud")
    public Cloud getCloud() {
        return new Cloud(true, "App works!");
    }

    @GetMapping("/v2/cloud")
    public Cloud getCloudV2() {
        return new Cloud(true, "App works!");
    }
}
