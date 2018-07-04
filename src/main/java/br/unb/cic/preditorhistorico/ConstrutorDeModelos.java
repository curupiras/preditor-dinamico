package br.unb.cic.preditorhistorico;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.unb.cic.controladorsimulacao.Resultado;
import br.unb.cic.controladorsimulacao.ResultadoRepository;
import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.arco.Arco;
import br.unb.cic.extrator.dominio.arco.ArcoRepository;
import br.unb.cic.extrator.dominio.no.No;
import br.unb.cic.extrator.dominio.no.NoRepository;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagemRepository;
import br.unb.cic.preditorhistorico.resultados.GravadorResultados;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
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
	private GeradorDeInstances geradorDeInstances;

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private NoRepository noRepository;

	@Autowired
	private GravadorResultados gravadorResultados;

	@Autowired
	private ResultadoRepository resultadoRepository;

	@Autowired
	private TempoViagemRepository tempoViagemRepository;

	private List<Arco> arcos;
	private List<No> nos;

	private static final Logger logger = Logger.getLogger(ConstrutorDeModelos.class.getName());

	@PostConstruct
	public void init() {
		this.arcos = arcoRepository.findAllByOrderByIdAsc();
		this.nos = noRepository.findAllByOrderByIdAsc();
	}

	// @Scheduled(initialDelay = 0, fixedRate = 86400000)
	public void construirModelo(Resultado resultado) {
		for (Arco arco : arcos) {
			logger.info("Iniciando construção do Modelo para o arco " + arco.getNome());
			construirModelo(arco, resultado);
			logger.info("Fim da construção do Modelo para o arco " + arco.getNome());
		}

		for (No no : nos) {
			logger.info("Iniciando construção do Modelo para o no " + no.getNome());
			construirModelo(no, resultado);
			logger.info("Fim da construção do Modelo para o no " + no.getNome());
		}
	}

	private void construirModelo(ElementoGrafo elementoGrafo, Resultado resultado) {
		try {

			resultado.setId(0);
			resultado.setElementoGrafo(elementoGrafo.getNome());

			Instances dados = geradorDeInstances.getInstancesFromDB(elementoGrafo,
					resultado.getTemposViagemAnteriores());
			// dados.randomize(new Random(42));
			dados.setClassIndex(0);

			// TODO: passar o objeto SMOreg como parametro assim como os Options
			SMOreg classificador = new SMOreg();
//			classificador.setOptions(Utils.splitOptions(
//					"-C 1.0 -N 0 -I \"weka.classifiers.functions.supportVector.RegSMOImproved -T 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007\""));

			Evaluation avaliador = null;

			if (avaliacaoCruzada) {
				logger.info("Inicio do buildClassifier");
				classificador.buildClassifier(dados);
				logger.info("Fim do buildClassifier");

				avaliador = new Evaluation(dados);
				
				logger.info("Inicio do crossValidateModel");
				avaliador.crossValidateModel(classificador, dados, numFolds, new Random(1));
				logger.info("Fim do crossValidateModel");
			}

			if (treinoETeste) {
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

				classificador.buildClassifier(treino);

				avaliador = new Evaluation(treino);
				avaliador.evaluateModel(classificador, teste);
			}

			if (avaliador != null) {
				List<String> listaResultado = getResultado(avaliador, resultado);
				gravadorResultados.escreverResultado(elementoGrafo, listaResultado);
				resultado.setDatahora(new Date());
				tempoViagemRepository.updateProcessado(resultado.getElementoGrafo());
				resultadoRepository.save(resultado);
				criarArff(dados, resultado);
//				System.out.println(avaliador.toSummaryString("\nResults\n======\n", false));
			}

		} catch (Exception ex) {
			logger.error("Erro na construção do modelo para: " + elementoGrafo.getNome(), ex);
		}
	}

	private void criarArff(Instances dados, Resultado resultado) {
		ArffSaver s = new ArffSaver();
		s.setInstances(dados);
		try {
			s.setFile(new File(getNomeArquivoArff(resultado)));
			s.writeBatch();
		} catch (IOException e) {
			logger.error("Erro ao tentar criar arquivo arff: ", e);
		}
	}

	private List<String> getResultado(Evaluation avaliador, Resultado resultado) throws Exception {
		List<String> listaResultado = new ArrayList<>();

		listaResultado.add(Double.toString(avaliador.correlationCoefficient()));
		resultado.setCoeficienteCorrelacao(avaliador.correlationCoefficient());

		listaResultado.add(Double.toString(avaliador.meanAbsoluteError()));
		resultado.setMeanAbsoluteError(avaliador.meanAbsoluteError());

		listaResultado.add(Double.toString(avaliador.rootMeanSquaredError()));
		resultado.setRootMeanSquaredError(avaliador.rootMeanSquaredError());

		listaResultado.add(Double.toString(avaliador.relativeAbsoluteError()));
		resultado.setRelativeAbsoluteArror(avaliador.relativeAbsoluteError());

		listaResultado.add(Double.toString(avaliador.rootRelativeSquaredError()));
		resultado.setRootRelativeSquaredError(avaliador.rootRelativeSquaredError());

		listaResultado.add(Double.toString(avaliador.numInstances()));
		resultado.setNumInstances((new Double(avaliador.numInstances())).intValue());

		return listaResultado;
	}
	
	private String getNomeArquivoArff(Resultado resultado){
		StringBuffer sb = new StringBuffer();
		sb.append("resultado/");
		sb.append(resultado.getElementoGrafo());
		sb.append("/");
		sb.append(resultado.getElementoGrafo());
		sb.append("-");
		sb.append(resultado.getIjklm());
		sb.append(".arff");
		return sb.toString();
	}

}
