package com.example;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class MainWeb {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();//создает новый веб сервер
        tomcat.setPort(8080);
        tomcat.getConnector();

        // Проверка пути
        File webappDir = new File("src/main/webapp");//указ. томкету где файлы
        if (!webappDir.exists()) {
            System.exit(1);
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", webappDir.getAbsolutePath());

        // Добавляем классы в classpath Tomcat (чтобы найти usercontroller, userservice и тд)
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            ctx.setResources(resources);

        }

        tomcat.start();
        System.out.println("Сервер запущен: http://localhost:8080/users");
        tomcat.getServer().await();
    }
}