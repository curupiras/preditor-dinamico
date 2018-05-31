package br.unb.cic.preditorhistorico;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

@Component
public class GeradorDeInstances {

	@Value("${spring.datasource.password}")
	private String senha;

	@Value("${spring.datasource.username}")
	private String usuario;

	@Value("${spring.datasource.url}")
	private String url;

	public Instances getInstancesFromDB() throws Exception {
		InstanceQuery query = new InstanceQuery();
		query.setUsername(usuario);
		query.setPassword(senha);
		query.setDatabaseURL(url);
		query.setQuery("select datahora, tempo from tempo_viagem_preditor order by datahora asc");
		query.setSparseData(true);
		return query.retrieveInstances();
	}

	public Instances getInstancesFromDB(ElementoGrafo elementoGrafo) throws Exception {
		InstanceQuery query = new InstanceQuery();
		query.setUsername(usuario);
		query.setPassword(senha);
		query.setDatabaseURL(url);
		query.setQuery("select datahora, tempo from tempo_viagem_preditor where nome = '" + elementoGrafo.getNome()
				+ "' order by datahora asc");
		query.setSparseData(true);
		return query.retrieveInstances();
	}
}
