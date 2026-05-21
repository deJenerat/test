package com.example.config;

import com.zaxxer.hikari.HikariConfig;//импорт для пула соединений
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;//о-кот управляет спринг
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;//вкл работу с бд через jpa
import org.springframework.orm.jpa.JpaTransactionManager;//упр транз
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;//фабрика
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;//интерф для транз
import org.springframework.transaction.annotation.EnableTransactionManagement;//вкл подд транз

import javax.sql.DataSource;//инт для подкл к БД чтобы С понимал, какой о. дает подк
import java.io.IOException;
import java.util.Properties;

@Configuration//класс с настройками
@ComponentScan(basePackages = "com.example")//**
@EnableJpaRepositories("com.example")//вкл sJPA ->ищи репоз. тут (UserRep)
@EnableTransactionManagement//вкл транзакции(@Transactional)
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean//создание бина подключения к БД
    public DataSource dataSource() throws IOException {
        {// создает о. которым будет управлять S (возвр о. который умеет создавать соед)
            Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
            HikariConfig config = new HikariConfig();//обект настр. пула соед
            config.setDriverClassName(props.getProperty("jdbc.driver"));
            config.setJdbcUrl(props.getProperty("jdbc.url"));
            config.setUsername(props.getProperty("jdbc.username"));
            config.setPassword(props.getProperty("jdbc.password"));
            return new HikariDataSource(config);//созд и возвр пул с этими настр
        }
    }

    @Bean//бин превращения Java-объектов в таблицы
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) throws IOException {
        {//( ...S сам найдет м передаст бин )
            Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"));
            LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();//о.-фабрикка
            emf.setDataSource(dataSource);//передача в фабрику пул, чтобы Hiber знал как подкл. к БД
            emf.setPackagesToScan("com.example"); // ищем @Entity в папке

            HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();//адаптер чтобы настроить Hiber внутри S
            adapter.setGenerateDdl(true);   // Hiber создаст таблицы автоматически при запуске
            adapter.setShowSql(Boolean.parseBoolean(props.getProperty("hibernate.show_sql"))); //  не показываем SQL запросы
            emf.setJpaVendorAdapter(adapter);//передает адаптер в фабрику

            return emf;//возвр фабрику спрингу
        }
    }
    @Bean//бин транзакций - как работать с бд(begin-commit-rollback)
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {//emf сам найдет и передаст
        JpaTransactionManager tm = new JpaTransactionManager();//о. управления транзакциями
        tm.setEntityManagerFactory(emf.getObject());//передает в менеджер фабрику и достает фабрики настоящий EntityManagerFactory
        return tm;//возвр менеджер транз. спрингу
    }
}