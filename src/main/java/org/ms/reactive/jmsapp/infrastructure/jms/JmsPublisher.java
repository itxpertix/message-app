package org.ms.reactive.jmsapp.infrastructure.jms;


public interface JmsPublisher {
	 
	 void publish(String msg,String queue);
	  
}
