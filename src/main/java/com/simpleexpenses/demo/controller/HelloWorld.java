package com.simpleexpenses.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hello-world")
public class HelloWorld {

    @GetMapping
    public ResponseEntity<String> sayHelloWorld() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = ((Jwt) authentication.getPrincipal()).getSubject();


        return ResponseEntity.ok("Hello World! ");
    }

}
