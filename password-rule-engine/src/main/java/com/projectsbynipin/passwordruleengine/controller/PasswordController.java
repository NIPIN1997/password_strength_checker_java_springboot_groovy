package com.projectsbynipin.passwordruleengine.controller;

import com.projectsbynipin.passwordruleengine.dto.ApiResponse;
import com.projectsbynipin.passwordruleengine.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Password Controller",
        description = "API to check the strength of the password."
)
@RestController
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Operation(
            summary = "Check password strength",
            description = "Evaluates the strength of a password based on dynamic groovy rules. Returns whether the password passed all rules and if not provides messages explaining which rules failed."
    )
    @PostMapping(path = "/check-password-strength")
    public ResponseEntity<ApiResponse<?>> checkPasswordStrength(@RequestParam("password") String password) {
        ApiResponse<?> apiResponse = passwordService.checkPasswordStrength(password);
        return ResponseEntity.ok(apiResponse);
    }
}
