package com.example;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class MainWeb {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        // Проверка пути
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            System.err.println("ERROR: webapp directory not found at " + webappDir.getAbsolutePath());
            System.exit(1);
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", webappDir.getAbsolutePath());

        // Добавляем классы в classpath Tomcat
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        } else {
            System.err.println("WARNING: target/classes not found. Run 'mvn compile' first.");
        }

        tomcat.start();
        System.out.println("Tomcat is listening on port: " + tomcat.getConnector().getPort());
        System.out.println("Local address: " + tomcat.getConnector().getProperty("address"));
        System.out.println("========================================");
        System.out.println("Сервер запущен: http://localhost:8080/users");
        System.out.println("========================================");
        tomcat.getServer().await();
    }
}