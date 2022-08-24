package cn.itcast.mq.listener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String msg) {
        System.out.println("消费者接收到simple.queue的消息：【" + msg + "】");
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dl.queue",durable = "true"),
            exchange = @Exchange(name = "dl.direct"),
            key = "dl"
    ))
    public void listenDlQueue(String msg){
        log.info("接收到 dl.queue的延迟消息：{}", msg);
    }

    //基于注解实现延迟队列DelayExchange类型交换机
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "delay.queue",durable = "true"),
            exchange = @Exchange(name = "delay.direct",type = ExchangeTypes.DIRECT,delayed = "true"),
            key = "delay"))
    public void listenDelayQueue(String msg){
        log.info("接收到 delay.queue的延迟消息：{}", msg);
    }
}
