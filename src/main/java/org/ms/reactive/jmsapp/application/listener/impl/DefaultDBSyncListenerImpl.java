package org.ms.reactive.jmsapp.application.listener.impl;

import org.ms.reactive.jmsapp.application.listener.DBSyncListener;
import org.ms.reactive.jmsapp.domain.model.Afiliado;
import org.ms.reactive.jmsapp.infrastructure.persistence.AfiliadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultDBSyncListenerImpl implements DBSyncListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDBSyncListenerImpl.class);
	
	@Autowired
	private AfiliadoService afiliadoService;

	@Override
	/*
	 * La cadena message esta en formato json y contiene el registro que se está
	 * sincronizando
	 */
	public void processMessage(String message) {
		LOGGER.info("synchronization message received");
		Afiliado afiliado = unmarshall(message);
		afiliadoService.saveAfiliado(afiliado);
	}

	private Afiliado unmarshall(String message) {
		// TODO Auto-generated method stub
		return new Afiliado();
	}
	
}
