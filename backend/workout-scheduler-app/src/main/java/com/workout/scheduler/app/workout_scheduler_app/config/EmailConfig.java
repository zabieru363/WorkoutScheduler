package com.workout.scheduler.app.workout_scheduler_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    /**
     * Configuración de propiedades para enviar correos con Gmail.
     * @return El mapa properties con las propiedades necesarias para enviar correos.
     */
    private Properties getMailProperties() {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return properties;
    }

    /**
     * Bean que proporciona una implementación de JavaMailSender para enviar correos.
     * Ya incluye las propiedades necesarias para enviar correos con Gmail.
     * @return La implementación de JavaMailSender
     */
    @Bean
    JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setJavaMailProperties(getMailProperties());
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        return mailSender;
    }

    /**
     * Otro bean necesario para enviar correos.
     * @return La implementación de ResourceLoader
     */
    @Bean
    ResourceLoader resourceLoader() {
        return new DefaultResourceLoader();
    }

}