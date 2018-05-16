package br.unb.cic.extrator.dominio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "localizacao_preditor")
public class Localizacao {

	private static final Log logger = LogFactory.getLog(Localizacao.class);

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	@Column(name = "datahora")
	private Date dataHora;

	@Column(name = "ordem")
	private String nome;

	@Column(name = "linha")
	private String linha;

	@Column(name = "geo_pto")
	private Point geoPto;

	@Column(name = "velocidade")
	private double velocidade;

	@Column(name = "processado")
	private boolean processado;

	@Transient
	private String latitude;

	@Transient
	private String longitude;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@JsonProperty("DATAHORA")
	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Object dataHora) {

		if (dataHora.getClass().equals(String.class)) {
			DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			try {
				this.dataHora = new Date(fmt.parse((String) dataHora).getTime());
			} catch (ParseException e) {
				logger.error("Erro ao tentar converter data.");
			}
		} else {
			this.dataHora = (Date) dataHora;
		}
	}

	@JsonProperty("ORDEM")
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@JsonProperty("LINHA")
	public String getLinha() {
		return linha;
	}

	public void setLinha(String linha) {
		this.linha = linha;
	}

	public Point getGeoPto() {
		return geoPto;
	}

	public void setGeoPto(Point geoPto) {
		this.geoPto = geoPto;
	}

	@JsonProperty("VELOCIDADE")
	public double getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}

	public boolean isProcessado() {
		return processado;
	}

	public void setProcessado(boolean processado) {
		this.processado = processado;
	}

	@JsonProperty("LATITUDE")
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;

		if (this.longitude != null && geoPto == null) {
			double bLongitude = Double.parseDouble(this.longitude);
			double bLatitude = Double.parseDouble(this.latitude);
			GeometryFactory geometryFactory = new GeometryFactory();
			this.geoPto = geometryFactory.createPoint(new Coordinate(bLongitude, bLatitude));
		}
	}

	@JsonProperty("LONGITUDE")
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;

		if (this.latitude != null && geoPto == null) {
			double bLongitude = Double.parseDouble(this.longitude);
			double bLatitude = Double.parseDouble(this.latitude);
			GeometryFactory geometryFactory = new GeometryFactory();
			this.geoPto = geometryFactory.createPoint(new Coordinate(bLongitude, bLatitude));
		}
	}

}
