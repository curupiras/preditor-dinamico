package br.unb.cic.extrator.dominio.tempoviagem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TempoViagemRepository extends JpaRepository<TempoViagem, Long> {

	TempoViagem findById(long id);
	List<TempoViagem> findByNomeAndProcessadoAndTempoNotNullOrderByDataHoraDesc(String nome, boolean processado);
	
	@Modifying
	@Transactional
	@Query("update TempoViagem tempoViagem set tempoViagem.processado =  true where tempoViagem.nome = ?1")
	void updateProcessado(String nome);

}
