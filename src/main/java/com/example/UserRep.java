package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository//работа с БД+Sbean +Перехват ошибок
public interface UserRep extends JpaRepository<User, Long> {

/*    // Базовые CRUD
userRep.save(user);                    // сохранить/обновить
userRep.saveAll(users);                // сохранить несколько
userRep.findById(1L);                  // найти по id
userRep.findAll();                     // найти всех
userRep.findAllById(ids);              // найти по списку id
userRep.existsById(1L);                // проверить существование
userRep.count();                       // количество записей

// Удаление
userRep.deleteById(1L);                // удалить по id
userRep.delete(user);                  // удалить объект
userRep.deleteAll();                   // удалить всех
userRep.deleteAll(users);              // удалить список

*/

    // Обновление пользователя
    @Modifying//изм.данные(не список)
    @Transactional//запрос - в транзакции
    @Query("UPDATE User u SET u.firstName = :firstName, u.lastName = :lastName, u.age = :age WHERE u.id = :id")//HQL
    int updateUser(@Param("id") Long id,
                   @Param("firstName") String firstName,
                   @Param("lastName") String lastName,
                   @Param("age") Integer age);//нет готового в jparep


    // Выборка всех пользователей, которые живут в одном доме
    @Query("SELECT u FROM User u WHERE u.address.house = :house")
    List<User> findUsersByHouse(@Param("house") String house);//Param сам подставится


    // Дополнительный метод: поиск пользователей по городу
    @Query("SELECT u FROM User u WHERE u.address.city = :city")
    List<User> findUsersByCity(@Param("city") String city);//Param сам

    /*@Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE users_id_seq RESTART WITH 1", nativeQuery = true)
    void resetIdSequence();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM users", nativeQuery = true)
    void deleteAllNative();

    default void clearAndResetIds() {
        deleteAllNative();           // удаляем все записи
        resetIdSequence();
    }*/
    }