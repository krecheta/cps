package model;

public class UnitJumpSignal extends Signal {
	
	private double jumpTime;
	
	public UnitJumpSignal(String name, double amplitude, int initTime, int duration, 
			int samplingFrequency, double jumpTime) {
		super(name, amplitude, initTime, duration, samplingFrequency);
		this.jumpTime = jumpTime;
		calculateValues();
		calculateStatistics();
	}

	private void calculateValues() {
		values = new double[duration*samplingFrequency];
		double t;
		for(int i = 0; i < values.length; i++) {
			t = i/(double)samplingFrequency;
			if(t < jumpTime-initTime) {
				values[i] = 0;
			} else if(t == jumpTime-initTime) {
				values[i] = 0.5*amplitude;
			} else {
				values[i] = amplitude;
			}
		}

	}

}
