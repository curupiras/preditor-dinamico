package br.unb.cic.controladorsimulacao;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Resultados {
	
	@Autowired
	private ResultadoRepository resultadoRepository;

	private Queue<Resultado> fila;

	@PostConstruct
	private void init(){
		this.fila = new LinkedList<>();
	}
	
	public void add(Resultado resultado){
		fila.add(resultado);
	}
	
	public void flush(){
		while(fila.size() > 0){
			Resultado resultado = fila.poll();
			resultadoRepository.save(resultado);
		}
	}

}
