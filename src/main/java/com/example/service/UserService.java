package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.model.User;
import com.example.repository.UserRep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional//публичные методы в транзакцию(begin-commit/rollback) над методом
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRep userRepository;//объекты-реализация методов интерфейса(ссылка на о. в куче)

    public UserService(UserRep userRepository) {
        this.userRepository = userRepository;
        log.info("UserService создан, UserRep внедрён через конструктор");
    }


    // CREATE / UPDATE
    public User saveUser(User user) {
        if (user.getId() == null) {
            log.info("Сохранение нового пользователя: {} {}", user.getFirstName(), user.getLastName());
        } else {
            log.info("Обновление пользователя с id={}: {} {}", user.getId(), user.getFirstName(), user.getLastName());
        }

        User savedUser = userRepository.save(user);
        log.debug("Пользователь сохранён с id={}", savedUser.getId());

        return savedUser;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {//класс-оболочка. О. либо содержится либо empty
        return userRepository.findById(id);
    }


    public void deleteUserById(Long id) {
        log.info("Удаление пользователя с id={}", id);
        userRepository.deleteById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // через метод репозитория
    public List<User> findUsersByHouse(String house) {
        return userRepository.findByAddressHouse(house);
    }

    public List<User> findUsersByCity(String city) {
        return userRepository.findByAddressCity(city);
    }

}