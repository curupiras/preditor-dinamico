package br.unb.cic.extrator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.arco.Arco;
import br.unb.cic.extrator.dominio.arco.ArcoRepository;
import br.unb.cic.extrator.dominio.localizacao.Localizacao;
import br.unb.cic.extrator.dominio.localizacao.LocalizacaoRepository;
import br.unb.cic.extrator.dominio.no.No;
import br.unb.cic.extrator.dominio.no.NoRepository;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagem;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagemRepository;
import br.unb.cic.parametros.Parametros;

@Component
public class GeradorDeDadosDeTrafego {

	private static final Log logger = LogFactory.getLog(GeradorDeDadosDeTrafego.class);

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private NoRepository noRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private TempoViagemRepository tempoViagemRepository;

	private List<Arco> arcos;
	private List<No> nos;

	@PostConstruct
	public void init() {
		this.arcos = arcoRepository.findAllByOrderByIdAsc();
		this.nos = noRepository.findAllByOrderByIdAsc();

		for (int i = 0; i < nos.size(); i++) {
			nos.get(i).setProximo(arcos.get(i));
		}

		for (int i = 0; i < arcos.size(); i++) {
			arcos.get(i).setAnterior(nos.get(i));
		}

		for (int i = 1; i < nos.size(); i++) {
			nos.get(i).setAnterior(arcos.get(i - 1));
		}

		for (int i = 0; i < arcos.size() - 1; i++) {
			arcos.get(i).setProximo(nos.get(i + 1));
		}

		arcos.get(arcos.size() - 1).setProximo(nos.get(0));
		nos.get(0).setAnterior(arcos.get(arcos.size() - 1));
	}

	@Scheduled(initialDelay = 0, fixedRate = 60000)
	public void scheduledTask() {
		logger.info("Iniciando geração de dados de tráfego.");
		List<String> listaOnibus = localizacaoRepository.findDistinctOnibus();
		for (String onibus : listaOnibus) {
			extrairDadosDeTrafegoPorOnibus(onibus);
		}
		logger.info("Fim da geração de dados de tráfego.");
	}

	private void extrairDadosDeTrafegoPorOnibus(String onibus) {
		List<Localizacao> localizacoes = localizacaoRepository.findByOnibusAndProcessadoOrderByDataHoraAsc(onibus,
				false);
		encontraElementoGrafoParaCadaLocalizacao(localizacoes);

		for (int i = 0; i < localizacoes.size(); i++) {
			// TODO: Levar em consideração tempo dispendido na parada mesmo que
			// o onibus passe de um arco para outro e não seja capturado nenhuma
			// medida na parada
			if (localizacoes.get(i).getElementoGrafo().getClass().equals(Arco.class)) {

				List<Localizacao> trecho = new ArrayList<Localizacao>();
				trecho.add(localizacoes.get(i));

				while (i + 1 < localizacoes.size()
						&& localizacoes.get(i).getElementoGrafo() == localizacoes.get(i + 1).getElementoGrafo()) {

					i = i + 1;
					trecho.add(localizacoes.get(i));
				}

				extrairDadosDeTrafegoDeTrecho(trecho);
			} else {

				List<Localizacao> parada = new ArrayList<Localizacao>();
				int primeiroIndiceDaParada = i;
				int ultimoIndiceDaParada = i;

				parada.add(localizacoes.get(i));

				while (i + 1 < localizacoes.size()
						&& localizacoes.get(i).getElementoGrafo() == localizacoes.get(i + 1).getElementoGrafo()) {

					i = i + 1;
					parada.add(localizacoes.get(i));
					ultimoIndiceDaParada = i;
				}

				Localizacao localizacaoAnterior;
				Localizacao localizacaoPosterior;

				if (primeiroIndiceDaParada == 0 || localizacoes.get(primeiroIndiceDaParada).getElementoGrafo()
						.getAnterior() != localizacoes.get(primeiroIndiceDaParada - 1).getElementoGrafo()) {
					localizacaoAnterior = null;
				} else {
					localizacaoAnterior = localizacoes.get(primeiroIndiceDaParada - 1);
				}

				if (ultimoIndiceDaParada == localizacoes.size() - 1
						|| localizacoes.get(ultimoIndiceDaParada).getElementoGrafo().getProximo() != localizacoes
								.get(ultimoIndiceDaParada + 1).getElementoGrafo()) {
					localizacaoPosterior = null;
				} else {
					localizacaoPosterior = localizacoes.get(ultimoIndiceDaParada + 1);
				}

				extrairDadosDeTrafegoDeParada(parada, localizacaoAnterior, localizacaoPosterior);
			}
		}
	}

