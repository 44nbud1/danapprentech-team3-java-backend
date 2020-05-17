package project.akhir.danapprentechteam3.rabbitmq.rabbitconfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig
{
    //  Queue
    private static final String signupQueue = "signupQueue";
    private static final String loginQueue = "loginQueue";
    private static final String logoutQueue = "logoutQueue";
    private static final String updateQueue = "updateQueue";

    //Echange
    private static final String exchangeDirect = "direct-Exchange";

    // routing key
    private static final String routingKeyDirect1 = "signupKey";
    private static final String routingKeyDirect2 = "loginKey";
    private static final String routingKeyDirect3 = "logoutKey";
    private static final String routingKeyDirect4 = "updateKey";


    //sign up
    @Bean
    Queue signupQueue()
    {
        return new Queue(signupQueue,true,false,false);
    }

    //login
    @Bean
    Queue loginQueue()
    {
        return new Queue(loginQueue,true,false,false);
    }

    //logout
    @Bean
    Queue logoutQueue()
    {
        return new Queue(logoutQueue,true,false,false);
    }

    //logout
    @Bean
    Queue updateQueue()
    {
        return new Queue(updateQueue,true,false,false);
    }

    @Bean
    DirectExchange directExchange()
    {
        return new DirectExchange(exchangeDirect);
    }

    @Bean
    Binding signupBinding()
    {
        return BindingBuilder.bind(signupQueue()).to(directExchange()).with(routingKeyDirect1);
    }

    @Bean
    Binding logoutBinding()
    {
        return BindingBuilder.bind(logoutQueue()).to(directExchange()).with(routingKeyDirect3);
    }

    @Bean
    Binding loginBinding()
    {
        return BindingBuilder.bind(loginQueue()).to(directExchange()).with(routingKeyDirect2);
    }

    @Bean
    Binding updateBinding()
    {
        return BindingBuilder.bind(updateQueue()).to(directExchange()).with(routingKeyDirect4);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
