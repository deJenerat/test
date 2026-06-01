package com.example.config;

import com.zaxxer.hikari.HikariConfig;//импорт для пула соединений(настройка)
import com.zaxxer.hikari.HikariDataSource;//пул
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;//о-кот управляет спринг (о для S-контейнера)
import org.springframework.context.annotation.Configuration;//класс содержит бины
import org.springframework.core.io.ClassPathResource;//читает файл из class path
import org.springframework.core.io.support.PropertiesLoaderUtils;//загружает пропы
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;//вкл работу с бд через jpa (созд реализации интерфейсов)
import org.springframework.orm.jpa.JpaTransactionManager;//упр транз
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;//фабрика для работы с бд
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;//посредник для хибера чтобы бд понимала запросы и давала ответы в нужном виде
import org.springframework.transaction.PlatformTransactionManager;//интерф для транз
import org.springframework.transaction.annotation.EnableTransactionManagement;//вкл подд транз(или полная тр или ролбэк)(UserService)

import javax.sql.DataSource;//инт для подкл к БД чтобы С понимал, какой о. дает подк
import java.io.IOException;
import java.util.Properties;

@Configuration//класс с настройками(источник определения бинов для AC)
@ComponentScan(basePackages = "com.example")//уточнение где сканировать @C,@S,@R,@Controller и созд бины
@EnableJpaRepositories("com.example")//вкл sJPA ->ищи репоз. тут (UserRep)(S создаст реализ интерф, реал JPAR и пос в AC)
@EnableTransactionManagement//вкл транзакции(@Transactional) в сервисах
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean//создание бина подключения к БД
    public DataSource dataSource() throws IOException {
        // создает о. которым будет управлять S (возвр о. который умеет создавать соед)
            Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
            HikariConfig config = new HikariConfig();//обект настр. пула соед(кеш готовых соед)
            config.setDriverClassName(props.getProperty("jdbc.driver"));
            config.setJdbcUrl(props.getProperty("jdbc.url"));
            config.setUsername(props.getProperty("jdbc.username"));
            config.setPassword(props.getProperty("jdbc.password"));
            return new HikariDataSource(config);//созд и возвр пул с этими настр

    }

    @Bean//бин превращения Java-объектов в таблицы
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) throws IOException {
        {//( ...S сам найдет м передаст бин )
            LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();//о.-фабрикка
            emf.setDataSource(dataSource);//передача в фабрику пул, чтобы Hiber знал как подкл. к БД
            emf.setPackagesToScan("com.example"); // ищем @Entity в папке

            HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();//адаптер чтобы настроить Hiber внутри S
            emf.setJpaVendorAdapter(adapter);//передает адаптер в фабрику
            Properties jpaProperties = PropertiesLoaderUtils.loadProperties(
                    (new ClassPathResource("application.properties")));
            emf.setJpaProperties(jpaProperties);

            return emf;//возвр фабрику спрингу
        }
    }
    @Bean//бин транзакций - как работать с бд(begin-commit-rollback) автоматом оборачивает в транзакцию где аннотация
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {//emf сам найдет и передаст
        JpaTransactionManager tm = new JpaTransactionManager();//о. управления транзакциями(JPA реализация)
        tm.setEntityManagerFactory(emf.getObject());//getObject() извлекает настоящий EntityManagerFactory из бина
        return tm;//возвр менеджер транз. спрингу
    }
}