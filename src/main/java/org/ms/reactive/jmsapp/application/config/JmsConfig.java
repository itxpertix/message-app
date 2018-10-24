package org.ms.reactive.jmsapp.application.config;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.log4j.spi.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Carlos Ortiz Urshela
 */
@Configuration
@EnableJms
public class JmsConfig {

    @Value("${jms.cache.size}")
    private int jmsCacheSize;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConfig.class);
    
    @Bean
    @ConditionalOnBean(ActiveMQConnectionFactory.class)
    public InitializingBean connectionFactory(ActiveMQConnectionFactory connectionFactory) {
        return configureRedeliveryPolicy(connectionFactory);
    }

    /*@Bean
    @ConditionalOnBean(PooledConnectionFactory.class)
    public InitializingBean pooledConnectionFactory(PooledConnectionFactory connectionFactory) {
        if (connectionFactory.getConnectionFactory() instanceof ActiveMQConnectionFactory) {
            return configureRedeliveryPolicy((ActiveMQConnectionFactory) connectionFactory.getConnectionFactory());
        } else return () -> {
            // do something else
        };
    }*/

    private InitializingBean configureRedeliveryPolicy(ActiveMQConnectionFactory connectionFactory) {
        return () ->
        {
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
            redeliveryPolicy.setMaximumRedeliveries(5);
            // configure redelivery policy
            connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        };
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ConnectionFactory connectionFactory) {
        
        String jmsClientID = "jms-listener-" + System.currentTimeMillis();
        
        
        CachingConnectionFactory ccf = new CachingConnectionFactory(connectionFactory);
        ccf.setClientId(jmsClientID);
        ccf.setSessionCacheSize(jmsCacheSize);
               

        DefaultJmsListenerContainerFactory dmlc = new DefaultJmsListenerContainerFactory();
        dmlc.setConnectionFactory(ccf);
        dmlc.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        dmlc.setTransactionManager(transactionManager(connectionFactory));
        dmlc.setErrorHandler(new org.springframework.util.ErrorHandler() {
			
			@Override
			public void handleError(Throwable arg0) {
				LOGGER.error("Unexpected error");
				
			}
		});
        
        
        LOGGER.info("ACTIVMQ STARTED");
        
        return dmlc;
    }
    

    @Bean
    public PlatformTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory);
        return transactionManager;
    }

    
}