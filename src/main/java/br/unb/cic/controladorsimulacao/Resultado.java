package br.unb.cic.controladorsimulacao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "resultado_preditor")
public class Resultado {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "id")
	private long id;

	@Column(name = "elemento_grafo")
	private String elementoGrafo;

	@Column(name = "datahora")
	private Date datahora;

	@Column(name = "probabilidade_evento_grave")
	private double probabilidadeEventoGrave;

	@Column(name = "probabilidade_evento_moderado")
	private double probabilidadeEventoModerado;

	@Column(name = "probabilidade_evento_leve")
	private double probabilidadeEventoLeve;

	@Column(name = "fator_correcao_leve")
	private double fatorCorrecaoLeve;

	@Column(name = "fator_correcao_moderado")
	private double fatorCorrecaoModerado;

	@Column(name = "fator_correcao_grave")
	private double fatorCorrecaoGrave;

	@Column(name = "fator_correcao_horario")
	private double fatorCorrecaoHorario;

	@Column(name = "fator_influencia_leve")
	private double fatorInfluenciaLeve;

	@Column(name = "fator_influencia_moderado")
	private double fatorInfluenciaModerado;

	@Column(name = "fator_influencia_forte")
	private double fatorInfluenciaForte;

	@Column(name = "fator_oscilacao_atraso")
	private double fatorOscilacaoAtraso;

	@Column(name = "fator_oscilacao_velocidade")
	private double fatorOscilacaoVelocidade;

	@Column(name = "tempos_viagem_anteriores")
	private int temposViagemAnteriores;

	@Column(name = "coeficiente_correlacao")
	private double coeficienteCorrelacao;

	@Column(name = "mean_absolute_error")
	private double meanAbsoluteError;

	@Column(name = "root_mean_squared_error")
	private double rootMeanSquaredError;

	@Column(name = "relative_absolute_error")
	private double relativeAbsoluteError;

	@Column(name = "root_relative_squared_error")
	private double rootRelativeSquaredError;

	@Column(name = "num_instances")
	private int numInstances;
	
	@Transient
	private String ijklm;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDatahora() {
		return datahora;
	}

	public void setDatahora(Date datahora) {
		this.datahora = datahora;
	}

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

	public double getCoeficienteCorrelacao() {
		return coeficienteCorrelacao;
	}

	public void setCoeficienteCorrelacao(double coeficienteCorrelacao) {
		this.coeficienteCorrelacao = coeficienteCorrelacao;
	}

	public double getMeanAbsoluteError() {
		return meanAbsoluteError;
	}

	public void setMeanAbsoluteError(double meanAbsoluteError) {
		this.meanAbsoluteError = meanAbsoluteError;
	}

	public double getRootMeanSquaredError() {
		return rootMeanSquaredError;
	}

	public void setRootMeanSquaredError(double rootMeanSquaredError) {
		this.rootMeanSquaredError = rootMeanSquaredError;
	}

	public double getRelativeAbsoluteArror() {
		return relativeAbsoluteError;
	}

	public void setRelativeAbsoluteArror(double relativeAbsoluteArror) {
		this.relativeAbsoluteError = relativeAbsoluteArror;
	}

	public int getNumInstances() {
		return numInstances;
	}

	public void setNumInstances(int numInstances) {
		this.numInstances = numInstances;
	}

	public double getRootRelativeSquaredError() {
		return rootRelativeSquaredError;
	}

	public void setRootRelativeSquaredError(double rootRelativeSquaredError) {
		this.rootRelativeSquaredError = rootRelativeSquaredError;
	}

	public double getRelativeAbsoluteError() {
		return relativeAbsoluteError;
	}

	public void setRelativeAbsoluteError(double relativeAbsoluteError) {
		this.relativeAbsoluteError = relativeAbsoluteError;
	}

	public String getElementoGrafo() {
		return elementoGrafo;
	}

	public void setElementoGrafo(String elementoGrafo) {
		this.elementoGrafo = elementoGrafo;
	}

	public String getIjklm() {
		return ijklm;
	}

	public void setIjklm(String ijklm) {
		this.ijklm = ijklm;
	}

}
