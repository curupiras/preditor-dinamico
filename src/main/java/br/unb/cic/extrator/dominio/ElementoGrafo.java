package br.unb.cic.extrator.dominio;

public abstract class ElementoGrafo {

	public abstract int getNumero();

	public abstract String getNome();

	public abstract ElementoGrafo getProximo();

	public abstract ElementoGrafo getAnterior();

	@Override
	public String toString() {
		return getAnterior().getNome() +"-"+getNome()+"-"+getProximo().getNome();
	}
	
	

}
