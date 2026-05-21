package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user_address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String street;

    @Column(nullable = false, length = 20)
    private String house;

    @OneToOne(mappedBy = "address")//ведомая сторона-для удобства. поиск по ключу в address. адрес не знает о владельце
    @ToString.Exclude//Address.toString() не включает информацию о юзере чтобы не было беск.цикла
    private User user;

    public Address(String city, String street, String house) {//чтобы не писать... new (null,...,null)
        this.city = city;
        this.street = street;
        this.house = house;
    }
}