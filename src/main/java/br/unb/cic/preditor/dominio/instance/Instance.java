package br.unb.cic.preditor.dominio.instance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "instance_preditor")
public class Instance {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	@Column(name = "id_tempo_viagem_preditor")
	private int idTempoViagem;

	@Column(name = "periodo_do_dia")
	private double periodoDoDia;

	@Column(name = "dia_da_semana")
	private double diaDaSemana;

	@Column(name = "tempo_de_viagem_1")
	private double tempoViagem1;

	@Column(name = "tempo_de_viagem_2")
	private double tempoViagem2;

	@Column(name = "tempo_de_viagem_3")
	private double tempoViagem3;

	@Column(name = "tempo_de_viagem_4")
	private double tempoViagem4;

	@Column(name = "tempo_de_viagem_5")
	private double tempoViagem5;

	@Column(name = "tempo_de_viagem_6")
	private double tempoViagem6;

	@Column(name = "velocidade_media")
	private double velocidadeMedia;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getIdTempoViagem() {
		return idTempoViagem;
	}

	public void setIdTempoViagem(int idTempoViagem) {
		this.idTempoViagem = idTempoViagem;
	}

	public double getPeriodoDoDia() {
		return periodoDoDia;
	}

	public void setPeriodoDoDia(double periodoDoDia) {
		this.periodoDoDia = periodoDoDia;
	}

	public double getDiaDaSemana() {
		return diaDaSemana;
	}

	public void setDiaDaSemana(double diaDaSemana) {
		this.diaDaSemana = diaDaSemana;
	}

	public double getTempoViagem1() {
		return tempoViagem1;
	}

	public void setTempoViagem1(double tempoViagem1) {
		this.tempoViagem1 = tempoViagem1;
	}

	public double getTempoViagem2() {
		return tempoViagem2;
	}

	public void setTempoViagem2(double tempoViagem2) {
		this.tempoViagem2 = tempoViagem2;
	}

	public double getTempoViagem3() {
		return tempoViagem3;
	}

	public void setTempoViagem3(double tempoViagem3) {
		this.tempoViagem3 = tempoViagem3;
	}

	public double getTempoViagem4() {
		return tempoViagem4;
	}

	public void setTempoViagem4(double tempoViagem4) {
		this.tempoViagem4 = tempoViagem4;
	}

	public double getTempoViagem5() {
		return tempoViagem5;
	}

	public void setTempoViagem5(double tempoViagem5) {
		this.tempoViagem5 = tempoViagem5;
	}

	public double getTempoViagem6() {
		return tempoViagem6;
	}

	public void setTempoViagem6(double tempoViagem6) {
		this.tempoViagem6 = tempoViagem6;
	}

	public double getVelocidadeMedia() {
		return velocidadeMedia;
	}

	public void setVelocidadeMedia(double velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}

}
