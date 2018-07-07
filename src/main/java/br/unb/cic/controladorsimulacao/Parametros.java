package br.unb.cic.controladorsimulacao;

import javax.persistence.Transient;

public class Parametros {

	private double probabilidadeEventoGrave;

	private double probabilidadeEventoModerado;

	private double probabilidadeEventoLeve;

	private double fatorCorrecaoLeve;

	private double fatorCorrecaoModerado;

	private double fatorCorrecaoGrave;

	private double fatorCorrecaoHorario;

	private double fatorInfluenciaLeve;

	private double fatorInfluenciaModerado;

	private double fatorInfluenciaForte;

	private double fatorOscilacaoAtraso;

	private double fatorOscilacaoVelocidade;

	private int temposViagemAnteriores;

	@Transient
	private String ijklm;

	public double getProbabilidadeEventoGrave() {
		return probabilidadeEventoGrave;
	}

	public void setProbabilidadeEventoGrave(double probabilidadeEventoGrave) {
		this.probabilidadeEventoGrave = probabilidadeEventoGrave;
	}

	public double getProbabilidadeEventoModerado() {
		return probabilidadeEventoModerado;
	}

	public void setProbabilidadeEventoModerado(double probabilidadeEventoModerado) {
		this.probabilidadeEventoModerado = probabilidadeEventoModerado;
	}

	public double getProbabilidadeEventoLeve() {
		return probabilidadeEventoLeve;
	}

	public void setProbabilidadeEventoLeve(double probabilidadeEventoLeve) {
		this.probabilidadeEventoLeve = probabilidadeEventoLeve;
	}

	public double getFatorCorrecaoLeve() {
		return fatorCorrecaoLeve;
	}

	public void setFatorCorrecaoLeve(double fatorCorrecaoLeve) {
		this.fatorCorrecaoLeve = fatorCorrecaoLeve;
	}

	public double getFatorCorrecaoModerado() {
		return fatorCorrecaoModerado;
	}

	public void setFatorCorrecaoModerado(double fatorCorrecaoModerado) {
		this.fatorCorrecaoModerado = fatorCorrecaoModerado;
	}

	public double getFatorCorrecaoGrave() {
		return fatorCorrecaoGrave;
	}

	public void setFatorCorrecaoGrave(double fatorCorrecaoGrave) {
		this.fatorCorrecaoGrave = fatorCorrecaoGrave;
	}

	public double getFatorCorrecaoHorario() {
		return fatorCorrecaoHorario;
	}

	public void setFatorCorrecaoHorario(double fatorCorrecaoHorario) {
		this.fatorCorrecaoHorario = fatorCorrecaoHorario;
	}

	public double getFatorInfluenciaLeve() {
		return fatorInfluenciaLeve;
	}

	public void setFatorInfluenciaLeve(double fatorInfluenciaLeve) {
		this.fatorInfluenciaLeve = fatorInfluenciaLeve;
	}

	public double getFatorInfluenciaModerado() {
		return fatorInfluenciaModerado;
	}

	public void setFatorInfluenciaModerado(double fatorInfluenciaModerado) {
		this.fatorInfluenciaModerado = fatorInfluenciaModerado;
	}

	public double getFatorInfluenciaForte() {
		return fatorInfluenciaForte;
	}

	public void setFatorInfluenciaForte(double fatorInfluenciaForte) {
		this.fatorInfluenciaForte = fatorInfluenciaForte;
	}

	public double getFatorOscilacaoAtraso() {
		return fatorOscilacaoAtraso;
	}

	public void setFatorOscilacaoAtraso(double fatorOscilacaoAtraso) {
		this.fatorOscilacaoAtraso = fatorOscilacaoAtraso;
	}

	public double getFatorOscilacaoVelocidade() {
		return fatorOscilacaoVelocidade;
	}

	public void setFatorOscilacaoVelocidade(double fatorOscilacaoVelocidade) {
		this.fatorOscilacaoVelocidade = fatorOscilacaoVelocidade;
	}

	public int getTemposViagemAnteriores() {
		return temposViagemAnteriores;
	}

	public void setTemposViagemAnteriores(int temposViagemAnteriores) {
		this.temposViagemAnteriores = temposViagemAnteriores;
	}

	public String getIjklm() {
		return ijklm;
	}

	public void setIjklm(String ijklm) {
		this.ijklm = ijklm;
	}

}
