package com.roomiematcher.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * This class handles data initialization for the application.
 * It can be used to run the initialization script when needed.
 */
@Configuration
public class DataInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.init-data:false}")
    private boolean shouldInitData;

    /**
     * Initialize data if the app.init-data property is set to true
     */
    @PostConstruct
    public void initializeData() {
        if (shouldInitData) {
            try {
                System.out.println("Initializing sample data...");
                
                // Read the init-data.sql file
                ClassPathResource resource = new ClassPathResource("init-data.sql");
                Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                String sql = FileCopyUtils.copyToString(reader);
                
                // Execute the SQL script
                jdbcTemplate.execute(sql);
                
                System.out.println("Sample data initialization completed successfully");
            } catch (Exception e) {
                System.err.println("Error initializing sample data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sample data initialization skipped (set app.init-data=true to initialize)");
        }
    }
} 