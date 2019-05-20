package com.vvorobel.bonds4all.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Table(name = "clients")
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String surname;

    @Email
    private String email;

    public Client() {
    }

    public Client(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
