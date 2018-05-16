package br.unb.cic.extrator.onibus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import br.unb.cic.simuladortrafego.onibus.DtoFrota;
import br.unb.cic.simuladortrafego.onibus.DtoOnibus;

@Component
public class LocalizacaoDao {

	private Connection conn;

	private static final Log logger = LogFactory.getLog(LocalizacaoDao.class);

	public long insereLocalizacao(DtoOnibus dtoOnibus) {
		long chave = 0;

		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/gerenciador";
			this.conn = DriverManager.getConnection(url, "postgres", "curup1ras");

			PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO localizacao_preditor (datahora, ordem, linha, geo_pto, velocidade, processado) "
							+ "values (?, ?, ?, st_geomfromtext('POINT('||?||' '||?||' )'), ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			ps.setTimestamp(1, getTimeStamp(dtoOnibus.getDataHora()));
			ps.setString(2, dtoOnibus.getNome());
			ps.setString(3, dtoOnibus.getLinha());
			ps.setString(4, dtoOnibus.getLongitude());
			ps.setString(5, dtoOnibus.getLatitude());
			ps.setDouble(6, dtoOnibus.getVelocidade());
			ps.setBoolean(7, false);

			ps.execute();

			ResultSet rs = ps.getGeneratedKeys();

			if (rs.next()) {
				chave = rs.getInt(1);
			}

			ps.close();
			conn.close();

		} catch (

		Exception e) {
			logger.error("Erro ao tentar inserir localização no banco de dados.", e);
		}

		return chave;
	}
	
	public void insereLocalizacao(DtoFrota dtoFrota) {
		List<DtoOnibus> lista = dtoFrota.getFrota();
		for (DtoOnibus dtoOnibus : lista) {
			insereLocalizacao(dtoOnibus);
		}
	}

	private static Timestamp getTimeStamp(String datahora) {
		
		DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date data = null;

		try {
			data = new Date(fmt.parse(datahora).getTime());
		} catch (ParseException e) {
			logger.error("Erro ao tentar converter data.");
		}

		return new Timestamp(data.getTime());
	}
}
