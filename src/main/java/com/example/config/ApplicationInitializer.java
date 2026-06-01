package com.example.config;

import org.springframework.web.WebApplicationInitializer;//класс реализующий интерф.автоматом обнаруживается томкетом
import org.springframework.web.context.ContextLoaderListener;//слушатель контейнера при старте томкета
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;//аннотации вместо xml
import org.springframework.web.filter.CharacterEncodingFilter;//кодировка для русских буков
import org.springframework.web.servlet.DispatcherServlet;//главный сервлет, что бы все запросы отправлялись в нужные контроллеры


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

public class ApplicationInitializer implements WebApplicationInitializer {

    private static final String DISPATCHER = "dispatcher";

    @Override
    public void onStartup(ServletContext servletContext) {//томкет вызывает метод при запуске(о через который мы рег. сервлеты и фильтр)
        //servletContext - это объект, через который мы регистрируем сервлеты и фильтры

        // создаём корневой контекст (AppConfig)

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);//возьми класс AppConfig и создай из него бины

        servletContext.addListener(new ContextLoaderListener(rootContext));
        //регистрируем слушатель, который связывает корневой контейнер с Tomcat.
        //теперь при старте приложения контейнер загрузится автоматически

        //создаём веб-контекст (WebConfig)
        //контейнер для веб-слоя (контроллеры, Thymeleaf, ViewResolver)
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);
        webContext.setParent(rootContext);//связь webConfig(видит корневой)

        //Регистрируем DispatcherServlet(главный) и регистрируем в томкэт реализация frontController
        ServletRegistration.Dynamic servlet=servletContext.addServlet(DISPATCHER, new DispatcherServlet(webContext));
        servlet.setLoadOnStartup(1);//сразу после старта(LOAD_ON_STARTUP)
        servlet.addMapping("/");//все запросы направляем сюда(URL_PATTERN)

        //фильтр кодировки
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter("characterEncodingFilter", encodingFilter)
                .addMappingForUrlPatterns(null, false, "/*");//ALL_URLS
    }
}