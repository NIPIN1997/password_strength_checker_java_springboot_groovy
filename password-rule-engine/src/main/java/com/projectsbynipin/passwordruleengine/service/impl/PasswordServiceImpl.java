package com.projectsbynipin.passwordruleengine.service.impl;

import com.projectsbynipin.passwordruleengine.dto.ApiResponse;
import com.projectsbynipin.passwordruleengine.exception.FailedToCheckPasswordException;
import com.projectsbynipin.passwordruleengine.service.PasswordService;
import com.projectsbynipin.passwordruleengine.service.engine.GroovyRuleManager;
import com.projectsbynipin.passwordruleengine.utils.ApiResponseCreator;
import com.projectsbynipin.passwordruleengine.utils.Constants;
import groovy.lang.Binding;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final GroovyRuleManager groovyRuleManager;
    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceImpl.class);

    public PasswordServiceImpl(GroovyRuleManager groovyRuleManager) {
        this.groovyRuleManager = groovyRuleManager;
    }

    @Override
    public ApiResponse<?> checkPasswordStrength(String password) {
        try {
            Map<String, Class<?>> scripts = groovyRuleManager.getScripts();
            if (scripts.isEmpty()) {
                throw new FileNotFoundException("No script found.");
            }
            List<String> messages = new ArrayList<>();
            boolean passwordStrong = true;
            for (Class<?> scriptClass : scripts.values()) {
                Script script = (Script) scriptClass.getDeclaredConstructor().newInstance();
                Binding binding = new Binding();
                binding.setVariable("password", password);
                script.setBinding(binding);
                script.run();
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
            logger.error(e.getMessage());
            throw new FailedToCheckPasswordException(Constants.FAILED_TO_CHECK_PASSWORD);
        }
    }
}
