package br.unb.cic.extrator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.unb.cic.parametros.Parametros;

@Component
public class ExtratorDeDadosDeTrafego {

	private ClienteRest clienteRest;

	private static final Logger log = LoggerFactory.getLogger(ExtratorDeDadosDeTrafego.class);

	public ExtratorDeDadosDeTrafego() {
		iniciarClienteRest();
	}

	private void iniciarClienteRest() {
		log.info("Iniciando Cliente REST.");
		clienteRest = new ClienteRest();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(clienteRest, 0, Parametros.PERIODO_DE_ATUALIZACAO_CLIENTE_REST_EM_SEGUNDOS,
				TimeUnit.SECONDS);
	}

}
