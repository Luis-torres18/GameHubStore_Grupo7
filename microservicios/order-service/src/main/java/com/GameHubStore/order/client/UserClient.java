package com.GameHubStore.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url= "http://localhost:8083")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable ("id") long id);
}
