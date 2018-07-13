package br.unb.cic.experimentoibk;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultadosExpIbk {
	
	@Autowired
	private ResultadoExpIbkRepository repository;

	private Queue<ResultadoExpIbk> fila;

	@PostConstruct
	private void init(){
		this.fila = new LinkedList<>();
	}
	
	public void add(ResultadoExpIbk resultado){
		fila.add(resultado);
	}
	
	public void flush(){
		while(fila.size() > 0){
			ResultadoExpIbk resultado = fila.poll();
			repository.save(resultado);
		}
	}

}
