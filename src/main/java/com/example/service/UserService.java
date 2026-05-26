package com.example.service;

import com.example.model.User;
import com.example.model.Address;
import com.example.repository.UserRep;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor//синглтон
@Transactional//публичные методы в транзакцию(begin-commit/rollback) над методом
public class UserService {

    //автоматически найдет бин UserRep и вставит сюда. Без @Autowired  UserRep userRepository = new UserRepImpl();
    private final UserRep userRepository;//объекты-реализация методов интерфейса(ссылка на о. в куче)

//    UserService userService = new UserService(); null
//    userService.userRepository = context.getBean(UserRep.class); не null
//    userService.saveUser(user);метод воркает

//   или через конструктор без Autowired @Service
//    public class UserService {
//        private final UserRep userRepository;  // final
//
//        public UserService(UserRep userRepository) {
//            this.userRepository = userRepository;
//        }
//    }

    // CREATE / UPDATE
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {//класс-оболочка. О. либо содержится либо empty
        return userRepository.findById(id);
    }

    // UPDATE (через save)
    public User updateUser(Long id, String firstName, String lastName, Integer age) {
        Optional<User> existing = userRepository.findById(id);//класс оболочка(контейнер)или объект User или empty
        if (existing.isPresent()) {
            User user = existing.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUserById(Long id) {
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