package com.micky.uploader.utils;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationContextEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = Logger.getLogger(getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        AliyunOSSConfig config = contextRefreshedEvent.getApplicationContext().getBean(AliyunOSSConfig.class);
        if (config != null) {
            try {
                AliyunOSSClient.getInstance().init(config);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
