package org.ms.reactive.jmsapp.infrastructure.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.ms.reactive.jmsapp.application.listener.DBSyncListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsUtils;
import org.springframework.stereotype.Service;

@Service
public class JMSApplicationInputEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(JMSApplicationInputEndpoint.class);

	@Autowired
	private DBSyncListener dbSyncListener;

	@Value("${work.queue.deadletter}")
	private String dlQueueName;

	@Autowired
	private JmsPublisher jmsPublisher;

	@JmsListener(destination = "${work.queue.input}", containerFactory = "jmsQueueListenerContainerFactory")
	public void onMessage(Message message) throws Exception {
		int deliveryCount = getDeliveryNumber(message);
		String data = null;
		try {

			TextMessage msg = (TextMessage) message;
			data = msg.getText();

			LOGGER.info("Received synchronization | data: " + data + " | Redelivery: " + message.getJMSRedelivered()
					+ " redelivery-count: " + getDeliveryNumber(message));
			
			dbSyncListener.processMessage(data);
			
			checkPostprocessException(message);

		} catch (Exception ex) {
			
			if (deliveryCount == 3) {
				LOGGER.info("se ha alcanzado el maximo numero de reprocesos para el mensaje, se envia a DeadLetter Queue");
				// Envia mensaje a DeadLetterQueue
				// se hace acknowledge del mensaje
				jmsPublisher.publish(data, dlQueueName);

			} else {
				if (ex instanceof JMSException) {
					throw JmsUtils.convertJmsAccessException((JMSException) ex);
				} else {
					throw ex;
				}
			}
		}

	}

	private int getDeliveryNumber(Message message) throws JMSException {
		return message.getIntProperty("JMSXDeliveryCount");
	}

	private void checkPostprocessException(Message message) throws JMSException {
		if (getDeliveryNumber(message) < 4) {
			throw new RuntimeException("error after processing message");
		}
	}
}
