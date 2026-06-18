package com.GameHubStore.review_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8092")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable("id") Long id);
}
