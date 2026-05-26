package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRep extends JpaRepository<User, Long> {//тип сущности, тип id

    // Кастомные методы поиска
    List<User> findByAddressHouse(String house);//SELECT u FROM User u JOIN u.address a WHERE a.house = ?1
    List<User> findByAddressCity(String city);
}

