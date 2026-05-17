package com.example;

import com.zaxxer.hikari.HikariConfig;//импорт для пула соединений
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;//о-кот управляет спринг
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;//вкл работу с бд через jpa
import org.springframework.orm.jpa.JpaTransactionManager;//упр транз
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;//фабрика
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;//интерф для транз
import org.springframework.transaction.annotation.EnableTransactionManagement;//вкл подд транз

import javax.sql.DataSource;//инт для подкл к БД чтобы С понимал, какой о. дает подк

@Configuration//класс с настройками
@EnableJpaRepositories("com.example")//вкл sJPA ->ищи репоз. тут (UserRep)
@EnableTransactionManagement//вкл транзакции(@Transactional)
public class AppConfig {

    @Bean//создание бина подключения к БД
    public DataSource dataSource() {// создает о. которым будет управлять S (возвр о. который умеет создавать соед)
        HikariConfig config = new HikariConfig();//обект настр. пула соед
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/testdb");
        config.setUsername("name");
        config.setPassword("1");
        config.setDriverClassName("org.postgresql.Driver");//как общаться с БД
        return new HikariDataSource(config);//созд и возвр пул с этими настр
    }

    @Bean//бин превращения Java-объектов в таблицы
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {//( ...S сам найдет м передаст бин )
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();//о.-фабрикка
        emf.setDataSource(dataSource);//передача в фабрику пул, чтобы Hiber знал как подкл. к БД
        emf.setPackagesToScan("com.example"); // ищем @Entity в папке

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();//адаптер чтобы настроить Hiber внутри S
        adapter.setGenerateDdl(true);   // Hiber создаст таблицы автоматически при запуске
        adapter.setShowSql(false);      //  не показываем SQL запросы
        emf.setJpaVendorAdapter(adapter);//передает адаптер в фабрику

        return emf;//возвр фабрику спрингу
    }

    @Bean//бин транзакций - как работать с бд(begin-commit-rollback)
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {//emf сам найдет и передаст
        JpaTransactionManager tm = new JpaTransactionManager();//о. управления транзакциями
        tm.setEntityManagerFactory(emf.getObject());//передает в менеджер фабрику и достает фабрики настоящий EntityManagerFactory
        return tm;//возвр менеджер транз. спрингу
    }
}