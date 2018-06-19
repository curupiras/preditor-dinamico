package br.unb.cic.preditor.dominio.instance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

	Instance findById(long id);

}
