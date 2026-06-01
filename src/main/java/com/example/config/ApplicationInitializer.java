package com.example.config;

import org.springframework.web.WebApplicationInitializer;//класс реализующий интерф.автоматом обнаруживается томкетом
import org.springframework.web.context.ContextLoaderListener;//слушатель контейнера при старте томкета
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;//аннотации вместо xml
import org.springframework.web.filter.CharacterEncodingFilter;//кодировка для русских буков
import org.springframework.web.servlet.DispatcherServlet;//главный сервлет, что бы все запросы отправлялись в нужные контроллеры


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {//томкет вызывает метод при запуске
        //servletContext - это объект, через который мы регистрируем сервлеты и фильтры

        // создаём корневой контекст (AppConfig)

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);//возьми класс AppConfig и создай из него бины

        servletContext.addListener(new ContextLoaderListener(rootContext));
        //регистрируем слушатель, который связывает корневой контейнер с Tomcat.
        //теперь при старте приложения контейнер загрузится автоматически

        //создаём веб-контекст (WebConfig)
        //контейнер для веб-слоя (контроллеры, Thymeleaf)
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);
        webContext.setParent(rootContext);//связь с webConfig

        //Регистрируем DispatcherServlet(главный) и регистрируем в томкэт
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);//сразу после старта
        dispatcher.addMapping("/");//все запросы направляем сюда

        //фильтр кодировки
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter("characterEncodingFilter", encodingFilter)
                .addMappingForUrlPatterns(null, false, "/*");
    }
}