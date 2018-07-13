package br.unb.cic.experimentoibk;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemovePercentage;

@Component
@Scope("prototype")
public class TarefaConstrutorModeloIbk implements Runnable {

	private ElementoGrafo elementoGrafo;
	private String cenario;

	@Value("${preditor.avaliacaoCruzada}")
	private boolean avaliacaoCruzada;

	@Value("${preditor.treinoETeste}")
	private boolean treinoETeste;

	@Value("${preditor.porcentagemDeTeste}")
	private double porcentagemDeTeste;

	@Value("${preditor.numeroDeFolds}")
	private int numFolds;

	@Autowired
	private ResultadosExpIbk resultados;

	private static final Logger logger = Logger.getLogger(TarefaConstrutorModeloIbk.class.getName());

	@Override
	public void run() {

		Instances dados = null;
		Instances treino = null;
		Instances teste = null;
		DataSource source = null;

		try {

			source = new DataSource(getNomeArquivoArff(elementoGrafo, cenario));
			dados = source.getDataSet();
			dados.setClassIndex(0);

			RemovePercentage rp = new RemovePercentage();
			rp.setInputFormat(dados);
			rp.setPercentage(porcentagemDeTeste);
			treino = Filter.useFilter(dados, rp);

			rp = new RemovePercentage();
			rp.setInputFormat(dados);
			rp.setPercentage(porcentagemDeTeste);
			rp.setInvertSelection(true);
			teste = Filter.useFilter(dados, rp);

			treino.setClassIndex(0);
			teste.setClassIndex(0);

		} catch (Exception e) {
			logger.error("Erro ao carregar: " + getNomeArquivoArff(elementoGrafo, cenario), e);
			return;
		}

		for (int k = 1; k < 53; k = k + 3) {

			logger.info("Inicio da construção do Modelo " + k + " para o arco " + elementoGrafo.getNome());

			ResultadoExpIbk resultado = new ResultadoExpIbk();
			resultado.setElementoGrafo(elementoGrafo.getNome());
			resultado.setIjklm(cenario);
			resultado.setK(k);

			try {
				IBk classificador = new IBk(k);
				Evaluation avaliador = null;

				classificador.buildClassifier(treino);
				avaliador = new Evaluation(treino);
				avaliador.evaluateModel(classificador, teste);

				if (avaliador != null) {
					atualizaResultado(avaliador, resultado);
					resultado.setDatahora(new Date());
					resultados.add(resultado);
				}

			} catch (Exception ex) {
				logger.error("Erro na construção do modelo para: " + elementoGrafo.getNome(), ex);
			}

			logger.info("Fim da construção do Modelo " + k + " para o arco " + elementoGrafo.getNome());
		}
	}

	private void atualizaResultado(Evaluation avaliador, ResultadoExpIbk resultado) throws Exception {
		resultado.setCoeficienteCorrelacao(avaliador.correlationCoefficient());
		resultado.setMeanAbsoluteError(avaliador.meanAbsoluteError());
		resultado.setRootMeanSquaredError(avaliador.rootMeanSquaredError());
		resultado.setRelativeAbsoluteArror(avaliador.relativeAbsoluteError());
		resultado.setRootRelativeSquaredError(avaliador.rootRelativeSquaredError());
		resultado.setNumInstances((new Double(avaliador.numInstances())).intValue());
	}

	private String getNomeArquivoArff(ElementoGrafo elementoGrafo, String cenario) {
		StringBuffer sb = new StringBuffer();
		sb.append("resultado-ibk/");
		sb.append(elementoGrafo.getNome());
		sb.append("/");
		sb.append(elementoGrafo.getNome());
		sb.append("-");
		sb.append(cenario);
		sb.append(".arff");
		return sb.toString();
	}

	public ElementoGrafo getElementoGrafo() {
		return elementoGrafo;
	}

	public void setElementoGrafo(ElementoGrafo elementoGrafo) {
		this.elementoGrafo = elementoGrafo;
	}

	public String getCenario() {
		return cenario;
	}

	public void setCenario(String cenario) {
		this.cenario = cenario;
	}

}
