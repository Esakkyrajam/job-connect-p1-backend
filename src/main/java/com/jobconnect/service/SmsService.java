//package com.jobconnect.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsService {
//
//    @Value("${twilio.account-sid}") private String accountSid;
//    @Value("${twilio.auth-token}") private String authToken;
//    @Value("${twilio.phone-number}") private String fromPhone;
//
//    @PostConstruct
//    public void init() { Twilio.init(accountSid, authToken); }
//
//    public void sendSms(String to, String message) {
//        Message.creator(
//                new com.twilio.type.PhoneNumber(to),
//                new com.twilio.type.PhoneNumber(fromPhone),
//                message
//        ).create();
//    }
//}












//
//
//package com.jobconnect.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsService {
//    //
////    // Load from application.properties or environment variables
////  cls
//
//    public void sendSms(String to, String messageBody) {
//        // Make sure Twilio credentials are initialized
//        Twilio.init("AC994b6e78424f0d1ae526ad56dfc8c4b7", "4c03dcd832a51f22fcf822f2f1400623");
//        //  Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//
//        // Ensure phone numbers are in E.164 format
//        Message.creator(
//                new PhoneNumber(to),        // Recipient number, e.g., +919876543210
//                new PhoneNumber("+14722262317"), // Your Twilio number, e.g., +1234567890
//                messageBody
//        ).create();
//    }
//}
//
//
//
//

//package com.jobconnect.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SmsService {
//
//    public String fromPhone;
//    @Value("${twilio.account-sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth-token}")
//    private String authToken;
//
//    @Value("${twilio.phone-number}")
//    private String fromNumber;
//
//    // Optional: Add setters for testing
//    public void setAccountSid(String accountSid) { this.accountSid = accountSid; }
//    public void setAuthToken(String authToken) { this.authToken = authToken; }
//    public void setFromNumber(String fromNumber) { this.fromNumber = fromNumber; }
//
//    @PostConstruct
//    public void init() {
//        Twilio.init("AC994b6e78424f0d1ae526ad56dfc8c4b7", "559f2abcb22ba84f63122802bd0a6d04");
//    }
//
//    public void sendSms(String to, String messageBody) {
//        Message.creator(
//                new PhoneNumber(to),
//                new PhoneNumber(fromNumber),
//                messageBody
//        ).create();
//    }
//
//
//    public void sendMessage(PhoneNumber any, PhoneNumber any1, String s) {
//    }
//}



package com.jobconnect.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    // 👇 Hardcoded credentials (replace with yours)
    private final String accountSid = "AC994b6e78424f0d1ae526ad56dfc8c4b7";
    private final String authToken  = "4c03dcd832a51f22fcf822f2f1400623";
    private final String fromNumber = "+14722262317";

    // 👈 Still keep this for overriding in tests
    public String fromPhone;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(String to, String messageBody) {
        // Use fromPhone if manually set (test), else hardcoded one
        String sender = (fromPhone != null) ? fromPhone : fromNumber;
        sendMessage(new PhoneNumber(to), new PhoneNumber(sender), messageBody);
    }

    // Extracted method so we can spy/mock in tests
    public Message sendMessage(PhoneNumber to, PhoneNumber from, String body) {
        return Message.creator(to, from, body).create();
    }
}





