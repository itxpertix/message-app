package org.ms.reactive.jmsapp.infrastructure.persistence.impl;

import org.ms.reactive.jmsapp.domain.model.Afiliado;
import org.ms.reactive.jmsapp.infrastructure.persistence.AfiliadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultAfiliadoServiceImpl implements AfiliadoService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAfiliadoServiceImpl.class);

	@Override
	public void saveAfiliado(Afiliado afiliado) {
		LOGGER.info("Registrando afiliando en la base de datos");
		
	}

}
