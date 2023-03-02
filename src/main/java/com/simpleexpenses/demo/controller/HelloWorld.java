package com.simpleexpenses.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello-world")
public class HelloWorld {

    @GetMapping
    public ResponseEntity<String> sayHelloWorld() {
        return ResponseEntity.ok("Hello World!");
    }

}
