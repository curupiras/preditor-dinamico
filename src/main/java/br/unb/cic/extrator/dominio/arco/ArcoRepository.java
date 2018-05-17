package br.unb.cic.extrator.dominio.arco;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArcoRepository extends JpaRepository<Arco, Long> {

	Arco findById(long id);

}
