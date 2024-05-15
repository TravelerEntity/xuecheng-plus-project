package com.xuecheng.content.feignclient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "search")
public interface SearchServiceClient {
    // @PostMapping("/search/index/course")
    // Boolean add(@RequestBody CourseIndex courseIndex);
}
