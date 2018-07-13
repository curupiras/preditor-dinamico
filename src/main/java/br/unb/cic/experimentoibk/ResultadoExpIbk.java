package br.unb.cic.experimentoibk;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "resultado_experimento_ibk")
public class ResultadoExpIbk {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "id")
	private long id;

	@Column(name = "elemento_grafo")
	private String elementoGrafo;

	@Column(name = "k")
	private int k;

	@Column(name = "datahora")
	private Date datahora;

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

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

}
