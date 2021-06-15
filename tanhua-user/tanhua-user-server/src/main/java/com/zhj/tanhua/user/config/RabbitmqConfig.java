package com.zhj.tanhua.user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huanjie.zhuang
 * @date 2021/6/6
 */
@Configuration
public class RabbitmqConfig {

    private static final String QUEUE = "tanhua_queue_sso";
    public static final String EXCHANGE = "tanhua_exchange_direct";
    public static final String ROUTING_KEY = "sso";

    @Bean
    public Queue getQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public DirectExchange getDirectExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingDirect() {
        return BindingBuilder.bind(getQueue()).to(getDirectExchange()).with(ROUTING_KEY);
    }


    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory){

        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        //设置开启Mandatory,才能触发回调函数,无论消息推送结果怎么样都强制调用回调函数
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("ConfirmCallback:--------" + "相关数据：" + correlationData);
            System.out.println("ConfirmCallback:--------" + "确认情况：" + ack);
            System.out.println("ConfirmCallback:--------" + "原因：" + cause);
        });

        return rabbitTemplate;
    }
}
