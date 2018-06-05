package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.UUID;

public class Signal {
	//id
	UUID id;
	//nazwa
	String name;
	//amplituda
	protected double amplitude;
	//czas pocz�tkowy
	protected int initTime;
	//czas trwania
	protected int duration;
	//cz�stotliwo�� pr�bkowania
	protected int samplingFrequency;
	
	//warto�ci sygna�u
	protected double[] values;
	//warto�� �rednia
	protected double avgValue;
	//warto�� �rednia bezwzgl�dna
	protected double avgAbsValue;
	//moc �rednia
	protected double avgPower;
	//wariancja
	protected double variance;
	//warto�� skuteczna
	protected double effectiveValue;
	
	protected Signal(String name, double amplitude, int initTime, int duration, int samplingFrequency) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.amplitude = amplitude;
		this.initTime = initTime;
		this.duration = duration;
		this.samplingFrequency = samplingFrequency;
	}
	
	public Signal(String name, int initTime, int sampplingFrequency, double[] values) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.initTime = initTime;
		this.duration = values.length / sampplingFrequency;
		this.samplingFrequency = sampplingFrequency;
		this.values = values;
		
		double max = Arrays.stream(values).max().getAsDouble();
		double min = Math.abs(Arrays.stream(values).min().getAsDouble());
		
		this.amplitude = ( max > min ) ? max : min;
		
		calculateStatistics();
	}
	
	//obliczanie statystyk sygna�u
	protected void calculateStatistics() {
		avgValue = Arrays.stream(values).sum() / (double)(values.length);
		avgAbsValue = Arrays.stream(values).map(Math::abs).sum() / (double)(values.length);
		avgPower = Arrays.stream(values).map( i -> Math.pow(i, 2) ).sum() / (double)(values.length);
		variance = Arrays.stream(values).map( i -> Math.pow(i-avgValue, 2) ).sum() / (double)(values.length);
		effectiveValue = Math.sqrt(avgPower);
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public double getAmplitude() {
		return amplitude;
	}
	
	public int getInitTime() {
		return initTime;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public int getSamplingFrequency() {
		return samplingFrequency;
	}
	
	public double[] getValues() {
		return values;
	}
	
	public int getMinValue() {
		return (int)Arrays.stream(values).min().getAsDouble();
	}
	
	public int getMaxvalue() {
		return (int)Arrays.stream(values).max().getAsDouble();
	}
	
	public String getPeriod() {
		return "-";
	}
	
	public String getFillFactory() {
		return "-";
	}
	
	public double getAvgValue() {
		return round(avgValue);
	}

	public double getAvgAbsValue() {
		return round(avgAbsValue);
	}

	public double getAvgPower() {
		return round(avgPower);
	}

	public double getVariance() {
		return round(variance);
	}

	public double getEffectiveValue() {
		return round(effectiveValue);
	}

	@Override
	public String toString() {
		return name;
	}
	
	public double round(double value) {
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(3, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
