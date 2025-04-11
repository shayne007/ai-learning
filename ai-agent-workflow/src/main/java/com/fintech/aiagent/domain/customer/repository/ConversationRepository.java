package com.fintech.aiagent.domain.customer.repository;

import com.fintech.aiagent.domain.customer.entity.Conversation;
import com.fintech.aiagent.domain.customer.valueobject.ConversationId;
import com.fintech.aiagent.domain.customer.valueobject.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, ConversationId> {
    List<Conversation> findByCustomerId(CustomerId customerId);
    List<Conversation> findByLastActivityTimeBefore(LocalDateTime time);
} 