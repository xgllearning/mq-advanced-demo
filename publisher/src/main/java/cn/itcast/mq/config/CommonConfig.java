package cn.itcast.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Slf4j
@Configuration
public class CommonConfig implements ApplicationContextAware {

    /**
     * 每个RabbitTemplate只能配置一个ReturnCallback，因此需要在项目加载时配置：
     * ApplicationContextAware是bean容器的一个通知接口，当spring工厂的bean准备好后会去通知你（ApplicationContextAware），
     * 实现这个接口就需要实现setApplicationContext方法，通知你的时候（ApplicationContextAware）会把Spring的容器（ApplicationContext）传递给你，
     * 拿到spring工厂后就可以取到想要的bean了（RabbitTemplate.class），再给RabbitTemplate.class设置ReturnCallback，
     * 因为项目启动的时候就会去执行，此时就会定义一个全局的ReturnCallback且只有一个
     * 修改publisher服务，添加一个：ReturnCallback，是指消息到达交换机了，路由的时候失败了
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取RabbitTemplate
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        // 设置ReturnCallback
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            // 投递失败，记录日志
            log.info("消息到达交换机，发到队列发送失败，应答码{}，原因{}，交换机{}，路由键{},消息{}",
                    replyCode, replyText, exchange, routingKey, message.toString());
            // 如果有业务需要，可以重发消息
        });
    }

}
