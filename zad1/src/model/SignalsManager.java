package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SignalsManager {
	private ObservableList<Signal> signals;
	private int bins;
	
	public SignalsManager() {
		signals = FXCollections.observableArrayList();
	}
	
	public ObservableList<Signal> getSignals(){
		return signals;
	}
	
	public void addSignal(Signal newSignal) {
		signals.add(newSignal);
	}
	
	public void setHistogramBins(int bins) {
		this.bins = bins;
	}
	
	//metoda obliczaj¹ca dane dla histogramu
	public Map<String, Integer> getHistogramData(Signal signal) {
		//kontener na dane do histogramu, przechowujemy tylko pocz¹tek ka¿dego przedzia³u
		Map<String, Integer> histogramData = new LinkedHashMap<>();
		//rozpiêtoœæ jednego przedzia³u histogramu
		double interval = (signal.getMaxvalue() - signal.getMinValue()) / (double)bins;
		//licznik
		int counter = 0;
		//wartosci sygnalu
		double[] values = signal.getValues();

		for(double i = signal.getMinValue(); i < signal.getMaxvalue(); i += interval) {
			for(int j = 0; j < values.length; j++) {
				if(values[j] >= i && values[j] < i + interval) {
					counter++;
				}
			}
			int index = String.valueOf(i).indexOf('.');
			 
			histogramData.put(String.valueOf(i).substring(0, index+2), counter);
			counter = 0;
		}
		
		return histogramData;
	}
}
