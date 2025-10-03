package com.socialmedia.petTreff.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
    private Mail mail = new Mail();

    public static class Mail {
        private String from;
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
    }

    public Mail getMail() { return mail; }
    public void setMail(Mail mail) { this.mail = mail; }
}