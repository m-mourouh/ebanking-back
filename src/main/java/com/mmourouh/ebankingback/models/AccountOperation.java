package com.mmourouh.ebankingback.models;

import com.mmourouh.ebankingback.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(value = EnumType.STRING)
    private OperationType type;
    @ManyToOne
    private BankAccount account;
    private String description;
}

