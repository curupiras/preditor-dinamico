package br.unb.cic.extrator.dominio.localizacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

	Localizacao findById(long id);
	
	List<Localizacao> findByOnibusAndProcessadoOrderByDataHoraAsc(String onibus, boolean processado);
	
	@Query("select distinct onibus from Localizacao where processado = false")
	List<String> findDistinctOnibus();

}