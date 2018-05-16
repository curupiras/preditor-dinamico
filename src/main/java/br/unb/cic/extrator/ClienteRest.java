package br.unb.cic.extrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.unb.cic.extrator.dominio.Frota;
import br.unb.cic.extrator.dominio.LocalizacaoRepository;

@Component
public class ClienteRest {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	LocalizacaoRepository repository;

	@Scheduled(initialDelay = 0, fixedRate = 10000)
	public void scheduledTask() {
		Frota dtoFrota = restTemplate.getForObject("http://localhost:8080/localizacao", Frota.class);
		repository.save(dtoFrota.getFrota());
	}

}
