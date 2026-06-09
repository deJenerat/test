package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;//класс реализующий интерф.автоматом обнаруживается томкетом
import org.springframework.web.context.ContextLoaderListener;//слушатель контейнера при старте томкета
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;//аннотации вместо xml
import org.springframework.web.filter.CharacterEncodingFilter;//кодировка для русских буков
import org.springframework.web.servlet.DispatcherServlet;//главный сервлет, что бы все запросы отправлялись в нужные контроллеры


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;

public class ApplicationInitializer implements WebApplicationInitializer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInitializer.class);

    private static final String DISPATCHER = "dispatcher";
    private static final String ENCODING_FILTER = "characterEncodingFilter";
    private static final String ENCODING = "UTF-8";
    private static final String URL_PATTERN = "/";
    private static final String ALL_URLS = "/*";
    private static final int LOAD_ON_STARTUP = 1;

    @Override
    public void onStartup(ServletContext servletContext) {//томкет вызывает метод при запуске(о через который мы рег. сервлеты и фильтр)
        //servletContext - это объект, через который мы регистрируем сервлеты и фильтры

        log.info("Инициализация Spring WebApplicationInitializer");
        log.debug("Создание корневого контекста (AppConfig)");
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);//возьми класс AppConfig и создай из него бины
        log.debug("AppConfig зарегистрирован в корневом контексте");


        servletContext.addListener(new ContextLoaderListener(rootContext));
        log.debug("ContextLoaderListener зарегистрирован");

        //регистрируем слушатель, который связывает корневой контейнер с Tomcat.
        //теперь при старте приложения контейнер загрузится автоматически

        //создаём веб-контекст (WebConfig)
        //контейнер для веб-слоя (контроллеры, Thymeleaf, ViewResolver)
        log.debug("Создание веб-контекста (WebConfig)");
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);
        webContext.setParent(rootContext);//связь webConfig(видит корневой)
        log.debug("WebConfig зарегистрирован, родительский контекст = rootContext");

        log.info("Регистрация DispatcherServlet");
        //Регистрируем DispatcherServlet(главный) и регистрируем в томкэт реализация frontController
        ServletRegistration.Dynamic servlet=servletContext.addServlet(DISPATCHER, new DispatcherServlet(webContext));
        servlet.setLoadOnStartup(LOAD_ON_STARTUP);//сразу после старта(LOAD_ON_STARTUP)
        servlet.addMapping(URL_PATTERN);//все запросы направляем сюда(URL_PATTERN)
        log.debug("DispatcherServlet зарегистрирован на URL: {}", URL_PATTERN);
        log.debug("Загрузка при старте: {}", LOAD_ON_STARTUP);

        //фильтр кодировки
        log.info("Регистрация фильтра кодировки");
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding(ENCODING);
        encodingFilter.setForceEncoding(true);
        servletContext.addFilter(ENCODING_FILTER, encodingFilter)
                .addMappingForUrlPatterns(null, false, ALL_URLS);//ALL_URLS
        log.debug("Фильтр кодировки {} зарегистрирован на URL: {}", ENCODING_FILTER, ALL_URLS);
        log.debug("Кодировка: {}, принудительно: {}", ENCODING, true);
        log.info("Инициализация завершена успешно");
    }
}