package br.unb.cic.preditorhistorico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.localizacao.Localizacao;
import br.unb.cic.extrator.dominio.localizacao.LocalizacaoRepository;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagem;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagemRepository;
import br.unb.cic.preditor.dominio.instance.InstanceRepository;
import br.unb.cic.preditor.dominio.instance.Instancia;
import br.unb.cic.preditorhistorico.util.Util;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

@Component
public class GeradorDeInstances {

	private static final Logger logger = Logger.getLogger(GeradorDeInstances.class.getName());

	private static final double HORAS_PARA_SEGUNDOS = 3600;
	private static final double MINUTOS_PARA_SEGUNDOS = 60;

	@Value("${spring.datasource.password}")
	private String senha;

	@Value("${spring.datasource.username}")
	private String usuario;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${preditor.quantidadeDeAmostrasParaCalculoDaVelocidadeMedia}")
	private int quantidadeDeAmostrasParaCalculoDaVelocidadeMedia;

	@Autowired
	private TempoViagemRepository tempoViagemRepository;

	@Autowired
	private LocalizacaoRepository localizacaoRepository;

	@Autowired
	private InstanceRepository instanceRepository;

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

	public synchronized Instances getInstancesFromDB(ElementoGrafo elementoGrafo,
			int quantidadeDeTemposDeViagemAnteriores) throws Exception {
		logger.info("Gerar instances de : " + elementoGrafo.getNome());
		logger.info("Popular tabela de instances inicio");
		popularTabelaInstance(elementoGrafo, quantidadeDeTemposDeViagemAnteriores);
		logger.info("Popular tabela de instances fim");
		InstanceQuery query = new InstanceQuery();
		query.setUsername(usuario);
		query.setPassword(senha);
		query.setDatabaseURL(url);
		query.setQuery(getQuery(quantidadeDeTemposDeViagemAnteriores));
		query.setSparseData(true);
		logger.info("Fim gerar instances de : " + elementoGrafo.getNome());
		return query.retrieveInstances();
	}

	public Instances getInstances(ElementoGrafo elementoGrafo, int quantidadeDeTemposDeViagemAnteriores)
			throws Exception {

		logger.info("Gerar instances de : " + elementoGrafo.getNome());
		List<Instancia> instancias = popularInstances(elementoGrafo, quantidadeDeTemposDeViagemAnteriores);

		ArrayList<Attribute> attributes = new ArrayList<>();
		attributes.add(new Attribute("tempo_de_viagem"));
		attributes.add(new Attribute("periodo_do_dia"));
		attributes.add(new Attribute("dia_da_semana"));
		attributes.add(new Attribute("velocidade_media"));
		attributes.add(new Attribute("tempo_de_viagem_1"));
		attributes.add(new Attribute("tempo_de_viagem_2"));
		attributes.add(new Attribute("tempo_de_viagem_3"));
		attributes.add(new Attribute("tempo_de_viagem_4"));
		attributes.add(new Attribute("tempo_de_viagem_5"));
		attributes.add(new Attribute("tempo_de_viagem_6"));

		Instances instances = new Instances("instance_preditor", attributes, instancias.size());

		for (Instancia instancia : instancias) {
			Instance instance = new DenseInstance(attributes.size());
			instance.setValue(0, instancia.getTempoViagem());
			instance.setValue(1, instancia.getPeriodoDoDia());
			instance.setValue(2, instancia.getDiaDaSemana());
			instance.setValue(3, instancia.getVelocidadeMedia());
			instance.setValue(4, instancia.getTempoViagem1());
			instance.setValue(5, instancia.getTempoViagem2());
			instance.setValue(6, instancia.getTempoViagem3());
			instance.setValue(7, instancia.getTempoViagem4());
			instance.setValue(8, instancia.getTempoViagem5());
			instance.setValue(9, instancia.getTempoViagem6());

			instances.add(instance);
		}

		logger.info("Fim gerar instances de : " + elementoGrafo.getNome());
		return instances;
	}

	private String getQuery(int quantidadeDeTemposDeViagemAnteriores) {
		StringBuffer sb = new StringBuffer();
		sb.append("select tempo_de_viagem, periodo_do_dia, dia_da_semana, velocidade_media");
		for (int i = 0; i < quantidadeDeTemposDeViagemAnteriores; i++) {
			sb.append(", tempo_de_viagem_");
			sb.append(i + 1);
		}
		sb.append(" from instance_preditor order by id asc");
		return sb.toString();
	}

