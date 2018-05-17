package br.unb.cic.extrator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Point;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.arco.Arco;
import br.unb.cic.extrator.dominio.arco.ArcoRepository;
import br.unb.cic.extrator.dominio.localizacao.Localizacao;
import br.unb.cic.extrator.dominio.localizacao.LocalizacaoRepository;
import br.unb.cic.extrator.dominio.no.No;
import br.unb.cic.extrator.dominio.no.NoRepository;

@Component
public class GeradorDeDadosDeTrafego {

	@Autowired
	private ArcoRepository arcoRepository;

	@Autowired
	private NoRepository noRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

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
		List<String> listaOnibus = localizacaoRepository.findDistinctOnibus();
		for (String onibus : listaOnibus) {
			extrairDadosDeTrafegoPorOnibus(onibus);
		}
	}

	private void extrairDadosDeTrafegoPorOnibus(String onibus) {
		List<Localizacao> localizacoes = localizacaoRepository.findByOnibusAndProcessadoOrderByDataHoraAsc(onibus,
				false);
		encontraElementoGrafoParaCadaLocalizacao(localizacoes);

		for (int i = 0; i < localizacoes.size(); i++) {
			if (localizacoes.get(i).getElementoGrafo().getClass().equals(Arco.class)) {
				List<Localizacao> trecho = new ArrayList<Localizacao>();
				trecho.add(localizacoes.get(i));
				while (localizacoes.get(i).getElementoGrafo() == localizacoes.get(i + 1).getElementoGrafo()) {
					i = i + 1;
					trecho.add(localizacoes.get(i));
				}
				extrairDadosDeTravegoDeTrecho(trecho);
				// TODO: Parei aqui!
			}
		}
	}

	private void extrairDadosDeTravegoDeTrecho(List<Localizacao> trecho) {
		// TODO Auto-generated method stub

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
