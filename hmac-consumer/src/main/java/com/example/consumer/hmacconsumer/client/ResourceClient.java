package com.example.consumer.hmacconsumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.consumer.hmacconsumer.dto.ResourceDto;

@FeignClient(value="resourceClient", url="localhost:8081")
public interface ResourceClient {
    @RequestMapping(method = RequestMethod.GET, value = "/v1/resource/{resourceId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResourceDto getResourceById(@PathVariable("resourceId") String resourceId);
}
