package com.vvorobel.bonds4all.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Table(name = "bonds")
@Audited
@Entity
public class Bond implements Cloneable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @NotAudited
    private int id;

    @NotAudited
    private int clientId;

    @Min(value=5, message="Term should be >= 5")
    private int term;

    @Min(value=1)
    @NotAudited
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

    public Bond(int term) {
        this.term = term;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
