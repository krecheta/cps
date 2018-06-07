package model;

import java.awt.image.SampleModel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	
	public void removeSignal(Signal signal) {
		for(Iterator<Signal> it = signals.iterator(); it.hasNext();) {
			if(it.next().getId().equals(signal.getId())) {
				it.remove();
			}
		}    
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
	

	//zapis sygna³u do pliku
	public void saveSignalToFile(Signal signal, String fileName) throws FileNotFoundException {
		String name = signal.getName();
		int initTime = signal.getInitTime();
		int sampligFrequency = signal.getSamplingFrequency();
		double[] values = signal.getValues();
		int numberOfSamples = values.length;
		
		PrintWriter pw = new PrintWriter(fileName);
		pw.println(name);
		pw.println(initTime);
		pw.println(sampligFrequency);
		pw.println(numberOfSamples);
		
		for(int i = 0; i < values.length; i++) {
			pw.printf("%.2f ", values[i]);
		}
		
		pw.close();
	}

	public void loadSignalFromFile(String fileName) throws IOException {
		FileReader fileReader = new FileReader(fileName);
		BufferedReader buffReader = new BufferedReader(fileReader);
		
		String name = buffReader.readLine();
		int initTime = Integer.parseInt(buffReader.readLine());
		int sampplingFrequency = Integer.parseInt(buffReader.readLine());
		int numberOfSamples = Integer.parseInt(buffReader.readLine());
		double values[] = new double[numberOfSamples];
		String[] strValues = buffReader.readLine().replaceAll(",", ".").split(" ");
		
		for(int i = 0; i < values.length; i++) {
			values[i] = Double.parseDouble(strValues[i]);
		}
		
		signals.add(new Signal(name, initTime, sampplingFrequency, values));
		
		buffReader.close();
	}
	
	//zwraca listê sygna³ów z którymi mo¿na wykonaæ operacjê arytmetyczn¹ (czyli ma takie samo próbkowanie)
	public List<Signal> getSignalsToArthmeticOperation(Signal signal) {
		return signals.stream()
				.filter(x -> x.getSamplingFrequency() == signal.getSamplingFrequency())
				.collect(Collectors.toList());
	}
	
	public void signalWeaves(Signal firstSignal,Signal secondSignal, String nameNewSignal) throws Exception{
		Signal newSignal = SignalFilter.signalWeave(firstSignal, secondSignal, nameNewSignal);
		signals.add(newSignal);
	}
	
	
	public void addSignals(Signal sig1, Signal sig2, String name) {
		if(sig1.getInitTime() > sig2.getInitTime()) {
			Signal temp = sig1;
			sig1 = sig2;
			sig2 = temp;
		}
		
		int initTime1 = sig1.getInitTime();
		int initTime2 = sig2.getInitTime();
		
		int endTime1 = initTime1 + sig1.getDuration();
		int endTime2 = initTime2 + sig2.getDuration();
		
		int duration = ((endTime1 > endTime2) ? endTime1 : endTime2)
				- (initTime1);
		
		int samplingFrequency = sig1.getSamplingFrequency();
		double[] result = new double[duration*samplingFrequency];
		
		int initOffset = initTime2 - initTime1;
		int i;
		
		double[] values1 = sig1.getValues();
		double[] values2 = sig2.getValues();
		
		for(i = 0; i < initOffset*samplingFrequency; i++) {
			result[i] = values1[i];
		}
		
		if(endTime1 <= endTime2) {
			for(; i < values1.length; i++) {
				result[i] = values1[i] + values2[i-initOffset*samplingFrequency];
			}
			
			for(; i < result.length; i++) {
				result[i] = values2[i-initOffset*samplingFrequency];
			}
		} else {
			for(; i < values2.length+initOffset*samplingFrequency; i++) {
				result[i] = values1[i] + values2[i-initOffset*samplingFrequency];
			}
			
			for(; i < result.length; i++) {
				result[i] = values1[i];
			}
		}
		
		signals.add(new Signal(name, initTime1, samplingFrequency, result));
	}
	
	public void subSignals(Signal sig1, Signal sig2, String name) {
		boolean flag =  false;
		if(sig1.getInitTime() > sig2.getInitTime()) {
			Signal temp = sig1;
			sig1 = sig2;
			sig2 = temp;
			flag = true;
		}
		
		int initTime1 = sig1.getInitTime();
		int initTime2 = sig2.getInitTime();
		
		int endTime1 = initTime1 + sig1.getDuration();
		int endTime2 = initTime2 + sig2.getDuration();
		
		int duration = ((endTime1 > endTime2) ? endTime1 : endTime2)
				- (initTime1);
		
		int samplingFrequency = sig1.getSamplingFrequency();
		double[] result = new double[duration*samplingFrequency];
		
		int initOffset = initTime2 - initTime1;
		int i;
		
		double[] values1 = sig1.getValues();
		double[] values2 = sig2.getValues();
		
		for(i = 0; i < initOffset*samplingFrequency; i++) {
			if(flag) {
				result[i] = -values1[i];
			} else {
				result[i] = values1[i];
			}
		}
		
		if(endTime1 <= endTime2) {
			for(; i < values1.length; i++) {
				if(flag) {
					result[i] = values2[i-initOffset*samplingFrequency] - values1[i];
				} else {
					result[i] = values1[i] - values2[i-initOffset*samplingFrequency];
				}
			}
			
			for(; i < result.length; i++) {
				if(flag) {
					result[i] = values2[i-initOffset*samplingFrequency];
				} else {
					result[i] = -values2[i-initOffset*samplingFrequency];
				}	
			}
		} else {
			for(; i < values2.length + initOffset*samplingFrequency; i++) {
				if(flag) {
					result[i] = values2[i-initOffset*samplingFrequency] - values1[i];
				} else {
					result[i] = values1[i] - values2[i-initOffset*samplingFrequency];
				}	
			}
			
			for(; i < result.length; i++) {
				if(flag) {
					result[i] = values1[i];
				} else {
					result[i] = -values1[i];
				}
			}
		}
		
		signals.add(new Signal(name, initTime1, samplingFrequency, result));
	}
	
	public void mulSignals(Signal sig1, Signal sig2, String name) {
		if(sig1.getInitTime() > sig2.getInitTime()) {
			Signal temp = sig1;
			sig1 = sig2;
			sig2 = temp;
		}
		
		int initTime1 = sig1.getInitTime();
		int initTime2 = sig2.getInitTime();
		int initOffset = initTime2 - initTime1;
		
		int endTime1 = initTime1 + sig1.getDuration();
		int endTime2 = initTime2 + sig2.getDuration();
		
		double[] values1 = sig1.getValues();
		double[] values2 = sig2.getValues();
		double[] result;
		
		int samplingFrequency = sig1.getSamplingFrequency();
		
		if(endTime1 <= endTime2) {
			result = new double[(sig1.getDuration()-initOffset)*samplingFrequency];
		} else {
			result = new double[sig2.getDuration()*samplingFrequency];	
		}
		
		for(int i = 0; i < result.length; i++) {
			result[i] = values1[i+initOffset*samplingFrequency]*values2[i];
		}
		
		signals.add(new Signal(name, initTime2, samplingFrequency, result));
	}
	
	public void divSignals(Signal sig1, Signal sig2, String name) throws Exception {
		boolean flag = false;
		if(sig1.getInitTime() > sig2.getInitTime()) {
			Signal temp = sig1;
			sig1 = sig2;
			sig2 = temp;
			flag = true;
		}
		
		int initTime1 = sig1.getInitTime();
		int initTime2 = sig2.getInitTime();
		int initOffset = initTime2 - initTime1;
		
		int endTime1 = initTime1 + sig1.getDuration();
		int endTime2 = initTime2 + sig2.getDuration();
		
		double[] values1 = sig1.getValues();
		double[] values2 = sig2.getValues();
		double[] result;
		
		int samplingFrequency = sig1.getSamplingFrequency();
		
		if(endTime1 <= endTime2) {
			result = new double[(sig1.getDuration()-initOffset)*samplingFrequency];
		} else {
			result = new double[sig2.getDuration()*samplingFrequency];
		}
		
		for(int i = 0; i < result.length; i++) {
			if(flag) {
				if(values1[i+initOffset*samplingFrequency] == 0) {
					throw new Exception();
				}
				result[i] = values2[i] / values1[i+initOffset*samplingFrequency];
			} else {
				if(values2[i] == 0) {
					throw new Exception();
				}
				result[i] = values1[i+initOffset*samplingFrequency] / values2[i];
			}
		}
		
		signals.add(new Signal(name, initTime2, samplingFrequency, result));
	}
}