	private void extrairDadosDeTrafegoDeParada(List<Localizacao> parada, Localizacao localizacaoAnterior,
			Localizacao localizacaoPosterior) {
		
		if(localizacaoAnterior == null || localizacaoPosterior == null){
			//TODO: Gravar null ou um valor padrão?
			armazenarDadosDeTrafegoDaParada(parada, null);
			return;
		}
		
		long tempo = 0;

		// Calcula tempo entre localizacoes da mesma parada
		for (int i = 0; i < parada.size() - 1; i++) {
			tempo = tempo + Math.abs(parada.get(i).getDataHora().getTime() - parada.get(i + 1).getDataHora().getTime())
					/ 1000;
		}

		// Calcula tempo entre chegada do onibus e primeira medida na parada
		if (localizacaoAnterior != null) {
			Point pontoLocalizacaoAnterior = localizacaoAnterior.getGeoPto();
			Geometry pontoDaParada = ((No) parada.get(0).getElementoGrafo()).getPonto();
			// TODO:Calcular essa distância andando sobre a linha e não em linha
			// reta
			double distancia = pontoLocalizacaoAnterior.distance(pontoDaParada);
			double velocidade = localizacaoAnterior.getVelocidade()
					* Parametros.KILOMETROS_POR_HORA_PARA_METROS_POR_SEGUNDO;
			double periodo = distancia / velocidade;

			long tempoLocalizacaoAnteriorParada = Math
					.abs(parada.get(0).getDataHora().getTime() - localizacaoAnterior.getDataHora().getTime()) / 1000;

			long diferenca = tempoLocalizacaoAnteriorParada - (long) Math.round(periodo);

			if (diferenca > 0) {
				tempo = tempo + diferenca;
			}
		}

		// Calcula tempo entre ultima medida na parada e saida do onibus
		if (localizacaoPosterior != null) {
			Point pontoLocalizacaoPosterior = localizacaoPosterior.getGeoPto();
			Geometry pontoDaParada = ((No) parada.get(parada.size() - 1).getElementoGrafo()).getPonto();
			// TODO:Calcular essa distância andando sobre a linha e não em linha
			// reta
			double distancia = pontoLocalizacaoPosterior.distance(pontoDaParada);
			double velocidade = localizacaoPosterior.getVelocidade()
					* Parametros.KILOMETROS_POR_HORA_PARA_METROS_POR_SEGUNDO;
			double periodo = distancia / velocidade;

			long tempoParadaLocalizacaoPosterior = Math.abs(parada.get(parada.size() - 1).getDataHora().getTime()
					- localizacaoPosterior.getDataHora().getTime()) / 1000;

			long diferenca = tempoParadaLocalizacaoPosterior - (long) Math.round(periodo);

			if (diferenca > 0) {
				tempo = tempo + diferenca;
			}
		}

		armazenarDadosDeTrafegoDaParada(parada, tempo);

	}

	private void extrairDadosDeTrafegoDeTrecho(List<Localizacao> trecho) {
		long tempo = 0;

		// Calcula tempo entre pontos do trecho
		for (int i = 0; i < trecho.size() - 1; i++) {
			tempo = tempo + Math.abs(trecho.get(i).getDataHora().getTime() - trecho.get(i + 1).getDataHora().getTime())
					/ 1000;
		}

		// Calcula tempo entre primeiro ponto do trecho e parada anterior
		Point primeiroPontoDoTrecho = trecho.get(0).getGeoPto();
		Geometry paradaAnterior = ((No) trecho.get(0).getElementoGrafo().getAnterior()).getPonto();
		// TODO:Calcular essa distância andando sobre a linha e não em linha
		// reta
		double distancia = primeiroPontoDoTrecho.distance(paradaAnterior);
		double velocidade = trecho.get(0).getVelocidade() * Parametros.KILOMETROS_POR_HORA_PARA_METROS_POR_SEGUNDO;
		double periodo = distancia / velocidade;
		tempo = tempo + (long) Math.round(periodo);

		// Calcula tempo entre ultimo ponto do trecho e parada posterior
		Point ultimoPontoDoTrecho = trecho.get(trecho.size() - 1).getGeoPto();
		Geometry paradaPosterior = ((No) trecho.get(trecho.size() - 1).getElementoGrafo().getProximo()).getPonto();
		// TODO:Calcular essa distância andando sobre a linha e não em linha
		// reta
		distancia = ultimoPontoDoTrecho.distance(paradaPosterior);
		velocidade = trecho.get(trecho.size() - 1).getVelocidade()
				* Parametros.KILOMETROS_POR_HORA_PARA_METROS_POR_SEGUNDO;
		periodo = distancia / velocidade;
		tempo = tempo + (long) Math.round(periodo);

		armazenarDadosDeTrafegoDoTrecho(trecho, tempo);

	}

	private void armazenarDadosDeTrafegoDoTrecho(List<Localizacao> trecho, long tempo) {
		Localizacao primeiraLocalizacao = trecho.get(0);
		TempoViagem tempoViagem = new TempoViagem(primeiraLocalizacao.getDataHora(),
				primeiraLocalizacao.getElementoGrafo().getNome(), tempo);
		tempoViagemRepository.save(tempoViagem);
		for (Localizacao localizacao : trecho) {
			localizacao.setProcessado(true);
			localizacaoRepository.save(localizacao);
		}
	}

	private void armazenarDadosDeTrafegoDaParada(List<Localizacao> parada, Long tempo) {
		Localizacao primeiraLocalizacao = parada.get(0);
		TempoViagem tempoViagem = new TempoViagem(primeiraLocalizacao.getDataHora(),
				primeiraLocalizacao.getElementoGrafo().getNome(), tempo);
		tempoViagemRepository.save(tempoViagem);
		for (Localizacao localizacao : parada) {
			localizacao.setProcessado(true);
			localizacaoRepository.save(localizacao);
		}
	}

	private void encontraElementoGrafoParaCadaLocalizacao(List<Localizacao> localizacoes) {
		for (Localizacao localizacao : localizacoes) {
			No no = getNoQueIntercepta(localizacao.getGeoPto());
			if (no == null) {
				localizacao.setElementoGrafo(getArcoQueIntercepta(localizacao.getGeoPto()));
			} else {
				localizacao.setElementoGrafo(no);
			}
		}

	}

	private ElementoGrafo getArcoQueIntercepta(Point geoPto) {
		for (Arco arco : arcos) {
			if (geoPto.distance(arco.getGeoLinha()) < 1) {
				return arco;
			}
		}

		return null;
	}

	private No getNoQueIntercepta(Point geoPto) {

		for (No no : nos) {
			if (geoPto.intersects(no.getPonto())) {
				return no;
			}
		}

		return null;
	}

}
