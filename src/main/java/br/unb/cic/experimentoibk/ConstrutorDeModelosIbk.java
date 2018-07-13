package br.unb.cic.experimentoibk;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.arco.Arco;
import br.unb.cic.extrator.dominio.arco.ArcoRepository;

@Component
public class ConstrutorDeModelosIbk {

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private ResultadosExpIbk resultados;

	private List<Arco> arcos;

	private static final Logger logger = Logger.getLogger(ConstrutorDeModelosIbk.class.getName());

	@PostConstruct
	public void init() {
		this.arcos = arcoRepository.findAllByOrderByIdAsc();
	}

	public void construirModelo(String cenario) {

		for (Arco arco : arcos) {
			TarefaConstrutorModeloIbk tarefaConstrutorModeloIbk = (TarefaConstrutorModeloIbk) appContext
					.getBean(TarefaConstrutorModeloIbk.class);
			tarefaConstrutorModeloIbk.setElementoGrafo(arco);
			tarefaConstrutorModeloIbk.setCenario(cenario);
			threadPoolTaskExecutor.execute(tarefaConstrutorModeloIbk);
		}

		for (;;) {
			int count = threadPoolTaskExecutor.getActiveCount();
			logger.debug("Active Threads : " + count);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				logger.error("Thread principal interrompida: ", e);
			}
			if (count == 0) {
				resultados.flush();
				break;
			}
		}
	}

}

// classificador.setOptions(Utils.splitOptions(
// "-C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -T
// 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K
// \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\""));