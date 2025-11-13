package com.example.storage.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/file/private/test")
    @PreAuthorize("hasAuthority('FILE_READ')")
    public String privateTest() {
        return "✅ Access granted to private file API!";
    }

    @GetMapping("/api/file/public/test")
    public String publicTest() {
        return "🌐 Public file API working!";
    }

    @GetMapping("/private/test")
    public String testPrivate(Authentication auth) {
        System.out.println(auth.getAuthorities()); // xem Spring nhận authority gì
        return "ok";
    }
}
