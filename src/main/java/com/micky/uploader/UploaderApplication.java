package com.micky.uploader;

import com.micky.uploader.utils.ApplicationContextEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class UploaderApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UploaderApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(UploaderApplication.class);
        app.addListeners(new ApplicationContextEventListener());
        app.run(args);
    }
}