package com.demo.huyaxiaochengxu.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//kafka消费者监听器
@Component
public class MsgConsumer{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    @KafkaListener(topics = {"device"})
    public void listen(ConsumerRecord<?, ?> record) {
        logger.info("kafka的key: " + record.key());
        logger.info("kafka的value: " + record.value().toString());
        System.out.println ("kafka的key: " + record.key());
        System.out.println("kafka的value: " + record.value().toString());
    }
}
