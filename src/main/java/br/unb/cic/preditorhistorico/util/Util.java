package br.unb.cic.preditorhistorico.util;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Util {

	public double calculaMedia(List<Double> lista) {
		double sum = 0;
		if (!lista.isEmpty()) {
			for (double item : lista) {
				sum += item;
			}
			return sum / lista.size();
		}
		return sum;
	}
}
