package com.fintech.aiagent.domain.customer.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ConversationId {
    @Column(name = "conversation_id")
    String id;
} 