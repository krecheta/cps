package model;

import java.util.Random;

public class PulseNoise extends Signal {
	
	private double propability;
	
	public PulseNoise(String name, double amplitude, int initTime, int duration, 
			int samplingFrequency, double propability) {
		super(name, amplitude, initTime, duration, samplingFrequency);
		this.propability = propability;
		calculateValues();
		calculateStatistics();
	}

	@Override
	protected void calculateValues() {
		values = new double[samplingFrequency*duration];
		Random ran = new Random();
		for(int i = 0; i < values.length; i++) {
			if(ran.nextDouble() <= propability) {
				values[i] = amplitude;
			} else {
				values[i] = 0;
			}
		}
	}

}
