package br.unb.cic.experimentoibk;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ControladorExperimento {

	@Autowired
	private ConstrutorDeModelosIbk construtorDeModelosIbk;

	private static final Logger logger = Logger.getLogger(ControladorExperimento.class.getName());

	@PostConstruct
	public void init() {
		controlarSimulacao();
	}

	public void controlarSimulacao() {

		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 1; j++) {
				String cenario = "000" + i + j;
				logger.info("Iniciando cenario: " + cenario);
				construtorDeModelosIbk.construirModelo(cenario);
				logger.info("Fim cenario: " + cenario);
			}
		}
	}

}
