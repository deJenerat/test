package com.example;

import com.example.config.AppConfig;
import com.example.model.Address;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);//контейнер(хранит бины) S читая конфиг
        UserService userService = context.getBean(UserService.class);//берет готовый UserService с внедренным UserRep


        System.out.println("Создание пользователей:");
        User user1 = new User("Иван", "Иванов", 13);
        User user2 = new User("Петр", "Петров", 21);
        User user3 = new User("Захар", "Володин", 99);

        userService.saveUser(user1);
        userService.saveUser(user2);
        userService.saveUser(user3);
        System.out.println("Создано 3 пользователя\n");

        System.out.println("Выбор всех пользователей:");
        userService.findAllUsers().forEach(user -> System.out.println("   " + user));
        System.out.println();

        System.out.println("Поиск пользователя по id");
        Long firstUserId = user1.getId();  // берём реальный ID
        System.out.println("Ищем пользователя с ID = " + firstUserId);
        userService.findUserById(firstUserId).ifPresentOrElse(
                user -> System.out.println("   Найден: " + user),
                () -> System.out.println("   Пользователь не найден")
        );
        System.out.println();

        System.out.println("Обновляем пользователя с ID = " + firstUserId);
        userService.updateUser(firstUserId, "Иван", "Говнович", 99);
        userService.findUserById(firstUserId).ifPresent(user ->
                System.out.println("   Обновлён: " + user));
        System.out.println();


        Long thirdUserId = user3.getId();  //берём реальный ID
        System.out.println("Удаляем пользователя с ID = " + thirdUserId);
        userService.deleteUserById(thirdUserId);
        System.out.println("   Пользователь удалён");
        System.out.println("   Осталось пользователей: " + userService.findAllUsers().size());//**
        System.out.println();


        System.out.println("Создание пользователей с адресами:");

        Address addr1 = new Address("Москва", "Тверская", "15");
        Address addr2 = new Address("Москва", "Тверская", "15");
        Address addr3 = new Address("Омск", "Невский", "25");

        User userWithAddr1 = new User("Анна", "Смирнова", 28, addr1);
        User userWithAddr2 = new User("Анастасия", "Иванова", 32, addr2);
        User userWithAddr3 = new User("Елена", "Петрова", 27, addr3);

        userService.saveUser(userWithAddr1);
        userService.saveUser(userWithAddr2);
        userService.saveUser(userWithAddr3);
        System.out.println("Создано 3 пользователя с адресами\n");

        System.out.println("Все пользователи :");
        userService.findAllUsers().forEach((user -> {
            if (user.getAddress() != null) {
                System.out.printf("   • %s %s (возраст: %d) - Адрес: %s, %s, %s%n",
                        user.getFirstName(), user.getLastName(), user.getAge(),
                        user.getAddress().getCity(), user.getAddress().getStreet(), user.getAddress().getHouse());
            } else {
                System.out.printf("   • %s %s (возраст: %d) - Адрес не указан%n",
                        user.getFirstName(), user.getLastName(), user.getAge());
            }
        }));
        System.out.println();

        String houseNumber = userWithAddr1.getAddress().getHouse();// берём реальный номер дома
        System.out.println("Выборка пользователей, живущих в доме "+houseNumber+" (пользователя1 с адресом)");
        List<User> usersInHouse = userService.findUsersByHouse(houseNumber);
        if (usersInHouse.isEmpty()) {
            System.out.println("   Жители не найдены");
        } else {
            usersInHouse.forEach(user ->
                    System.out.printf("   • %s %s (ID=%d)%n",
                            user.getFirstName(), user.getLastName(), user.getId()));
        }
        System.out.println();


        String cityName = userWithAddr1.getAddress().getCity(); // берём реальный город
        System.out.println("Выборка пользователей из города " +cityName+" (пользователя1 с адресом)");
        List<User> usersInCity = userService.findUsersByCity(cityName);
        if (usersInCity.isEmpty()) {
            System.out.println("   Жители не найдены");
        } else {
            usersInCity.forEach(user ->
                    System.out.printf("   • %s %s (ID=%d)%n",
                            user.getFirstName(), user.getLastName(), user.getId()));
        }
        System.out.println();

        System.out.println("Удаление пользователя с адресом ");
        Long idToDelete = userWithAddr1.getId();
        userService.deleteUserById(idToDelete);
        System.out.printf("Пользователь с id=%d и его адрес удалены %n", idToDelete);
        System.out.println("Оставшиеся пользователи с адресами:");
        userService.findAllUsers().forEach(user -> {
            if (user.getAddress() != null) {
                System.out.printf("   • %s %s - Адрес: %s, %s, %s%n",
                        user.getFirstName(), user.getLastName(), user.getAddress().getCity(),
                        user.getAddress().getStreet(),user.getAddress().getHouse());
            }
        });
        System.out.println();

//        System.out.println("Удаление всех пользователей:");
//        long countBefore = userService.findAllUsers().size();
//        userService.deleteAllUsers();
//        long countAfter = userService.findAllUsers().size();
//        System.out.printf("Удалено пользователей: %d%n", countBefore);
//        System.out.printf("Осталось пользователей: %d%n", countAfter);

        ((AnnotationConfigApplicationContext) context).close();
    }
}