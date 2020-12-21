package com.proximity.vending.admin.rest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestRestController {

    @GetMapping
    public String index() {
        return "Hello from Admin";
    }
}
