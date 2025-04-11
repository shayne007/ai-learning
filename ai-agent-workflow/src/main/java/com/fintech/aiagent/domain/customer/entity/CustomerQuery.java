package com.fintech.aiagent.domain.customer.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CustomerQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime timestamp;

    public CustomerQuery(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    protected CustomerQuery() {
        // For JPA
    }
} 