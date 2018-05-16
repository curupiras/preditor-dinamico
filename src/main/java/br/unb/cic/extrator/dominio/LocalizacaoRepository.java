package br.unb.cic.extrator.dominio;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {

	Localizacao findById(long id);

}
