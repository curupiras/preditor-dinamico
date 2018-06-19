package br.unb.cic.extrator.dominio.tempoviagem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TempoViagemRepository extends JpaRepository<TempoViagem, Long> {

	TempoViagem findById(long id);
	List<TempoViagem> findByNomeOrderByDataHoraDesc(String nome);

}
