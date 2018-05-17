package br.unb.cic.extrator;

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
		this.arcos = arcoRepository.findAll();
		this.nos = noRepository.findAll();
		System.out.println();
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
		//TODO: Parei aqui!
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
