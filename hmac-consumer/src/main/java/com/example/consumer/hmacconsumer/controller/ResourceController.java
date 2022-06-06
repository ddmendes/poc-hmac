package com.example.consumer.hmacconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.consumer.hmacconsumer.client.ResourceClient;
import com.example.consumer.hmacconsumer.dto.ResourceDto;

@RestController
@RequestMapping("/v1/consumer/resource")
public class ResourceController {

    @Autowired
    private ResourceClient resourceClient;

    @GetMapping("/{resourceId}")
    public ResourceDto getResourceById(@PathVariable String resourceId) {
        return resourceClient.getResourceById(resourceId);
    }
}
