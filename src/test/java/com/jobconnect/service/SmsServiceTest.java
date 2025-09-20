package com.jobconnect.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SmsServiceTest {

    SmsService smsService;

    @BeforeEach
    void setUp() {
        smsService = spy(new SmsService());
        // Override sender for testing
        smsService.fromPhone = "+1234567890";
    }

    @Test
    void testSendSms() {
        // Mock Message
        Message mockMessage = mock(Message.class);

        // Stub the helper method
        doReturn(mockMessage)
                .when(smsService)
                .sendMessage(any(PhoneNumber.class), any(PhoneNumber.class), anyString());

        // Call the actual method
        smsService.sendSms("+0987654321", "Hello World!");

        // Verify helper was called with correct args
        verify(smsService, times(1))
                .sendMessage(
                        new PhoneNumber("+0987654321"),
                        new PhoneNumber("+1234567890"),
                        "Hello World!"
                );
    }
}
