package br.unb.cic.preditorhistorico;

import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.arco.Arco;
import br.unb.cic.extrator.dominio.arco.ArcoRepository;
import br.unb.cic.extrator.dominio.no.No;
import br.unb.cic.extrator.dominio.no.NoRepository;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

@Component
public class ConstrutorDeModelos {

	@Value("${preditor.numeroDeFolds}")
	private int numFolds;

	@Value("${preditor.avaliacaoCruzada}")
	private boolean avaliacaoCruzada;

	@Value("${preditor.treinoETeste}")
	private boolean treinoETeste;

	@Value("${preditor.porcentagemDeTeste}")
	private double porcentagemDeTeste;

	@Autowired
	GeradorDeInstances geradorDeInstances;

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private NoRepository noRepository;

	private static final int UM_DIA = 86400000;

	private List<Arco> arcos;
	private List<No> nos;

	private static final Logger logger = Logger.getLogger(ConstrutorDeModelos.class.getName());

	@PostConstruct
	public void init() {
		this.arcos = arcoRepository.findAllByOrderByIdAsc();
		this.nos = noRepository.findAllByOrderByIdAsc();
	}

	@Scheduled(initialDelay = 10000, fixedRate = UM_DIA)
	public void scheduledTask() {
		for (Arco arco : arcos) {
			logger.info("Iniciando construção do Modelo para o arco " + arco.getNome());
			construirModelo(arco);
			logger.info("Fim da construção do Modelo para o arco " + arco.getNome());
		}

		for (No no : nos) {
			logger.info("Iniciando construção do Modelo para o no " + no.getNome());
			construirModelo(no);
			logger.info("Fim da construção do Modelo para o no " + no.getNome());
		}
	}

	private void construirModelo(ElementoGrafo elementoGrafo) {
		try {

			if (avaliacaoCruzada) {

				// Carregar dados de viagem do banco de dados
				Instances dados = geradorDeInstances.getInstancesFromDB(elementoGrafo);
				dados.setClassIndex(0);

				// Criar nova instancia do classificador
				SMOreg classificador = new SMOreg();
				classificador.setOptions(Utils.splitOptions(
						"-C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\""));
				classificador.buildClassifier(dados);

				// Fazer avalização cruzada
				Evaluation avaliador = new Evaluation(dados);
				avaliador.crossValidateModel(classificador, dados, numFolds, new Random(1));
				System.out.println(avaliador.toSummaryString("\nResults\n======\n", false));
			}

			if (treinoETeste) {

				// Carregar dados de viagem do banco de dados
				Instances dados = geradorDeInstances.getInstancesFromDB(elementoGrafo);
				// dados.randomize(new Random(42));

				RemovePercentage rp = new RemovePercentage();
				rp.setInputFormat(dados);
				rp.setPercentage(porcentagemDeTeste);
				Instances treino = Filter.useFilter(dados, rp);

				rp = new RemovePercentage();
				rp.setInputFormat(dados);
				rp.setPercentage(porcentagemDeTeste);
				rp.setInvertSelection(true);
				Instances teste = Filter.useFilter(dados, rp);

				treino.setClassIndex(0);
				teste.setClassIndex(0);

				// Treinar classificador
				SMOreg classificador = new SMOreg();
				classificador.setOptions(Utils.splitOptions(
						"-C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\""));
				classificador.buildClassifier(treino);

				// Avaliar o classificador
				Evaluation avaliador = new Evaluation(treino);
				avaliador.evaluateModel(classificador, teste);
				System.out.println(avaliador.toSummaryString("\nResults\n======\n", false));
			}

		} catch (Exception ex) {
			logger.error("Erro na construção do modelo para: " + elementoGrafo.getNome(), ex);
		}
	}

}
