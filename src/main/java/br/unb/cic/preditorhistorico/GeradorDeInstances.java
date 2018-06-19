package br.unb.cic.preditorhistorico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.localizacao.LocalizacaoRepository;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagem;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagemRepository;
import br.unb.cic.preditor.dominio.instance.Instance;
import br.unb.cic.preditorhistorico.util.Util;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.gui.experiment.GeneratorPropertyIteratorPanel;

@Component
public class GeradorDeInstances {

	private static final double HORAS_PARA_SEGUNDOS = 3600;
	private static final double MINUTOS_PARA_SEGUNDOS = 60;

	@Value("${spring.datasource.password}")
	private String senha;

	@Value("${spring.datasource.username}")
	private String usuario;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${preditor.quantidadeDeTemposDeViagemAnteriores}")
	private int quantidadeDeTemposDeViagemAnteriores;

	@Value("${preditor.quantidadeDeAmostrasParaCalculoDaVelocidadeMedia}")
	private int quantidadeDeAmostrasParaCalculoDaVelocidadeMedia;

	@Autowired
	private TempoViagemRepository tempoViagemRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private Util util;

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
		popularTabelaInstance(elementoGrafo);

		InstanceQuery query = new InstanceQuery();
		query.setUsername(usuario);
		query.setPassword(senha);
		query.setDatabaseURL(url);
		query.setQuery("select datahora, tempo from tempo_viagem_preditor where nome = '" + elementoGrafo.getNome()
				+ "' order by datahora asc");
		query.setSparseData(true);
		return query.retrieveInstances();
	}

	private void popularTabelaInstance(ElementoGrafo elementoGrafo) {
		List<TempoViagem> temposDeViagem = tempoViagemRepository.findByNomeOrderByDataHoraDesc(elementoGrafo.getNome());
		for (int i = 0; i < temposDeViagem.size(); i++) {
			TempoViagem tempoDeViagem = temposDeViagem.get(i);

			long idTempoViagem = tempoDeViagem.getId();
			double periodoDoDia = getPeriodoDoDia(tempoDeViagem);
			int diaDaSemana = getDiaDaSemana(tempoDeViagem);
			List<Double> temposDeViagemAnteriores = getTemposDeViagemAnteriores(temposDeViagem, i);
			double velocidadeMedia = getVelocidadeMedia(temposDeViagem, i);

			System.out.println(velocidadeMedia);
		}

	}

	private double getVelocidadeMedia(TempoViagem tempoDeViagem) {
		// TODO:Gravar Onibus no tempo de Viagem e recupera Velocidades
		// Anteriores do Banco
		List<Double> velocidadesAnteriores = localizacaoRepository
				.findTop6ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(tempoDeViagem.getOnibus(),
						tempoDeViagem.getDataHora());
		return util.calculaMedia(velocidadesAnteriores);
	}

	private List<Double> getTemposDeViagemAnteriores(List<TempoViagem> temposDeViagem, int i) {
		List<Double> temposDeViagemAnteriores = new ArrayList<>();

		for (int j = 1; j <= quantidadeDeTemposDeViagemAnteriores; j++) {
			if (i + j == temposDeViagem.size()) {
				break;
			}
			temposDeViagemAnteriores.add((double) temposDeViagem.get(i + j).getTempo());
		}

		return temposDeViagemAnteriores;
	}

	private int getDiaDaSemana(TempoViagem tempoDeViagem) {
		Date dataHora = tempoDeViagem.getDataHora();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataHora);

		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	private double getPeriodoDoDia(TempoViagem tempoViagem) {
		Date dataHora = tempoViagem.getDataHora();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataHora);

		double horas = calendar.get(Calendar.HOUR_OF_DAY);
		double minutos = calendar.get(Calendar.MINUTE);
		double segundos = calendar.get(Calendar.SECOND);

		return (horas * HORAS_PARA_SEGUNDOS + minutos * MINUTOS_PARA_SEGUNDOS + segundos) / (HORAS_PARA_SEGUNDOS);
	}

	// public Instances getInstancesFromDB(ElementoGrafo elementoGrafo) throws
	// Exception {
	// InstanceQuery query = new InstanceQuery();
	// query.setUsername(usuario);
	// query.setPassword(senha);
	// query.setDatabaseURL(url);
	// query.setQuery("select datahora, tempo from tempo_viagem_preditor where
	// nome = '" + elementoGrafo.getNome()
	// + "' order by datahora asc");
	// query.setSparseData(true);
	// return query.retrieveInstances();
	// }
}
