package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.model.Address;
import com.example.model.User;
import com.example.service.UserService;//контроллер вызывает методы сервиса для работы с бд
import org.springframework.stereotype.Controller;//класс-веб контроллер (@GetMapping, @PostMapping)
import org.springframework.ui.Model;//о, в кот мы передаем данные, чтобы передать в html шаблон (thymeleaf)(model.addAttribute("users", users) в HTML можно ${users})
import org.springframework.web.bind.annotation.*;//импорт аннтотаций @GetMapping@PostMapping@PathVariable@ModelAttribute

import java.util.List;

@Controller//обработчик веб-запросов
@RequestMapping("/users")//методы доступны по URL типа http://localhost:8080/users
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;  // final - неизменяемая ссылка

    // Внедрение через конструктор (без @Autowired)
    public UserController(UserService userService) {
        this.userService = userService;
        log.info("UserController создан, UserService внедрён через конструктор");
    }

    @GetMapping
    public String listUsers(Model model) {//обработчик get - запроса, model - конт для данных, которые пойдут в html
        log.info("GET /users - запрос списка всех пользователей");
        List<User> users = userService.findAllUsers();//вызывает сервис, который получает
        log.debug("Найдено пользователей в БД: {}", users.size());
        model.addAttribute("users", users);//кладет список в модель (можно будет обратиться к нему ${users})
        log.debug("Список пользователей добавлен в модель под именем 'users'");
        return "index";//покажи страницу index.html
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {//Обработчик GET запроса на /users/id
        log.info("GET /users/{} - запрос деталей пользователя", id);
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        log.debug("Пользователь добавлен в модель под именем 'user'");
        return "user-details";//показывает user-details.html.
    }

    @GetMapping("/new")// /users/new
    public String showCreateForm(Model model) {
        log.info("GET /users/new - отображение формы создания пользователя");
        model.addAttribute("user", new User());
        log.debug("Создан пустой объект User и добавлен в модель");
        return "user-form";//Создаёт пустого пользователя, кладёт в модель, показывает форму.
    }

    @GetMapping("/{id}/edit")// Обработчик GET
    public String showEditForm(@PathVariable Long id, Model model) { //Находит пользователя по ID, кладёт в модель (с уже заполненными полями),
        log.info("GET /users/{}/edit - отображение формы редактирования пользователя", id);
        User user = userService.findUserById(id)//  показывает ту же форму, но с данными.
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        log.debug("Пользователь добавлен в модель под именем 'user'");
        return "user-form";
    }

    @PostMapping("/save")//Обработчик POST запроса
    public String saveUser(@ModelAttribute User user) {//S сам создаст о. User и заполнит его полями из данных формы
        // Проверяем, что адрес не создаётся автоматически
        if (user.getAddress() != null) {
            Address address = user.getAddress();
            // Если все поля адреса пустые - не сохраняем адрес
            log.debug("Проверка адреса: город='{}', улица='{}', дом='{}'",
                    address.getCity(), address.getStreet(), address.getHouse());
                if ((address.getCity() == null || address.getCity().trim().isEmpty()) &&
                    (address.getStreet() == null || address.getStreet().trim().isEmpty()) &&
                    (address.getHouse() == null || address.getHouse().trim().isEmpty())) {
                user.setAddress(null);
                log.debug("Все поля адреса пустые - адрес не будет сохранён");
            } else if ((address.getCity() == null || address.getCity().trim().isEmpty()) ||
                    (address.getStreet() == null || address.getStreet().trim().isEmpty()) ||
                    (address.getHouse() == null || address.getHouse().trim().isEmpty())) {
                // Если адрес заполнен частично - тоже не сохраняем
                log.warn("Адрес заполнен частично - не сохраняем (город='{}', улица='{}', дом='{}')",
                        address.getCity(), address.getStreet(), address.getHouse());
                user.setAddress(null);
            }
        }

        userService.saveUser(user);
        log.info("Пользователь успешно сохранён");
        return "redirect:/users";
    }

    @GetMapping("/{id}/delete")// Обработчик GET
    public String deleteUser(@PathVariable Long id) {
        log.info("GET /users/{}/delete - удаление пользователя", id);
        userService.deleteUserById(id);
        log.info("Пользователь с id={} успешно удалён", id);
        return "redirect:/users";
    }

    @PostMapping("/delete-all")
    public String deleteAllUsers() {
        log.warn("POST /users/delete-all - удаление ВСЕХ пользователей");
        userService.deleteAllUsers();
        return "redirect:/users";
    }
    @GetMapping("/search/house")
    public String searchByHouse(@RequestParam String house, Model model) {
        log.info("GET /users/search/house - поиск по дому: {}", house);
        List<User> users = userService.findUsersByHouse(house);
        model.addAttribute("users", users);
        model.addAttribute("searchType", "Дому: " + house);
        return "search-results";
    }
    @GetMapping("/search/city")
    public String searchByCity(@RequestParam String city, Model model) {
        log.info("GET /users/search/city - поиск по городу: {}", city);
        List<User> users = userService.findUsersByCity(city);
        model.addAttribute("users", users);
        model.addAttribute("searchType", "Городу: " + city);
        return "search-results";
    }
}