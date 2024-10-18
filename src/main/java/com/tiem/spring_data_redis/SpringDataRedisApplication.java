package com.tiem.spring_data_redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@SpringBootApplication
public class SpringDataRedisApplication {
	
	private static final Logger log = LoggerFactory.getLogger(SpringDataRedisApplication.class);

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = SpringApplication.run(SpringDataRedisApplication.class, args);

		StringRedisTemplate template = ctx.getBean(StringRedisTemplate.class);
		Receiver receiver = ctx.getBean(Receiver.class);
		
		while (receiver.getCount() < 100) {
			log.info("Sending message...");
			template.convertAndSend("chat", receiver.getCount()+" Hello from Redis");
			Thread.sleep(500L);
		}
		
		System.exit(0);
	}
	
	@Bean
	RedisMessageListenerContainer container (RedisConnectionFactory connectionFactory, MessageListenerAdapter msgAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(msgAdapter, new PatternTopic("chat"));
		return container;
	}
	
	@Bean
	MessageListenerAdapter msgAdapter (Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
	@Bean
	Receiver receiver () {
		return new Receiver();
	}
	
	@Bean
	StringRedisTemplate template (RedisConnectionFactory connectionFactory) {
		return new StringRedisTemplate(connectionFactory);
	}

}
