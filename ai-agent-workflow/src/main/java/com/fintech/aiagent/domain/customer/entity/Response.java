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
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private ResponseType type;

    public Response(String content, ResponseType type) {
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    protected Response() {
        // For JPA
    }

    public enum ResponseType {
        TEXT,
        ACTION,
        ERROR
    }
} 