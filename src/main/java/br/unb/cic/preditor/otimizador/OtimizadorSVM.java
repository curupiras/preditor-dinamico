package br.unb.cic.preditor.otimizador;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.functions.supportVector.RegSMOImproved;
import weka.classifiers.meta.GridSearch;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

@Component
public class OtimizadorSVM {

	@Value("${otimizador.caminho.resultado}")
	private String resultadoPath;

	@Value("${otimizador.instances}")
	private String[] instances;

	// @Value("${otimizador.options}")
	// private String options;

	// @Value("${otimizador.gridsearch.options}")
	// private String gridSearchOptions;

	@Value("${otimizador.smoreg.options}")
	private String smoRegOptions;

	@Value("${otimizador.regsmoimproved.options}")
	private String regSmoImprovedOptions;

	@Value("${otimizador.rbfkernel.options}")
	private String rbfKernelOptions;

	@Value("${otimizador.gridsearch.xbase}")
	private double xBase;

	@Value("${otimizador.gridsearch.xexpression}")
	private String xExpression;

	@Value("${otimizador.gridsearch.xmax}")
	private double xMax;

	@Value("${otimizador.gridsearch.xmin}")
	private double xMin;

	@Value("${otimizador.gridsearch.xProperty}")
	private String xProperty;

	@Value("${otimizador.gridsearch.xstep}")
	private double xStep;

	@Value("${otimizador.gridsearch.ybase}")
	private double yBase;

	@Value("${otimizador.gridsearch.yexpression}")
	private String yExpression;

	@Value("${otimizador.gridsearch.ymax}")
	private double yMax;

	@Value("${otimizador.gridsearch.ymin}")
	private double yMin;

	@Value("${otimizador.gridsearch.yproperty}")
	private String yProperty;

	@Value("${otimizador.gridsearch.ystep}")
	private double yStep;

	@Value("${otimizador.gridsearch.debug}")
	private boolean debug;

	@Value("${otimizador.gridsearch.evaluation}")
	private String evaluation;

	@Value("${otimizador.gridsearch.logfile}")
	private String logFile;

	@Value("${otimizador.gridsearch.numexecutionsslots}")
	private int numExecutionsSlots;

	@Value("${otimizador.gridsearch.samplesizepercent}")
	private double sampleSizePercent;

	@Value("${otimizador.gridsearch.seed}")
	private int seed;

	@Value("${otimizador.gridsearch.transversal}")
	private String transversal;

	private static final long TRINTA_DIAS = 2592000000L;
	private static final Logger logger = Logger.getLogger(OtimizadorSVM.class);

	@Scheduled(fixedDelay = TRINTA_DIAS)
	public void otimizarSVM() {

		for (String instance : instances) {

			String nomeArquivo = resultadoPath + instance;
			DataSource source;
			Instances instances;
			String[] splitedOptions;

			try {
				source = new DataSource(nomeArquivo);
				instances = source.getDataSet();
				instances.setClassIndex(0);

				RBFKernel rbfKernel = new RBFKernel();
				splitedOptions = Utils.splitOptions(rbfKernelOptions);
				rbfKernel.setOptions(splitedOptions);

				RegSMOImproved regSMOImproved = new RegSMOImproved();
				splitedOptions = Utils.splitOptions(regSmoImprovedOptions);
				regSMOImproved.setOptions(splitedOptions);

				SMOreg smoReg = new SMOreg();
				splitedOptions = Utils.splitOptions(smoRegOptions);
				smoReg.setOptions(splitedOptions);
				smoReg.setRegOptimizer(regSMOImproved);
				smoReg.setKernel(rbfKernel);

				GridSearch gridSearch = new GridSearch();
				// splitedOptions = Utils.splitOptions(gridSearchOptions);
				// gridSearch.setOptions(splitedOptions);
				gridSearch.setClassifier(smoReg);
				gridSearch.setXBase(xBase);
				gridSearch.setXExpression(xExpression);
				gridSearch.setXMax(xMax);
				gridSearch.setXMin(xMin);
				gridSearch.setXProperty(xProperty);
				gridSearch.setXStep(xStep);
				gridSearch.setYBase(yBase);
				gridSearch.setYExpression(yExpression);
				gridSearch.setYMax(yMax);
				gridSearch.setYMin(yMin);
				gridSearch.setYProperty(yProperty);
				gridSearch.setYStep(yStep);
				gridSearch.setDebug(debug);

				SelectedTag st = null;
				if (evaluation.equals("MAE")) {
					st = new SelectedTag(GridSearch.EVALUATION_MAE, GridSearch.TAGS_EVALUATION);
				}

				gridSearch.setEvaluation(st);
				gridSearch.setLogFile(new File(logFile));
				gridSearch.setNumExecutionSlots(numExecutionsSlots);
				gridSearch.setSampleSizePercent(sampleSizePercent);
				gridSearch.setSeed(seed);

				SelectedTag stt = null;
				if (transversal.equals("COLUMN-WISE")) {
					stt = new SelectedTag(GridSearch.TRAVERSAL_BY_COLUMN, GridSearch.TAGS_TRAVERSAL);
				}
				gridSearch.setTraversal(stt);

				logger.info("Inicio do buildClassifier");
				gridSearch.buildClassifier(instances);
				logger.info("Fim do buildClassifier");

				logger.info(gridSearch.getBestClassifier().toString());
				logger.info("Criteria: " + gridSearch.getEvaluation().getSelectedTag().getID());
				logger.info("Results: " + gridSearch.getValues());

			} catch (Exception e) {
				logger.error("Erro ao carregar instances.", e);
			}

		}

	}

}
