package com.projectsbynipin.passwordruleengine.service.impl;

import com.projectsbynipin.passwordruleengine.dto.ApiResponse;
import com.projectsbynipin.passwordruleengine.exception.FailedToCheckPasswordException;
import com.projectsbynipin.passwordruleengine.service.PasswordService;
import com.projectsbynipin.passwordruleengine.utils.ApiResponseCreator;
import com.projectsbynipin.passwordruleengine.utils.Constants;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Setter
    @Value("${scripts.path}")
    public String scriptPath;

    @Override
    public ApiResponse<?> checkPasswordStrength(String password) {
        try {
            File folder = new File(scriptPath);
            if (!folder.exists()) {
                throw new FailedToCheckPasswordException(Constants.FAILED_TO_CHECK_PASSWORD);
            }
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".groovy"));
            if (files == null || files.length == 0) {
                throw new FailedToCheckPasswordException(Constants.FAILED_TO_CHECK_PASSWORD);
            }
            Binding binding = new Binding();
            binding.setVariable("password", password);
            GroovyShell groovyShell = new GroovyShell(binding);
            List<String> messages = new ArrayList<>();
            boolean passwordStrong = true;
            for (File file : files) {
                groovyShell.evaluate(file);
                boolean isEnabled = (boolean) binding.getVariable("enabled");
                if (isEnabled) {
                    boolean isPassed = (boolean) binding.getVariable("passed");
                    if (!isPassed) {
                        messages.add((String) binding.getVariable("message"));
                        passwordStrong = false;
                    }
                }
            }
            if (passwordStrong) {
                return ApiResponseCreator.success(passwordStrong, Constants.PASSWORD_CHECKED);
            } else {
                return ApiResponseCreator.success(passwordStrong, Constants.PASSWORD_CHECKED, messages);
            }
        } catch (Exception e) {
            throw new FailedToCheckPasswordException(Constants.FAILED_TO_CHECK_PASSWORD);
        }

    }
}
