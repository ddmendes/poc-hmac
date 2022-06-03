package com.example.consumer.hmacconsumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.consumer.hmacconsumer.dto.ResourceDto;

@FeignClient(value="resourceclient", url="localhost:8081")
public interface ResourceClient {
    @RequestMapping(method = RequestMethod.GET, value = "/v1/resource/{resourceId}", consumes = "application/json")
    ResourceDto getResourceById(@PathVariable("resourceId") String resourceId);
}
