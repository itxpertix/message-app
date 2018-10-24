package org.ms.reactive.jmsapp;

import org.ms.reactive.jmsapp.application.config.JmsConfig;
import org.ms.reactive.jmsapp.infrastructure.jms.JmsPublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = { JmsConfig.class })
public class DBSyncApp {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(DBSyncApp.class, args);

		JmsPublisher publisher = context.getBean(JmsPublisher.class);
		Thread.sleep(10000);
		for (int i = 0; i < 100; i++) {
			publisher.publish("hello","dbsync-in");
			Thread.sleep(10000);
		}
	}
}
