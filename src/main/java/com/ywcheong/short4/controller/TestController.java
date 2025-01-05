package com.ywcheong.short4.controller;

import com.ywcheong.short4.utility.WordDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    final WordDictionary generator;

    @Autowired
    public TestController(WordDictionary generator) {
        this.generator = generator;
    }

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello world!";
    }

    @GetMapping("/dict")
    public Map<String, List<String>> getDict() {
        return generator.getDictionaryOfLanguage();
    }
}
