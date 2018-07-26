package br.unb.cic.preditorhistorico;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import br.unb.cic.controladorsimulacao.Parametros;
import br.unb.cic.controladorsimulacao.Resultados;
import br.unb.cic.extrator.dominio.arco.Arco;
import br.unb.cic.extrator.dominio.arco.ArcoRepository;
import br.unb.cic.extrator.dominio.localizacao.LocalizacaoRepository;
import br.unb.cic.extrator.dominio.no.No;
import br.unb.cic.extrator.dominio.no.NoRepository;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagemRepository;

@Component
public class ConstrutorDeModelos {

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private NoRepository noRepository;

	@Autowired
	private TempoViagemRepository tempoViagemRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;

	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private Resultados resultados;

	private List<Arco> arcos;
	private List<No> nos;

	private static final Logger logger = Logger.getLogger(ConstrutorDeModelos.class.getName());

	@PostConstruct
	public void init() {
		this.arcos = arcoRepository.findAllByOrderByIdAsc();
		this.nos = noRepository.findAllByOrderByIdAsc();
	}

	public void construirModelo(Parametros parametros) {
		for (Arco arco : arcos) {
			ConstruirModeloTask construirModeloTask = (ConstruirModeloTask) appContext
					.getBean(ConstruirModeloTask.class);
			construirModeloTask.setElementoGrafo(arco);
			construirModeloTask.setParametros(parametros);
			threadPoolTaskExecutor.execute(construirModeloTask);
		}

		for (No no : nos) {
			ConstruirModeloTask construirModeloTask = (ConstruirModeloTask) appContext
					.getBean(ConstruirModeloTask.class);
			construirModeloTask.setElementoGrafo(no);
			construirModeloTask.setParametros(parametros);
			threadPoolTaskExecutor.execute(construirModeloTask);
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
				// tempoViagemRepository.updateProcessado();
				
				if(parametros.getTemposViagemAnteriores() == 2){
					logger.info("Limpeza das tabelas");
					tempoViagemRepository.deleteAllInBatch();
					localizacaoRepository.deleteAllInBatch();
					logger.info("Fim da limpeza das tabelas");
				}

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