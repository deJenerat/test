package com.example.controller;

import com.example.model.Address;
import com.example.model.User;
import com.example.service.UserService;//контроллер вызывает методы сервиса для работы с бд
import org.springframework.beans.factory.annotation.Autowired;//S сам найдет бин UserService и вставит в контроллер
import org.springframework.stereotype.Controller;//класс-веб контроллер (@GetMapping, @PostMapping)
import org.springframework.ui.Model;//о, в кот мы передаем данные, чтобы передать в html шаблон (thymeleaf)(model.addAttribute("users", users) в HTML можно ${users})
import org.springframework.web.bind.annotation.*;//импорт аннтотаций @GetMapping@PostMapping@PathVariable@ModelAttribute

import java.util.List;

@Controller//обработчик веб-запросов
@RequestMapping("/users")//методы доступны по URL типа http://localhost:8080/users
public class UserController {

    @Autowired//внедрение в поле поэтому нужно
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {//обработчик get - запроса, model - конт для данных, которые пойдут в html
        List<User> users = userService.findAllUsers();//вызывает сервис, который получает
        model.addAttribute("users", users);//кладет список в модель (можно будет обратиться к нему ${users})
        return "index";//покажи страницу index.html
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {//Обработчик GET запроса на /users/id
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        return "user-details";//показывает user-details.html.
    }

    @GetMapping("/new")// /users/new
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";//Создаёт пустого пользователя, кладёт в модель, показывает форму.
    }

    @GetMapping("/{id}/edit")// Обработчик GET
    public String showEditForm(@PathVariable Long id, Model model) { //Находит пользователя по ID, кладёт в модель (с уже заполненными полями),
        User user = userService.findUserById(id)//  показывает ту же форму, но с данными.
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/save")//Обработчик POST запроса
    public String saveUser(@ModelAttribute User user) {//S сам создаст о. User и заполнит его полями из данных формы
        // Проверяем, что адрес не создаётся автоматически
        if (user.getAddress() != null) {
            Address address = user.getAddress();
            // Если все поля адреса пустые - не сохраняем адрес
            if ((address.getCity() == null || address.getCity().trim().isEmpty()) &&
                    (address.getStreet() == null || address.getStreet().trim().isEmpty()) &&
                    (address.getHouse() == null || address.getHouse().trim().isEmpty())) {
                user.setAddress(null);
            } else if ((address.getCity() == null || address.getCity().trim().isEmpty()) ||
                    (address.getStreet() == null || address.getStreet().trim().isEmpty()) ||
                    (address.getHouse() == null || address.getHouse().trim().isEmpty()))
                // Если адрес заполнен частично - тоже не сохраняем
                user.setAddress(null);
        }

        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/delete")// Обработчик GET
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/users";
    }
}