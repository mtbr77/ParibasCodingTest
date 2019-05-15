package com.vvorobel.bonds4all.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@Table(name = "bonds")
@Entity
public class Bond {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private int clientId;

    @Min(value=5)
    private int term;

    @Min(value=1)
    private long amount;

    private double coupon = 5.0;

    private LocalDateTime time;

    private String ip;

    public Bond() {
    }

    public Bond(int term, long amount) {
        this.term = term;
        this.amount = amount;
    }
}
