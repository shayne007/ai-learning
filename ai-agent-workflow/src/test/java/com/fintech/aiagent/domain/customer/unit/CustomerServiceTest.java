package com.fintech.aiagent.domain.customer.unit;

import com.fintech.aiagent.domain.customer.entity.Conversation;
import com.fintech.aiagent.domain.customer.entity.CustomerQuery;
import com.fintech.aiagent.domain.customer.entity.Response;
import com.fintech.aiagent.domain.customer.repository.ConversationRepository;
import com.fintech.aiagent.domain.customer.service.CustomerServiceDomainService;
import com.fintech.aiagent.domain.customer.valueobject.ConversationId;
import com.fintech.aiagent.domain.customer.valueobject.CustomerId;
import com.fintech.aiagent.infrastructure.messaging.MessageQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Customer Service Domain Tests")
class CustomerServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageQueueService messageQueueService;

    private CustomerServiceDomainService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerService = new CustomerServiceDomainService(conversationRepository, messageQueueService);
    }

    @Test
    @DisplayName("Should create new conversation successfully")
    void shouldCreateNewConversation() {
        // Given
        CustomerId customerId = new CustomerId("customer123");
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Conversation conversation = customerService.createConversation(customerId);

        // Then
        assertNotNull(conversation);
        assertEquals(customerId, conversation.getCustomerId());
        assertEquals(0, conversation.getTurnCount());
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should process query and return response")
    void shouldProcessQueryAndReturnResponse() {
        // Given
        ConversationId conversationId = new ConversationId("conv123");
        CustomerId customerId = new CustomerId("customer123");
        Conversation conversation = new Conversation(conversationId, customerId, LocalDateTime.now());
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Response response = customerService.processQuery(conversationId, "Hello");

        // Then
        assertNotNull(response);
        assertEquals(Response.ResponseType.TEXT, response.getType());
        verify(conversationRepository).save(any(Conversation.class));
        verify(messageQueueService).sendMessage(any());
    }

    @Test
    @DisplayName("Should track conversation turn count")
    void shouldTrackConversationTurnCount() {
        // Given
        ConversationId conversationId = new ConversationId("conv123");
        CustomerId customerId = new CustomerId("customer123");
        Conversation conversation = new Conversation(conversationId, customerId, LocalDateTime.now());
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        customerService.processQuery(conversationId, "Hello");

        // Then
        assertEquals(1, conversation.getTurnCount());
        verify(messageQueueService).sendMessage(any());
    }

    @Test
    @DisplayName("Should throw exception when conversation not found")
    void shouldThrowExceptionWhenConversationNotFound() {
        // Given
        ConversationId conversationId = new ConversationId("nonexistent");
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.processQuery(conversationId, "Hello");
        });
    }

    @Test
    @DisplayName("Should maintain conversation context across multiple turns")
    void shouldMaintainConversationContext() {
        // Given
        ConversationId conversationId = new ConversationId("conv123");
        CustomerId customerId = new CustomerId("customer123");
        Conversation conversation = new Conversation(conversationId, customerId, LocalDateTime.now());
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Response firstResponse = customerService.processQuery(conversationId, "What's my credit limit?");
        Response secondResponse = customerService.processQuery(conversationId, "Can I increase it?");

        // Then
        assertNotNull(firstResponse);
        assertNotNull(secondResponse);
        assertEquals(2, conversation.getTurnCount());
        verify(conversationRepository, times(2)).save(any(Conversation.class));
        verify(messageQueueService, times(2)).sendMessage(any());
    }
} 