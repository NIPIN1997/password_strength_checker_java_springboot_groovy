package com.projectsbynipin.passwordruleengine.service.engine;

import groovy.lang.GroovyClassLoader;
import lombok.Setter;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardWatchEventKinds.*;


@Component
public class GroovyRuleManager implements InitializingBean {

    @Setter
    @Value("${scripts.path}")
    public String scriptPath;

    private final CompilerConfiguration compilerConfiguration;
    private AtomicReference<Map<String, Class<?>>> scripts = new AtomicReference<>(new ConcurrentHashMap<>());
    private static final Logger logger = LoggerFactory.getLogger(GroovyRuleManager.class);
    private final GroovyClassLoader groovyClassLoader;

    public GroovyRuleManager(CompilerConfiguration compilerConfiguration) {
        this.compilerConfiguration = compilerConfiguration;
        this.groovyClassLoader = new GroovyClassLoader(GroovyRuleManager.class.getClassLoader(), compilerConfiguration);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        addScripts();
    }

    public synchronized void addScripts() throws FileNotFoundException {
        File folder = new File(scriptPath);
        if (folder.exists()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".groovy"));
            if (files != null && files.length > 0) {
                Map<String, Class<?>> temp = new ConcurrentHashMap<>();
                for (File file : files) {
                    groovyCompiler(file, temp);
                }
                scripts.set(Collections.unmodifiableMap(temp));
            } else {
                logger.error("Groovy script files not found.");
                throw new FileNotFoundException("Groovy script files not found.");
            }
        } else {
            logger.error("Script path not found");
            throw new FileNotFoundException("Script path not found.");
        }
    }

    public void groovyCompiler(File file, Map<String, Class<?>> temp) {
        try {
            temp.put(file.getName(), groovyClassLoader.parseClass(file));
        } catch (MultipleCompilationErrorsException | SecurityException e) {
            logger.error("Security Exception : {} :- {}", file.getName(), e.getMessage());
            throw new SecurityException("Malicious Groovy script file.");
        } catch (Exception e) {
            logger.error("Failed to parse groovy script file : {} :- {}", file.getName(), e.getMessage());
        }
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void fileWatcher() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path path = Paths.get(scriptPath);
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            while (true) {
                WatchKey watchKey = watchService.take();
                boolean removeCache = false;
                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    String fileName = event.context().toString();
                    if (fileName.endsWith(".groovy")) {
                        removeCache = true;
                        break;
                    }
                }
                if (removeCache) {
                    groovyClassLoader.clearCache();
                    addScripts();
                }
                if (!watchKey.reset()) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error in file watcher : {}", e.getMessage());
        }
    }

    public Map<String, Class<?>> getScripts() {
        return scripts.get();
    }
}
