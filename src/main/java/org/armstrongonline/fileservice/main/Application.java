package org.armstrongonline.fileservice.main;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ComponentScan(basePackages={"org.armstrongonline.fileservice.controller"})

@SpringBootApplication
@ImportResource("classpath:Beans.xml")
public class Application {
	
	//@Value("${tomcat.server.document-root}")
	//private String documentRoot;

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
      
        System.out.println("My FileService has started.");
    }
}