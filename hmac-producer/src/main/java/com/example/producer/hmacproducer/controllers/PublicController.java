package com.example.producer.hmacproducer.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.producer.hmacproducer.dto.ResourceDto;

@RestController
@RequestMapping("/v1/public")
public class PublicController {

    @GetMapping("/hello")
    public ResourceDto getHello() {
        ResourceDto res = new ResourceDto();
        res.setId("h3ll0");
        res.setValue("Hello World");
        return res;
    }
}
