package com.ywcheong.short4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class testController {
    @GetMapping("/hello")
    public String helloWorld(){
        return "Hello world!";
    }
}
