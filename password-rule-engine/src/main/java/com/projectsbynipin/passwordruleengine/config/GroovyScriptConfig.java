package com.projectsbynipin.passwordruleengine.config;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class GroovyScriptConfig {
    @Bean
    public CompilerConfiguration compilerConfiguration() {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        SecureASTCustomizer secureASTCustomizer = new SecureASTCustomizer();
        secureASTCustomizer.setAllowedImports(Collections.emptyList());
        secureASTCustomizer.setAllowedReceiversClasses(
                List.of(
                        String.class,
                        Math.class
                )
        );
        compilerConfiguration.addCompilationCustomizers(secureASTCustomizer);
        return compilerConfiguration;
    }
}
