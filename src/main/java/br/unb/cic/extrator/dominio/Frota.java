package br.unb.cic.extrator.dominio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Frota {

	@Autowired
	LocalizacaoRepository repository;

	List<Localizacao> frota;

	@JsonProperty("FROTA")
	public List<Localizacao> getFrota() {
		return this.frota;
	}

	public void setFrota(List<Localizacao> frota) {
		this.frota = frota;
	}

}
