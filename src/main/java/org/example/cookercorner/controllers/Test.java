package org.example.cookercorner.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
public class Test {

    @GetMapping("/test")
    public String test(){
        return "Hello from secured endpoint";
    }
}
