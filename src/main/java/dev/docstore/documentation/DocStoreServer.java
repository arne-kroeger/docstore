package dev.docstore.documentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"dev.docstore"})
@EnableScheduling
public class DocStoreServer {
    
    public static void main(String[] args) {
        SpringApplication.run(DocStoreServer.class, args);
    }

}