package br.unb.cic.preditor.dominio.instance;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

@Scope("prototype")
public interface InstanceRepository extends JpaRepository<Instancia, Long> {

	Instancia findById(long id);

}
