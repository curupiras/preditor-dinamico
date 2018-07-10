package br.unb.cic.preditorhistorico;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.unb.cic.controladorsimulacao.Parametros;
import br.unb.cic.controladorsimulacao.Resultado;
import br.unb.cic.controladorsimulacao.Resultados;
import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.preditorhistorico.resultados.GravadorResultados;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

@Component
@Scope("prototype")
public class ConstruirModeloTask implements Runnable {

	private ElementoGrafo elementoGrafo;
	private Parametros parametros;

	@Value("${preditor.avaliacaoCruzada}")
	private boolean avaliacaoCruzada;

	@Value("${preditor.treinoETeste}")
	private boolean treinoETeste;

	@Value("${preditor.porcentagemDeTeste}")
	private double porcentagemDeTeste;

	@Value("${preditor.numeroDeFolds}")
	private int numFolds;

	@Autowired
	private GravadorResultados gravadorResultados;

	@Autowired
	private Resultados resultados;

	@Autowired
	private GeradorDeInstances geradorDeInstances;

	private static final Logger logger = Logger.getLogger(ConstruirModeloTask.class.getName());

	@Override
	public void run() {

		logger.info("Inicio da construção do Modelo para o arco " + elementoGrafo.getNome());

		try {
			Resultado resultado = new Resultado(parametros);
			resultado.setElementoGrafo(elementoGrafo.getNome());

			Instances dados = null;

			try {
				dados = geradorDeInstances.getInstances(elementoGrafo, resultado.getTemposViagemAnteriores());
				dados.setClassIndex(0);

			} catch (Exception e) {
				logger.error("Erro ao gerar instances para elemento: " + elementoGrafo.getNome(), e);
			}

			// TODO: passar o objeto SMOreg como parametro assim como os Options
			// SMOreg classificador = new SMOreg();
			IBk classificador = new IBk(5);

			Evaluation avaliador = null;

			if (avaliacaoCruzada) {
				logger.info("Inicio do buildClassifier " + elementoGrafo.getNome());
				classificador.buildClassifier(dados);
				logger.info("Fim do buildClassifier " + elementoGrafo.getNome());

				avaliador = new Evaluation(dados);

				logger.info("Inicio do crossValidateModel " + elementoGrafo.getNome());
				avaliador.crossValidateModel(classificador, dados, numFolds, new Random(1));
				logger.info("Fim do crossValidateModel " + elementoGrafo.getNome());
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

				logger.info("Inicio do buildClassifier " + elementoGrafo.getNome());
				classificador.buildClassifier(treino);
				logger.info("Fim do buildClassifier " + elementoGrafo.getNome());

				avaliador = new Evaluation(treino);
				logger.info("Inicio do evaluateModel " + elementoGrafo.getNome());
				avaliador.evaluateModel(classificador, teste);
				logger.info("Fim do evaluateModel " + elementoGrafo.getNome());
			}

			if (avaliador != null) {
				List<String> listaResultado = getResultado(avaliador, resultado);
				gravadorResultados.escreverResultado(listaResultado, resultado.getElementoGrafo());
				resultado.setDatahora(new Date());
				resultados.add(resultado);
				criarArff(dados, resultado);
				// System.out.println(avaliador.toSummaryString("\nResults\n======\n",
				// false));
			}

		} catch (Exception ex) {
			logger.error("Erro na construção do modelo para: " + elementoGrafo.getNome(), ex);
		}

		logger.info("Fim da construção do Modelo para o arco " + elementoGrafo.getNome());
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

	private String getNomeArquivoArff(Resultado resultado) {
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

	public ElementoGrafo getElementoGrafo() {
		return elementoGrafo;
	}

	public void setElementoGrafo(ElementoGrafo elementoGrafo) {
		this.elementoGrafo = elementoGrafo;
	}

	public Parametros getParametros() {
		return parametros;
	}

	public void setParametros(Parametros parametros) {
		this.parametros = parametros;
	}

}

// classificador.setOptions(Utils.splitOptions(
// "-C 1.0 -N 0 -I
// \"weka.classifiers.functions.supportVector.RegSMOImproved -T
// 0.001 -V -P 1.0E-12 -L 0.001 -W 1\" -K
// \"weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C
// 250007\""));
