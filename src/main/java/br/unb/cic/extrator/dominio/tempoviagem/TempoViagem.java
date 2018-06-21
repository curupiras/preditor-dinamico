package br.unb.cic.extrator.dominio.tempoviagem;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tempo_viagem_preditor")
public class TempoViagem {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	public TempoViagem(Date dataHora, String nome, Long tempo, String onibus) {
		super();
		this.dataHora = dataHora;
		this.nome = nome;
		this.tempo = tempo;
		this.onibus = onibus;
	}

	public TempoViagem() {
	}

	@Column(name = "datahora")
	private Date dataHora;

	@Column(name = "nome")
	private String nome;

	@Column(name = "tempo")
	private Long tempo;

	@Column(name = "onibus")
	private String onibus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getTempo() {
		return tempo;
	}

	public void setTempo(Long tempo) {
		this.tempo = tempo;
	}

	public String getOnibus() {
		return onibus;
	}

	public void setOnibus(String onibus) {
		this.onibus = onibus;
	}

}
