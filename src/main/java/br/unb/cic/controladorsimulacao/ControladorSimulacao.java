package br.unb.cic.controladorsimulacao;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.unb.cic.preditorhistorico.ConstrutorDeModelos;

@Component
public class ControladorSimulacao {

	@Value("${preditor.arquivoPropriedadeSimulador}")
	private String arquivoPropriedadeSimulador;

	@Value("${preditor.stringExecucaoSimulador}")
	private String stringExecucaoSimulador;

	@Value("${preditor.tempoExecucaoSimulador}")
	private long tempoExecucaoSimulador;
	
	@Value("${simulador.i}")
	private int iInicial;
	
	@Value("${simulador.j}")
	private int jInicial;
	
	@Value("${simulador.k}")
	private int kInicial;
	
	@Value("${simulador.l}")
	private int lInicial;
	
	@Value("${simulador.m}")
	private int mInicial;

	@Autowired
	private ConstrutorDeModelos construtorDeModelos;
	
	private boolean inicializado = false;

	private static final Logger logger = Logger.getLogger(ControladorSimulacao.class.getName());

	// private static final long TRES_HORAS = 3 * 60 * 60 * 1000;
	// private static final long TRINTA_DIAS = 30 * 24 * 60 * 60 * 1000;

	private List<String> probabilidadeDeOcorrenciaDeEventoGrave = new ArrayList<>();
	private List<String> probabilidadeDeOcorrenciaDeEventoModerado = new ArrayList<>();
	private List<String> probabilidadeDeOcorrenciaDeEventoLeve = new ArrayList<>();

	private List<String> fatorDeCorrecaoLeve = new ArrayList<>();
	private List<String> fatorDeCorrecaoModerado = new ArrayList<>();
	private List<String> fatorDeCorrecaoGrave = new ArrayList<>();
	private List<String> fatorDeCorrecaoHorarioDePico = new ArrayList<>();

	private List<String> fatorDeInfluenciaLeve = new ArrayList<>();
	private List<String> fatorDeInfluenciaModerado = new ArrayList<>();
	private List<String> fatorDeInfluenciaForte = new ArrayList<>();

	private List<String> fatorDeOscilacaoDoAtrasoDesvioPadrao = new ArrayList<>();
	private List<String> fatorDeOscilacaoDaVelocidadeDesvioPadrao = new ArrayList<>();

	private List<String> quantidadeDeTemposDeViagemAnteriores = new ArrayList<>();

	@PostConstruct
	public void init() {

		probabilidadeDeOcorrenciaDeEventoGrave.add("0.0000");
		probabilidadeDeOcorrenciaDeEventoGrave.add("0.0005");
		probabilidadeDeOcorrenciaDeEventoGrave.add("0.0010");

		probabilidadeDeOcorrenciaDeEventoModerado.add("0.0000");
		probabilidadeDeOcorrenciaDeEventoModerado.add("0.0010");
		probabilidadeDeOcorrenciaDeEventoModerado.add("0.0020");

		probabilidadeDeOcorrenciaDeEventoLeve.add("0.0000");
		probabilidadeDeOcorrenciaDeEventoLeve.add("0.0020");
		probabilidadeDeOcorrenciaDeEventoLeve.add("0.0040");

		fatorDeCorrecaoLeve.add("0.90");
		fatorDeCorrecaoLeve.add("0.80");
		fatorDeCorrecaoLeve.add("0.70");

		fatorDeCorrecaoModerado.add("0.75");
		fatorDeCorrecaoModerado.add("0.65");
		fatorDeCorrecaoModerado.add("0.55");

		fatorDeCorrecaoGrave.add("0.60");
		fatorDeCorrecaoGrave.add("0.50");
		fatorDeCorrecaoGrave.add("0.40");

		fatorDeCorrecaoHorarioDePico.add("0.80");
		fatorDeCorrecaoHorarioDePico.add("0.70");
		fatorDeCorrecaoHorarioDePico.add("0.60");

		fatorDeInfluenciaLeve.add("1.00");
		fatorDeInfluenciaLeve.add("0.90");
		fatorDeInfluenciaLeve.add("0.80");

		fatorDeInfluenciaModerado.add("1.00");
		fatorDeInfluenciaModerado.add("0.80");
		fatorDeInfluenciaModerado.add("0.70");

		fatorDeInfluenciaForte.add("1.00");
		fatorDeInfluenciaForte.add("0.70");
		fatorDeInfluenciaForte.add("0.60");

		fatorDeOscilacaoDoAtrasoDesvioPadrao.add("0.01");
		fatorDeOscilacaoDoAtrasoDesvioPadrao.add("0.05");
		fatorDeOscilacaoDoAtrasoDesvioPadrao.add("0.10");

		fatorDeOscilacaoDaVelocidadeDesvioPadrao.add("0.01");
		fatorDeOscilacaoDaVelocidadeDesvioPadrao.add("0.05");
		fatorDeOscilacaoDaVelocidadeDesvioPadrao.add("0.10");

		quantidadeDeTemposDeViagemAnteriores.add("6");
		quantidadeDeTemposDeViagemAnteriores.add("4");
		quantidadeDeTemposDeViagemAnteriores.add("2");

		controlarSimulacao();
	}

