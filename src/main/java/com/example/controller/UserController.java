package com.example.controller;

import com.example.model.Address;
import com.example.model.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "index";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        return "user-details";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        model.addAttribute("user", user);
        return "user-form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
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
                    (address.getHouse() == null || address.getHouse().trim().isEmpty())) {
                // Если адрес заполнен частично - тоже не сохраняем
                user.setAddress(null);
            }
        }

        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/users";
    }
}