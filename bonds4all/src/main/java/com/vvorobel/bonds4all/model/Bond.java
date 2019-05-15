package com.vvorobel.bonds4all.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Table(name = "bonds")
@Entity
public class Bond {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private int clientId;

    @Size(min=5)
    private int term;

    @Size(min=1)
    private long amount;

    private double coupon = 5.0;

    private LocalDateTime time;

    private String ip;

    public Bond() {
    }

    public Bond(@Size(min = 5) int term, @Size(min = 1) long amount) {
        this.term = term;
        this.amount = amount;
    }
}
