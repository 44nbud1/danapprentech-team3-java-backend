package project.akhir.danapprentechteam3.login.rabbitmq.rabbitconsumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.payload.request.LoginRequest;
import project.akhir.danapprentechteam3.login.payload.request.SignupRequest;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitMqConsumer
{
        static final String Signupqueue = "signupKey";
        @RabbitListener(queues = Signupqueue)
        public SignupRequest recievedMessage(SignupRequest signupRequest)
        {
            return signupRequest;
        }

//        static final String Loginqueue = "loginKey";
//        @RabbitListener(queues = Loginqueue)
//        public LoginRequest loginRequest(LoginRequest loginRequest)
//        {
//            System.out.println("");
//            return loginRequest;
//        }
//
//        static final String Logoutqueue = "logoutKey";
//        @RabbitListener(queues = Logoutqueue)
//        public LoginRequest logoutRequest(LoginRequest loginRequest)
//        {
//            System.out.println("");
//            return loginRequest;
//        }
//
//        static final String updatequeue = "updateKey";
//        @RabbitListener(queues = updatequeue)
//        public LoginRequest updateRequest(LoginRequest loginRequest)
//        {
//            System.out.println("");
//            return loginRequest;
//        }
}
