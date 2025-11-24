package com.pulsewatch.api.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class TestController {



    @GetMapping("test")
    public ResponseEntity<String> test(@RequestParam String msg){
        return ResponseEntity.ok("success");
    }
}
