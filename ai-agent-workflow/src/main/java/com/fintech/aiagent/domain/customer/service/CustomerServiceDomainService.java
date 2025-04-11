package com.fintech.aiagent.domain.customer.service;

import com.fintech.aiagent.domain.customer.entity.Conversation;
import com.fintech.aiagent.domain.customer.entity.CustomerQuery;
import com.fintech.aiagent.domain.customer.entity.Response;
import com.fintech.aiagent.domain.customer.repository.ConversationRepository;
import com.fintech.aiagent.domain.customer.valueobject.ConversationId;
import com.fintech.aiagent.domain.customer.valueobject.CustomerId;
import com.fintech.aiagent.infrastructure.messaging.MessageQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceDomainService {
    private final ConversationRepository conversationRepository;
    private final MessageQueueService messageQueueService;

    @Transactional
    public Conversation createConversation(CustomerId customerId) {
        ConversationId conversationId = new ConversationId(UUID.randomUUID().toString());
        Conversation conversation = new Conversation(conversationId, customerId, LocalDateTime.now());
        return conversationRepository.save(conversation);
    }

    @Transactional
    public Response processQuery(ConversationId conversationId, String queryContent) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        CustomerQuery query = new CustomerQuery(queryContent);
        conversation.addQuery(query);

        // Simulate AI processing
        Response response = new Response(
                "This is a simulated response to: " + queryContent,
                Response.ResponseType.TEXT
        );
        conversation.addResponse(response);

        // Send message to queue for async processing
        messageQueueService.sendMessage("Processing query: " + queryContent);

        conversationRepository.save(conversation);
        return response;
    }

    @Transactional(readOnly = true)
    public int getConversationTurnCount(ConversationId conversationId) {
        return conversationRepository.findById(conversationId)
                .map(Conversation::getTurnCount)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));
    }
} 