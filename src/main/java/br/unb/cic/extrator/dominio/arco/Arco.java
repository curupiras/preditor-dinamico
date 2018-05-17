package br.unb.cic.extrator.dominio.arco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.vividsolutions.jts.geom.Geometry;

import br.unb.cic.extrator.dominio.ElementoGrafo;

@Entity
@Table(name = "arco_preditor")
public class Arco extends ElementoGrafo {

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "fid")
	private int id;

	@Column(name = "nome")
	private String nome;

	@Column(name = "tamanho")
	private double tamanho;

	@Column(name = "geo_linhas_lin")
	private Geometry geoLinha;

	@Column(name = "linha")
	private String linha;

	@Transient
	private ElementoGrafo proximo;

	@Transient
	private ElementoGrafo anterior;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getTamanho() {
		return tamanho;
	}

	public void setTamanho(double tamanho) {
		this.tamanho = tamanho;
	}

	public Geometry getGeoLinha() {
		return geoLinha;
	}

	public void setGeoLinha(Geometry geoLinha) {
		this.geoLinha = geoLinha;
	}

	public String getLinha() {
		return linha;
	}

	public void setLinha(String linha) {
		this.linha = linha;
	}

	@Override
	public int getNumero() {
		return this.id;
	}

	@Override
	public ElementoGrafo getProximo() {
		return proximo;
	}

	public void setProximo(ElementoGrafo proximo) {
		this.proximo = proximo;
	}

	@Override
	public ElementoGrafo getAnterior() {
		return anterior;
	}

	public void setAnterior(ElementoGrafo anterior) {
		this.anterior = anterior;
	}

}
