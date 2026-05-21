package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRep extends JpaRepository<User, Long> {

    // Кастомные методы поиска
    List<User> findByAddressHouse(String house);
    List<User> findByAddressCity(String city);
}

