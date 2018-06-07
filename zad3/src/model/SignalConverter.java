package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.analysis.function.Sinc;

public class SignalConverter {
	
	private Signal original;
	private double[] sampledValues;
	private double samplingFrequency;
	private Map<String, Double> quantizedValues;
	private Map<String, Double> reconstructedValues;
	
	private double mse;
	private double snr;
	private double psnr;
	private double md;
	
	public SignalConverter(Signal original) {
		this.original = original;
	}
	
	public double[] sampleSignal(double frequency) throws Exception {
		
		if(frequency > original.getSamplingFrequency()) {
			throw new Exception("Nieprawid³owa czêstotliwoœæ!");
		}
		if(original.getSamplingFrequency() % frequency != 0) {
			throw new Exception("Nieprawid³owa czêstotliwoœæ!");
		}

		int step = (int) (original.getSamplingFrequency() / frequency);

		double[] values = new double [(int) (frequency*original.getDuration() + 1)];
		for(int i = 0, j = 0; i < values.length-1; i++, j+=step) {
			values[i] = original.getValues()[j];
		}
		
		values[values.length-1] = original.getValues()[original.getValues().length-1];
		sampledValues = values;
		samplingFrequency = frequency;
		
		return values;
	}
	
	public Map<String, Double> quantization(int bits) {
		
		double min = Arrays.stream(sampledValues).min().getAsDouble();
		double max = Arrays.stream(sampledValues).max().getAsDouble();
		double range = max - min;
		double step = range / (Math.pow(2, bits));
		List<Double> steps = new ArrayList<>();
		Map<String, Double> values = new LinkedHashMap<>();
		
		for(double i = min; i < max; i += step) {
			steps.add(i);
		}
		
		for(int i = 0; i < sampledValues.length; i++) {
			String t = String.format("%.2f", i/(double)samplingFrequency + original.getInitTime()); 
			double temp = max;
			double val = 0;
			for(Double s : steps) {
				double distance = Math.abs(s - sampledValues[i]);
				if(distance < temp) {
					temp = distance;
					val = s;
				}
			}
			values.put(t, val);
		}
		quantizedValues = values;
		return values;
	}
	
	public Map<String, Double> extrapolation() {
		
		Map<String, Double> extrapolated = new LinkedHashMap<>();
		double T = 1 / samplingFrequency;
		for(int i = 0; i < original.getValues().length; i++) {
			double t = i / (double)original.getSamplingFrequency() + original.getInitTime();
			double sum = 0;
			for(int j = 0; j < sampledValues.length; j++) {
				double rect = rect( (t - (T / 2.0) - (j * T)) / T );
				sum += sampledValues[j] * rect;
			}
			extrapolated.put(String.format("%.2f", t), sum);
		}
		reconstructedValues = extrapolated;
		return extrapolated;
	}
	
	public Map<String, Double> interpolation(){
		Map<String, Double> interpolated = new LinkedHashMap<>();
		double T = 1 / samplingFrequency;
		
		for(int i = 0; i < original.getValues().length; i++) {
			double t = i / (double)original.getSamplingFrequency() + original.getInitTime();
			double sum = 0;
			for(int j = 0; j < sampledValues.length; j++) {
				double tri = tri( (t - j*T) / T );
				sum += sampledValues[j] * tri;
			}
			interpolated.put(String.format("%.2f", t), sum);
		}
		
		reconstructedValues = interpolated;
		return interpolated;
	}
	
	public Map<String, Double> sinc(){
		Map<String, Double> sincValues = new LinkedHashMap<>();
		double T = 1 / samplingFrequency;
		Sinc sinc = new Sinc(true);
		for(int i = 0; i < original.getValues().length; i++) {
			double t = i / (double)original.getSamplingFrequency() + original.getInitTime();
			double sum = 0;
			for(int j = 0; j < sampledValues.length; j++) {
				sum += sampledValues[j] * sinc.value((t / T) - j);
			}
			sincValues.put(String.format("%.2f", t), sum);
		}
		reconstructedValues = sincValues;
		return sincValues;
	}
	
	public void calculateStatistics() {
		double[] orVal = original.getValues();
		Map<String, Double> origValues = new LinkedHashMap<>();
		for(int j = 0; j < orVal.length; j++) {
			String t = String.format("%.2f", j/(double)original.samplingFrequency + original.getInitTime());
			origValues.put(t, orVal[j]);
		}
		
		Map<String, Double> newValues = reconstructedValues;
		
		//oblicz mse
		double temp = 0;
		
		for(Entry<String, Double> entry : origValues.entrySet()) {
			Double newVal = newValues.get(entry.getKey());
			if(newVal != null) {
				temp += Math.pow(entry.getValue() - newVal, 2);
			} else {
				temp += Math.pow(entry.getValue(), 2);
			}
		}
		mse = temp / (double)newValues.size();
		
		//snr
		double temp2 = 0;
		
		for(Entry<String, Double> entry : origValues.entrySet()) {
			temp2 += Math.pow(entry.getValue(), 2);
		}
	
		snr = 10*Math.log10(temp2/temp);
		
		//psnr
		psnr = 10*Math.log10(origValues.values().stream().mapToDouble(d -> d).max().getAsDouble() / mse);
		
		//md
		List<Double> diffs = new ArrayList<>();
		
		for(Entry<String, Double> entry : origValues.entrySet()) {
			Double newVal = newValues.get(entry.getKey());
			if(newVal != null) {
				diffs.add(Math.abs(entry.getValue() - newVal));
			} else {
				diffs.add(Math.abs(entry.getValue()));
			}
		}
		md = Collections.max(diffs);
	}

	public double getMSE() {
		return mse;
	}
	
	public double getSNR() {
		return snr;
	}
	
	public double getPSNR() {
		return psnr;
	}
	
	public double getMD() {
		return md;
	}
	
	public double[] getSampledValues() {
		return sampledValues;
	}
	
	public double getSamplingFrequency() {
		return samplingFrequency;
	}
	
	private double rect(double t) {
		if(Math.abs(t) > 0.5) {
			return 0;
		} else if(Math.abs(t) == 0.5) {
			return 0.5;
		} else {
			return 1.0;
		}
	}
	
	private double tri(double t) {
		if(Math.abs(t) < 1.0) {
			return 1.0 - Math.abs(t);
		} else {
			return 0.0;
		}
	}
}