	private void popularTabelaInstance(ElementoGrafo elementoGrafo, int quantidadeDeTemposDeViagemAnteriores) {
		instanceRepository.deleteAll();
		List<TempoViagem> temposDeViagem = tempoViagemRepository
				.findByNomeAndProcessadoAndTempoNotNullOrderByDataHoraDesc(elementoGrafo.getNome(), false);
		for (int i = 0; i < temposDeViagem.size(); i++) {
			TempoViagem tempoDeViagem = temposDeViagem.get(i);

			long idTempoViagem = tempoDeViagem.getId();
			double tempo = tempoDeViagem.getTempo();
			double periodoDoDia = getPeriodoDoDia(tempoDeViagem);
			int diaDaSemana = getDiaDaSemana(tempoDeViagem);
			List<Double> temposDeViagemAnteriores = getTemposDeViagemAnteriores(temposDeViagem, i, quantidadeDeTemposDeViagemAnteriores);
			double velocidadeMedia = getVelocidadeMedia(tempoDeViagem);

			Instancia instance = new Instancia();
			instance.setIdTempoViagem(idTempoViagem);
			instance.setTempoViagem(tempo);
			instance.setPeriodoDoDia(periodoDoDia);
			instance.setDiaDaSemana(diaDaSemana);
			instance.setTemposDeViagemAnteriores(temposDeViagemAnteriores);
			instance.setVelocidadeMedia(velocidadeMedia);

			instanceRepository.save(instance);
		}

	}

	private List<Instancia> popularInstances(ElementoGrafo elementoGrafo, int quantidadeDeTemposDeViagemAnteriores) {
		List<Instancia> instancias = new ArrayList<>();
		List<TempoViagem> temposDeViagem = tempoViagemRepository
				.findByNomeAndProcessadoAndTempoNotNullOrderByDataHoraDesc(elementoGrafo.getNome(), false);

		for (int i = 0; i < temposDeViagem.size(); i++) {
			TempoViagem tempoDeViagem = temposDeViagem.get(i);

			long idTempoViagem = tempoDeViagem.getId();
			double tempo = tempoDeViagem.getTempo();
			double periodoDoDia = getPeriodoDoDia(tempoDeViagem);
			int diaDaSemana = getDiaDaSemana(tempoDeViagem);
			List<Double> temposDeViagemAnteriores = getTemposDeViagemAnteriores(temposDeViagem, i, quantidadeDeTemposDeViagemAnteriores);
			double velocidadeMedia = getVelocidadeMedia(tempoDeViagem);

			Instancia instancia = new Instancia();
			instancia.setIdTempoViagem(idTempoViagem);
			instancia.setTempoViagem(tempo);
			instancia.setPeriodoDoDia(periodoDoDia);
			instancia.setDiaDaSemana(diaDaSemana);
			instancia.setTemposDeViagemAnteriores(temposDeViagemAnteriores);
			instancia.setVelocidadeMedia(velocidadeMedia);

			instancias.add(instancia);
		}

		return instancias;

	}

	private double getVelocidadeMedia(TempoViagem tempoDeViagem) {
		double velocidadeMedia;
		List<Localizacao> localizacoesAnteriores = getLocalizacoesAnteriores(tempoDeViagem.getOnibus(),
				tempoDeViagem.getDataHora());

		velocidadeMedia = util.calculaMedia(localizacoesAnteriores);
		if (velocidadeMedia == 0) {
			velocidadeMedia = getUltimaLocalizacao(tempoDeViagem.getOnibus(), tempoDeViagem.getDataHora())
					.getVelocidade();
		}
		return velocidadeMedia;
	}

	private List<Localizacao> getLocalizacoesAnteriores(String onibus, Date data) {

		if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 6) {
			return localizacaoRepository.findTop6ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(onibus, data);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 5) {
			return localizacaoRepository.findTop5ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(onibus, data);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 4) {
			return localizacaoRepository.findTop4ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(onibus, data);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 3) {
			return localizacaoRepository.findTop3ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(onibus, data);
		} else if (quantidadeDeAmostrasParaCalculoDaVelocidadeMedia == 2) {
			return localizacaoRepository.findTop2ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(onibus, data);
		} else {
			return localizacaoRepository.findTop1ByOnibusAndDataHoraLessThanOrderByDataHoraDesc(onibus, data);
		}
	}

	private Localizacao getUltimaLocalizacao(String onibus, Date data) {
		return localizacaoRepository.findTop1ByOnibusAndDataHoraLessThanEqualOrderByDataHoraDesc(onibus, data);
	}

	private List<Double> getTemposDeViagemAnteriores(List<TempoViagem> temposDeViagem, int i, int quantidadeDeTemposDeViagemAnteriores) {
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

}
