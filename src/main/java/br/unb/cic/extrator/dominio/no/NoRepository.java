package br.unb.cic.extrator.dominio.no;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoRepository extends JpaRepository<No, Long> {

	No findById(long id);

}
