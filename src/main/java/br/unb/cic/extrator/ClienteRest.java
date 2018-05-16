package br.unb.cic.extrator;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import br.unb.cic.extrator.onibus.LocalizacaoDao;
import br.unb.cic.simuladortrafego.onibus.DtoFrota;

@Component
public class ClienteRest implements Runnable {

	private LocalizacaoDao localizacaoDao;
	private RestTemplate restTemplate;

	public ClienteRest() {
		super();
		restTemplate = new RestTemplate();
		localizacaoDao = new LocalizacaoDao();
	}

	@Override
	public void run() {
		DtoFrota dtoFrota = restTemplate.getForObject("http://localhost:8080/localizacao", DtoFrota.class);
		localizacaoDao.insereLocalizacao(dtoFrota);
	}

}
