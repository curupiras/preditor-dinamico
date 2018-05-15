package br.unb.cic.extrator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.unb.cic.simuladortrafego.onibus.DtoFrota;


@Component
public class Extrator {

	private static final Logger log = LoggerFactory.getLogger(Extrator.class);

	public Extrator() {
		RestTemplate restTemplate = new RestTemplate();
		DtoFrota dtoFrota = restTemplate.getForObject("http://localhost:8080/localizacao", DtoFrota.class);
		log.info(dtoFrota.toString());
	}

}
