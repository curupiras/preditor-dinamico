package br.unb.cic.extrator.dominio.arco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.vividsolutions.jts.geom.Geometry;

import br.unb.cic.extrator.dominio.ElementoGrafo;

@Entity
@Table(name = "arco")
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

}
