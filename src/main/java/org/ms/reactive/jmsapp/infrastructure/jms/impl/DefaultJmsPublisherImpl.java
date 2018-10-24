package org.ms.reactive.jmsapp.infrastructure.jms.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.ms.reactive.jmsapp.infrastructure.jms.JmsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class DefaultJmsPublisherImpl implements JmsPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJmsPublisherImpl.class);

	@Autowired
	private JmsTemplate jmsTemplate;

	@Value("${work.queue.input}")
	private String queueOut;

	@Override
	public void publish(String msg, String queue) {
		LOGGER.info("Start sending message...!");
		MessageCreator messageCreator = new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				String textMessage = "{\"record-id\": \"" + System.currentTimeMillis() + "\"}";
				return session.createTextMessage(textMessage);
			}
		};
		LOGGER.info("Sending message.");
		jmsTemplate.send(queue, messageCreator);
	}

}
