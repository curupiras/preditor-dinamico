package br.unb.cic.preditorhistorico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;
import br.unb.cic.extrator.dominio.localizacao.Localizacao;
import br.unb.cic.extrator.dominio.localizacao.LocalizacaoRepository;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagem;
import br.unb.cic.extrator.dominio.tempoviagem.TempoViagemRepository;
import br.unb.cic.preditor.dominio.instance.Instance;
import br.unb.cic.preditor.dominio.instance.InstanceRepository;
import br.unb.cic.preditorhistorico.util.Util;
import weka.core.Instances;
import weka.experiment.InstanceQuery;

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

//	@Value("${preditor.quantidadeDeTemposDeViagemAnteriores}")
	private int quantidadeDeTemposDeViagemAnteriores;

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

	public Instances getInstancesFromDB(ElementoGrafo elementoGrafo, int quantidadeDeTemposDeViagemAnteriores) throws Exception {
		this.quantidadeDeTemposDeViagemAnteriores = quantidadeDeTemposDeViagemAnteriores;
		popularTabelaInstance(elementoGrafo);
		InstanceQuery query = new InstanceQuery();
		query.setUsername(usuario);
		query.setPassword(senha);
		query.setDatabaseURL(url);
		query.setQuery(getQuery());
		query.setSparseData(true);
		return query.retrieveInstances();
	}

	private String getQuery() {
		StringBuffer sb = new StringBuffer();
		sb.append("select tempo_de_viagem, periodo_do_dia, dia_da_semana, velocidade_media");
		for (int i = 0; i < quantidadeDeTemposDeViagemAnteriores; i++) {
			sb.append(", tempo_de_viagem_");
			sb.append(i + 1);
		}
		sb.append(" from instance_preditor order by id asc");
		return sb.toString();
	}

	private void popularTabelaInstance(ElementoGrafo elementoGrafo) {
		instanceRepository.deleteAll();
		List<TempoViagem> temposDeViagem = tempoViagemRepository.findByNomeAndTempoNotNullOrderByDataHoraDesc(elementoGrafo.getNome());
		for (int i = 0; i < temposDeViagem.size(); i++) {
			TempoViagem tempoDeViagem = temposDeViagem.get(i);

			long idTempoViagem = tempoDeViagem.getId();
			long tempo = tempoDeViagem.getTempo();
			double periodoDoDia = getPeriodoDoDia(tempoDeViagem);
			int diaDaSemana = getDiaDaSemana(tempoDeViagem);
			List<Double> temposDeViagemAnteriores = getTemposDeViagemAnteriores(temposDeViagem, i);
			double velocidadeMedia = getVelocidadeMedia(tempoDeViagem);

			Instance instance = new Instance();
			instance.setIdTempoViagem(idTempoViagem);
			instance.setTempoViagem(tempo);
			instance.setPeriodoDoDia(periodoDoDia);
			instance.setDiaDaSemana(diaDaSemana);
			instance.setTemposDeViagemAnteriores(temposDeViagemAnteriores);
			instance.setVelocidadeMedia(velocidadeMedia);

			instanceRepository.save(instance);
		}

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
		return localizacaoRepository.findTop1ByOnibusAndDataHoraOrderByDataHoraDesc(onibus, data);
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

}
