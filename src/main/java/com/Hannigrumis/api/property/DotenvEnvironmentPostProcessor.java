package com.Hannigrumis.api.property;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", dotenv.get("DB_URL"));
        props.put("spring.datasource.username", dotenv.get("DB_USER"));
        props.put("spring.datasource.password", dotenv.get("DB_PASSWORD"));
        props.put("jwt.secret", dotenv.get("JWT_SECRET"));

        ConfigurableEnvironment environment = event.getEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("dotenv", props));
    }
}
