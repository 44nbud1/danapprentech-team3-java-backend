package project.akhir.danapprentechteam3.login.rabbitmq.rabbitproducer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.akhir.danapprentechteam3.login.payload.request.LoginRequest;
import project.akhir.danapprentechteam3.login.payload.request.SignupRequest;


@Service
public class RabbitMqProducer {

    //echange
    private static final String exchangeDirect = "direct-Exchange";

    //Queue
    private static final String signupKey = "signupKey";
    private static final String loginKey = "loginKey";
    private static final String logoutKey = "logoutKey";
    private static final String updateKey = "updateKey";

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void signupSendRabbit(SignupRequest signupRequest)
    {
        amqpTemplate.convertAndSend(exchangeDirect,signupKey,signupRequest);
    }

    public void loginSendRabbit(LoginRequest loginRequest)
    {
        amqpTemplate.convertAndSend(exchangeDirect,loginKey,loginRequest);
    }

//    public void logouSendRabbit(LoginRequest loginRequest)
//    {
//        amqpTemplate.convertAndSend(exchangeDirect,logoutKey,loginRequest);
//    }
//
//    public void updateSendRabbit(LoginRequest loginRequest)
//    {
//        amqpTemplate.convertAndSend(exchangeDirect,updateKey,loginRequest);
//    }
}
