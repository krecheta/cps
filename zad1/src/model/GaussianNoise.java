package model;

import java.util.Random;

//szum gausowski
public class GaussianNoise extends Signal {

	public GaussianNoise(String name, double amplitude, int initTime, int duration, int samplingFrequency) {
		super(name, amplitude, initTime, duration, samplingFrequency);
		calculateValues();
		calculateStatistics();
	}

	@Override
	protected void calculateValues() {
		values = new double[duration*samplingFrequency];
		Random gen = new Random();
		
		for(int i = 0; i < values.length; i++) {
			values[i] = gen.nextGaussian()*1+0;
		}
	}
}
