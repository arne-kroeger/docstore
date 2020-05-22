package dev.docstore.documentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.docstore"})
public class PerfActDocumentationApp {
    
    public static void main(String[] args) {
        SpringApplication.run(PerfActDocumentationApp.class, args);
    }

}