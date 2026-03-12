package com.projectsbynipin.passwordruleengine.service;

import com.projectsbynipin.passwordruleengine.dto.ApiResponse;

public interface PasswordService {
    ApiResponse<?> checkPasswordStrength(String password);
}
