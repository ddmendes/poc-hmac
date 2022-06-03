package com.example.producer.hmacproducer.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.producer.hmacproducer.dto.ResourceDto;

@RestController
@RequestMapping("/v1/resource")
public class ResourceController {
    @GetMapping("/{resourceId}")
    public ResourceDto getResource(@PathVariable("resourceId") String resourceId) {
        ResourceDto res = new ResourceDto();
        res.setId(resourceId);
        res.setValue("xablau");
        return res;
    }
}
