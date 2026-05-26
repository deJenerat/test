package com.example.model;//модели данных (JPA-сущности)

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;//get,set,eq,hash,toS
import lombok.NoArgsConstructor;
import lombok.ToString;//исключить поле

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

    @OneToOne(mappedBy = "address")//ведомая - для удобства. поиск по ключу в address. адрес не знает о владельце,связь настроена уже в др классе
    @ToString.Exclude//Address.toString() не включает информацию о юзере чтобы не было беск.цикла(у юзера-у адреса-у юзера)т.к. связь двусторонняя Lombok
    private User user;//обратная ссылка на владельца адреса чтобы мы могли найти ЮЗЕРА по адресу

    public Address(String city, String street, String house) {//чтобы не писать... new (null,...,null)
        this.city = city;
        this.street = street;
        this.house = house;
    }
}