package br.unb.cic.preditorhistorico.resultados;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import br.unb.cic.extrator.dominio.ElementoGrafo;

@Component
public class GravadorResultados {

	private static final String SEPARADOR_DE_LINHAS = "\n";

	private static final Object[] CABECALHO = { "CC", "MAE", "RMSE", "RAE", "RRSE", "TNI" };

	private static final Logger logger = Logger.getLogger(GravadorResultados.class.getName());

	public void escreverResultado(ElementoGrafo elementoGrafo, List<String> resultado) {

		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(SEPARADOR_DE_LINHAS);
		String nomeArquivo = getNomeArquivo(elementoGrafo);

		try {
			boolean arquivoExiste = Files.exists(Paths.get(nomeArquivo));
			fileWriter = new FileWriter(nomeArquivo,true);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			if (!arquivoExiste) {
				csvFilePrinter.printRecord(CABECALHO);
			}
			csvFilePrinter.printRecord(resultado);

		} catch (Exception e) {
			logger.error("Erro no CsvFileWriter", e);
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				logger.error("Erro no flush/close do fileWriter/csvPrinter", e);
			}
		}
	}

	private static String getNomeArquivo(ElementoGrafo elementoGrafo) {
		StringBuffer sb = new StringBuffer();
		sb.append("resultado-");
		sb.append(elementoGrafo.getNome());
		sb.append(".csv");
		return sb.toString();
	}
}
