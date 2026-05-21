package com.example.service;

import com.example.model.User;
import com.example.model.Address;
import com.example.repository.UserRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRep userRepository;

    // CREATE / UPDATE
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    // UPDATE (через save)
    public User updateUser(Long id, String firstName, String lastName, Integer age) {
        Optional<User> existing = userRepository.findById(id);
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

    public boolean existsByAddressId(Long addressId) {
        return userRepository.existsById(addressId);
    }
}