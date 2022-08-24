package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        //1.准备消息
        String routingKey = "simple.test";
        String message = "hello, spring amqp!";
        //普通发送消息-rabbitTemplate.convertAndSend("amq.topic", routingKey, message);
        //2.消息确认机制的发送消息，准备correlationData
        // 2.1.准备全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 2.2.添加callback，使用一个成功回调和一个失败回调，都是匿名内部类
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        log.debug("消息发送成功，到达交换机, 消息ID:{}", correlationData.getId());
                    }else{
                        // 3.2.nack，消息失败
                        log.error("消息发送到交换机失败, 消息ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        //3.发送消息
        rabbitTemplate.convertAndSend("amq.topic", routingKey, message,correlationData);//交换机的名称、routingKey的名称、消息、与配置类 publisher-confirm-type: correlated  相对应

        // 休眠一会儿，等待ack回执
        Thread.sleep(2000);
    }

    //发送延迟消息到正常交换机，再到死信交换机
    @Test
    public void testTTLMsg() {
        // 创建消息
        Message message = MessageBuilder
                .withBody("hello, ttl message".getBytes(StandardCharsets.UTF_8))
                .setExpiration("5000")
                .build();
        // 消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 发送消息
        rabbitTemplate.convertAndSend("ttl.direct", "ttl", message, correlationData);
        log.debug("发送消息成功");
    }
}
