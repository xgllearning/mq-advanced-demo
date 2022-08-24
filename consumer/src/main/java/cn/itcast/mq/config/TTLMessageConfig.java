package cn.itcast.mq.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TTLMessageConfig {
    @Bean
    public DirectExchange ttlDirectExchange(){
        return new DirectExchange("ttl.direct");
    }
    //队列与死信交换机绑定
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder.durable("ttl.queue") // 指定队列名称，并持久化
                .ttl(10000) // 设置队列的超时时间，10秒---10s钟后接收到消息
                .deadLetterExchange("dl.direct") // 指定死信交换机
                .deadLetterRoutingKey("dl")
                .build();
    }
    //队列与正常交换机绑定
    @Bean
    public Binding ttlBinding(){
        return BindingBuilder.bind(ttlQueue()).to(ttlDirectExchange()).with("ttl");
    }

}
