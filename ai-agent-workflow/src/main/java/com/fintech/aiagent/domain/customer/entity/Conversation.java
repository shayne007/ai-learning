package com.fintech.aiagent.domain.customer.entity;

import com.fintech.aiagent.domain.customer.valueobject.ConversationId;
import com.fintech.aiagent.domain.customer.valueobject.CustomerId;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Embedded;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Conversation {
    @EmbeddedId
    private ConversationId conversationId;
    
    @Embedded
    private CustomerId customerId;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityTime;
    private int turnCount;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<CustomerQuery> queries = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Response> responses = new ArrayList<>();

    public Conversation() {
    }

    public Conversation(ConversationId conversationId, CustomerId customerId, LocalDateTime createdAt) {
        this.conversationId = conversationId;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.lastActivityTime = createdAt;
        this.turnCount = 0;
    }

    public void addQuery(CustomerQuery query) {
        queries.add(query);
        lastActivityTime = LocalDateTime.now();
    }

    public void addResponse(Response response) {
        responses.add(response);
        turnCount++;
        lastActivityTime = LocalDateTime.now();
    }

    public List<Response> getResponses() {
        return new ArrayList<>(responses);
    }

    public List<CustomerQuery> getQueries() {
        return new ArrayList<>(queries);
    }
} 