package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class MainWeb {
    private static final Logger log = LoggerFactory.getLogger(MainWeb.class);
    private static final int PORT = 8080;
    private static final String WEBAPP_PATH = "src/main/webapp";
    private static final String CONTEXT_PATH = "/";
    private static final String CLASSES_PATH = "/WEB-INF/classes";

    public static void main(String[] args) throws Exception {
        log.info("Запуск встроенного Tomcat сервера");
        Tomcat tomcat = new Tomcat();//создает новый веб сервер
        tomcat.setPort(PORT);
        tomcat.getConnector();
        log.debug("Tomcat настроен на порт: {}", PORT);

        // Проверка пути
        File webappDir = new File(WEBAPP_PATH);//указ. томкету где файлы
        if (!webappDir.exists()) {
            log.error("Папка webapp не найдена по пути: {}", webappDir.getAbsolutePath());
            System.exit(1);
        }
        log.debug("Папка webapp найдена: {}", webappDir.getAbsolutePath());

        StandardContext ctx = (StandardContext) tomcat.addWebapp(CONTEXT_PATH, webappDir.getAbsolutePath());

        // Добавляем классы в classpath Tomcat (чтобы найти usercontroller, userservice и тд)
        File classesDir = new File("target/classes");
        if (classesDir.exists()) {
            log.debug("Папка с классами найдена: {}", classesDir.getAbsolutePath());

            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, CLASSES_PATH,
                    classesDir.getAbsolutePath(), CONTEXT_PATH));
            ctx.setResources(resources);
            log.debug("Классы добавлены в classpath Tomcat");


        }

        tomcat.start();
        log.info("Сервер запущен: http://localhost:{}/users", PORT);
        tomcat.getServer().await();
    }
}