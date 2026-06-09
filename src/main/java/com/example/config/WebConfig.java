package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;//чтобы контейнеру получить ссылку на самого себя (метод setApplicationContext() и передать себя)
import org.springframework.context.annotation.Bean;//результат метода - бин
import org.springframework.context.annotation.ComponentScan;//@C,@S
import org.springframework.context.annotation.Configuration;// класс-конфигурация
import org.springframework.web.servlet.config.annotation.EnableWebMvc;//подкл web mvc (@getmapping, @postcontroller)
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;//из имени шаблона (index) в htlm страницу
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;//переопределить метод configureViewResolvers() (исп. thymeleaf для созд страниц)
import org.thymeleaf.spring6.SpringTemplateEngine;//движок thymeleaf  (берет html шаблон и вставляет данные из java)
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;//ищет html файлы в нужной папке
import org.thymeleaf.spring6.view.ThymeleafViewResolver;//когда Controller  возвр index надо прогнать через engine и отдать пользователю

@Configuration//настройка приложения
@EnableWebMvc//вкл обработку http запросов(@controller начинает работать+GetMapping,PostMapping обрабатывать запросы+возврат html страницы)
@ComponentScan(basePackages = "com.example.controller")//ищет контроллеры и создает их бины
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {//возможность настр SMVC, доступ к S контейнеру

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    private static final String TEMPLATES_PREFIX = "/WEB-INF/templates/";
    private static final String TEMPLATES_SUFFIX = ".html";
    private static final String TEMPLATE_MODE = "HTML";
    private static final String ENCODING = "UTF-8";
    private static final boolean CACHEABLE = false;
    private static final boolean SPRING_EL_COMPILER = true;


    private ApplicationContext applicationContext;//сюда спринг положит ссылку на контейнер(сам на себя)

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {//при создании получить ссылку на себя чтоы потом попросить доступ к файлам
        log.info("Получение ссылки на Spring контейнер");
        this.applicationContext = applicationContext;
        log.debug("ApplicationContext установлен: {}", applicationContext);

    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {//бин - решатель шаблонов (знает где лежат html файлы)
        log.info("Создание SpringResourceTemplateResolver (решатель шаблонов)");

        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();//созд новый о-решатель
        resolver.setApplicationContext(applicationContext);//дает решателю доступ к контейнеру чтобы он мог читать файлы из папки webapp

        resolver.setPrefix(TEMPLATES_PREFIX);
        resolver.setSuffix(TEMPLATES_SUFFIX);
        resolver.setTemplateMode(TEMPLATE_MODE);
        resolver.setCacheable(CACHEABLE);

        log.debug("Префикс шаблонов: {}", TEMPLATES_PREFIX);
        log.debug("Суффикс шаблонов: {}", TEMPLATES_SUFFIX);
        log.debug("Режим шаблонов: {}", TEMPLATE_MODE);
        log.debug("Кэширование: {}", CACHEABLE);
        log.info("SpringResourceTemplateResolver создан");

//        resolver.setPrefix("/WEB-INF/templates/");//в какой папке HTML файлы
//        resolver.setSuffix(".html");//расширение у всех файлов
//        resolver.setTemplateMode("HTML");//обрабатывай как html
//        resolver.setCacheable(false);//при каждом запросе читать файл заново(видеть изменения без перезапуска)
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {//движок thymeleaf. берет HTLM шаблон и сам вставляет данные из java. отдает готовую страницу
        log.info("Создание SpringTemplateEngine (движок Thymeleaf)");
        SpringTemplateEngine engine = new SpringTemplateEngine();//созд новый движок
        engine.setTemplateResolver(templateResolver());//передали решатель. движок будет его использовать когда нужно найти шаблон
        engine.setEnableSpringELCompiler(SPRING_EL_COMPILER);//для сложных выражеий в html чтобы работать быстрее
        log.info("SpringTemplateEngine создан");
        return engine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {//регистрирует Thymeleaf как основной способ превращения Java-данных в HTML страницы.
        log.info("Настройка ViewResolver (Thymeleaf)");
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();//созд. веб реселвер(имена шаблонов в готовые html страницы)
        resolver.setTemplateEngine(templateEngine());//передали движок чтобы обрабатывать шаблоны
        resolver.setCharacterEncoding(ENCODING);
        registry.viewResolver(resolver);// когда контроллер возвращает "index", Spring знает, что надо использовать ThymeleafViewResolver
        log.debug("ThymeleafViewResolver зарегистрирован");
        log.info("Thymeleaf настроен как основной ViewResolver");
    }
}