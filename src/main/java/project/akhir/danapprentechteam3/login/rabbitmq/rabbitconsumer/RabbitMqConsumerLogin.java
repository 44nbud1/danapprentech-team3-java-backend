package project.akhir.danapprentechteam3.login.rabbitmq.rabbitconsumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.payload.request.LoginRequest;

@Service
public class RabbitMqConsumerLogin
{

        static final String Loginqueue = "signupKey";
        @RabbitListener(queues = Loginqueue)
        public LoginRequest recievedMessage(LoginRequest loginRequest)
        {
            return loginRequest;
        }

}
