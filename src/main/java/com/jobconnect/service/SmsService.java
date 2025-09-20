


package com.jobconnect.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

   
    private final String accountSid = "";
    private final String authToken  = "";
    private final String fromNumber = "";

    public String fromPhone;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String to, String messageBody) {
        String sender = (fromPhone != null) ? fromPhone : fromNumber;
        sendMessage(new PhoneNumber(to), new PhoneNumber(sender), messageBody);
    }

    public Message sendMessage(PhoneNumber to, PhoneNumber from, String body) {
        return Message.creator(to, from, body).create();
    }
}





