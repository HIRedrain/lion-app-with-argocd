package app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
    @GetMapping("/api/status")
    public String statusTest() {
        return "status - 2025.01.09";
    }
}