	// @Scheduled(fixedDelay = TRES_HORAS)
	public void controlarSimulacao() {
		Parametros parametros = new Parametros();

		for (int i = 0; i < 3; i++) {
			alterarPropriedade("simulador.probabilidadeDeOcorrenciaDeEventoGrave",
					probabilidadeDeOcorrenciaDeEventoGrave.get(i));
			parametros.setProbabilidadeEventoGrave(Double.parseDouble(probabilidadeDeOcorrenciaDeEventoGrave.get(i)));

			alterarPropriedade("simulador.probabilidadeDeOcorrenciaDeEventoModerado",
					probabilidadeDeOcorrenciaDeEventoModerado.get(i));
			parametros.setProbabilidadeEventoModerado(
					Double.parseDouble(probabilidadeDeOcorrenciaDeEventoModerado.get(i)));

			alterarPropriedade("simulador.probabilidadeDeOcorrenciaDeEventoLeve",
					probabilidadeDeOcorrenciaDeEventoLeve.get(i));
			parametros.setProbabilidadeEventoLeve(Double.parseDouble(probabilidadeDeOcorrenciaDeEventoLeve.get(i)));

			for (int j = 0; j < 3; j++) {
				alterarPropriedade("simulador.fatorDeCorrecaoLeve", fatorDeCorrecaoLeve.get(j));
				parametros.setFatorCorrecaoLeve(Double.parseDouble(fatorDeCorrecaoLeve.get(j)));

				alterarPropriedade("simulador.fatorDeCorrecaoModerado", fatorDeCorrecaoModerado.get(j));
				parametros.setFatorCorrecaoModerado(Double.parseDouble(fatorDeCorrecaoModerado.get(j)));

				alterarPropriedade("simulador.fatorDeCorrecaoGrave", fatorDeCorrecaoGrave.get(j));
				parametros.setFatorCorrecaoGrave(Double.parseDouble(fatorDeCorrecaoGrave.get(j)));

				alterarPropriedade("simulador.fatorDeCorrecaoHorarioDePico", fatorDeCorrecaoHorarioDePico.get(j));
				parametros.setFatorCorrecaoHorario(Double.parseDouble(fatorDeCorrecaoHorarioDePico.get(j)));

				for (int k = 0; k < 3; k++) {
					alterarPropriedade("simulador.fatorDeInfluenciaLeve", fatorDeInfluenciaLeve.get(k));
					parametros.setFatorInfluenciaLeve(Double.parseDouble(fatorDeInfluenciaLeve.get(k)));

					alterarPropriedade("simulador.fatorDeInfluenciaModerado", fatorDeInfluenciaModerado.get(k));
					parametros.setFatorInfluenciaModerado(Double.parseDouble(fatorDeInfluenciaModerado.get(k)));

					alterarPropriedade("simulador.fatorDeInfluenciaForte", fatorDeInfluenciaForte.get(k));
					parametros.setFatorInfluenciaForte(Double.parseDouble(fatorDeInfluenciaForte.get(k)));

					for (int l = 0; l < 3; l++) {
						alterarPropriedade("simulador.fatorDeOscilacaoDoAtrasoDesvioPadrao",
								fatorDeOscilacaoDoAtrasoDesvioPadrao.get(l));
						parametros.setFatorOscilacaoAtraso(
								Double.parseDouble(fatorDeOscilacaoDoAtrasoDesvioPadrao.get(l)));

						alterarPropriedade("simulador.fatorDeOscilacaoDaVelocidadeDesvioPadrao",
								fatorDeOscilacaoDaVelocidadeDesvioPadrao.get(l));
						parametros.setFatorOscilacaoVelocidade(
								Double.parseDouble(fatorDeOscilacaoDaVelocidadeDesvioPadrao.get(l)));

						for (int m = 0; m < 3; m++) {
							
							if(inicializado == false){
								i = iInicial;
								j = jInicial;
								k = kInicial;
								l = lInicial;
								m = mInicial;
								inicializado = true;
							}

							try {
								logger.info("Iniciando simulador para ijklm=" + i + j + k + l + m);
								ProcessBuilder pb = new ProcessBuilder(stringExecucaoSimulador);
								pb.redirectOutput(Redirect.appendTo(new File("simulador.log")));
								pb.redirectError(Redirect.appendTo(new File("simulador.log")));
								Process process = pb.start();

								Thread.sleep(tempoExecucaoSimulador);

								logger.info("Finalizando simulador");
								pb.redirectOutput(Redirect.appendTo(new File("lixo.log")));
								pb.redirectError(Redirect.appendTo(new File("lixo.log")));
								process.destroy();
								finalizarSimulador();
								Thread.sleep(5000);
							} catch (InterruptedException | IOException e) {
								logger.error("Execução do simulador interrompida:", e);
							}

							parametros.setTemposViagemAnteriores(
									Integer.parseInt(quantidadeDeTemposDeViagemAnteriores.get(m)));
							parametros.setIjklm("" + i + j + k + l + m);
							logger.info("Construção de modelos para ijklm=" + parametros.getIjklm());
							construtorDeModelos.construirModelo(parametros);
						}
					}
				}
			}
		}

	}

	private void alterarPropriedade(String chave, String valor) {
		PropertiesConfiguration conf;
		try {
			conf = new PropertiesConfiguration(arquivoPropriedadeSimulador);
			conf.setProperty(chave, valor);
			conf.save();
		} catch (ConfigurationException e) {
			logger.error("Problemas para atualizar propriedade", e);
		}
	}

	private void finalizarSimulador() {
		URL url;
		try {
			url = new URL("http://localhost:8080/finalizacao");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.getResponseCode();
		} catch (SocketException se) {
			if (se.toString().contains("java.net.SocketException: Connection reset")) {
				logger.info("Fim do Simulador.");
			} else {
				logger.error("Problemas para finalizar simulador: ", se);
			}
		} catch (Exception e) {
			logger.error("Problemas para finalizar simulador: ", e);
		}
	}

}
