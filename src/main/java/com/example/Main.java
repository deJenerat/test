package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);//контейнер(хранит бины) S читая конфиг
        UserRep userRepository = context.getBean(UserRep.class);//достает из конт. бин, юр-через перем будем работать

//        System.out.println("Очистка базы данных и сброс ID...");
//        userRepository.clearAndResetIds();
//        System.out.println("База очищена, ID начнутся с 1\n");


        System.out.println("Создание пользователей:");
        User user1 = new User("Иван", "Иванов", 13);
        User user2 = new User("Петр", "Петров", 21);
        User user3 = new User("Захар", "Володин", 99);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        System.out.println("Создано 3 пользователя\n");

        System.out.println("Выбор всех пользователей:");
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(user -> System.out.println("   " + user));//  ????????
        System.out.println();

        System.out.println("Поиск пользователя по id");
        Long firstUserId = user1.getId();  // берём реальный ID
        System.out.println("Ищем пользователя с ID = " + firstUserId);
        userRepository.findById(firstUserId).ifPresentOrElse(
                user -> System.out.println("   Найден: " + user),
                () -> System.out.println("   Пользователь не найден")
        );
        System.out.println();

        System.out.println("Обновляем пользователя с ID = " + firstUserId);
        userRepository.updateUser(firstUserId, "Иван", "Говнович", 99);
        userRepository.findById(firstUserId).ifPresent(user ->
                System.out.println("   Обновлён: " + user));
        System.out.println();


        Long thirdUserId = user3.getId();  //берём реальный ID
        System.out.println("Удаляем пользователя с ID = " + thirdUserId);
        userRepository.deleteById(thirdUserId);
        System.out.println("   Пользователь удалён");
        System.out.println("   Осталось пользователей: " + userRepository.count());
        System.out.println();


        System.out.println("Создание пользователей с адресами:");

        Address addr1 = new Address("Москва", "Тверская", "15");
        Address addr2 = new Address("Москва", "Тверская", "15");
        Address addr3 = new Address("Омск", "Невский", "25");

        User userWithAddr1 = new User("Анна", "Смирнова", 28, addr1);
        User userWithAddr2 = new User("Анастасия", "Иванова", 32, addr2);
        User userWithAddr3 = new User("Елена", "Петрова", 27, addr3);

        userRepository.save(userWithAddr1);
        userRepository.save(userWithAddr2);
        userRepository.save(userWithAddr3);
        System.out.println("Создано 3 пользователя с адресами\n");

        System.out.println("Все пользователи с адресами:");
        List<User> allWithAddresses = userRepository.findAll();
        allWithAddresses.forEach(user -> {
            if (user.getAddress() != null) {
                System.out.printf("   • %s %s (возраст: %d) - Адрес: %s, %s, %s%n",
                        user.getFirstName(), user.getLastName(), user.getAge(),
                        user.getAddress().getCity(), user.getAddress().getStreet(), user.getAddress().getHouse());
            } else {
                System.out.printf("   • %s %s (возраст: %d) - Адрес не указан%n",
                        user.getFirstName(), user.getLastName(), user.getAge());
            }
        });
        System.out.println();

        System.out.println("Выборка пользователей, живущих в доме пользователя1 с адресом");
        String houseNumber = userWithAddr1.getAddress().getHouse();  // берём реальный номер дома
        List<User> usersInHouse = userRepository.findUsersByHouse(houseNumber);
        if (usersInHouse.isEmpty()) {
            System.out.println("   Жители не найдены");
        } else {
            System.out.println("   Жители дома " + houseNumber + ":");
            usersInHouse.forEach(user ->
                    System.out.printf("   • %s %s (ID=%d)%n",
                            user.getFirstName(), user.getLastName(), user.getId()));
        }
        System.out.println();

        System.out.println("Выборка пользователей из города пользователя1 с адресом");
        String cityName = userWithAddr1.getAddress().getCity();  // берём реальный город
        List<User> usersInCity = userRepository.findUsersByCity(cityName);
        if (usersInCity.isEmpty()) {
            System.out.println("   Жители не найдены");
        } else {
            System.out.println("   Жители " + cityName + ":");
            usersInCity.forEach(user ->
                    System.out.printf("   • %s %s (ID=%d)%n",
                            user.getFirstName(), user.getLastName(), user.getId()));
        }
        System.out.println();

        System.out.println("Удаление пользователя с адресом ");
        Long idToDelete = userWithAddr1.getId();
        userRepository.deleteById(idToDelete);
        System.out.printf("Пользователь с id=%d и его адрес удалены %n", idToDelete);
        System.out.println("Оставшиеся пользователи с адресами:");
        userRepository.findAll().forEach(user -> {
            if (user.getAddress() != null) {
                System.out.printf("   • %s %s - Адрес: %s%n",
                        user.getFirstName(), user.getLastName(), user.getAddress().getHouse());
            }
        });
        System.out.println();

        System.out.println("Удаление всех пользователей:");
        long countBefore = userRepository.count();
        userRepository.deleteAll();
        long countAfter = userRepository.count();
        System.out.printf("Удалено пользователей: %d%n", countBefore);
        System.out.printf("Осталось пользователей: %d%n", countAfter);

        ((AnnotationConfigApplicationContext) context).close();
    }
}