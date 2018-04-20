package model;

import java.util.Random;

//szum o rozk³adzie jednostajnym
public class UniformDistributionNoise extends Signal {

	public UniformDistributionNoise(String name, double amplitude, int initTime, int duration, int samplingFrequency) {
		super(name, amplitude, initTime, duration, samplingFrequency);
		calculateValues();
		calculateStatistics();
	}

	private void calculateValues() {
		values = new double[duration*samplingFrequency];
		Random gen = new Random();
		
		for(int i = 0; i < values.length; i++) {
			values[i] = (gen.nextDouble() * 2 - 1) * amplitude;
		}
	}

}
