package model;

public class SinusoidalTwoHalfSignal extends SinusoidalSignal {
	
	public SinusoidalTwoHalfSignal(String name, double amplitude, int initTime, int duration,
			int samplingFrequency, int period) {
		super(name, amplitude, initTime, duration, samplingFrequency, period);;
	}

	@Override
	protected void calculateValues() {
		values = new double[duration*samplingFrequency];
		for(int i = 0; i < values.length; i++) {
			values[i] = amplitude * Math.abs(Math.sin((2*Math.PI)/period * ((i/(double)samplingFrequency)-initTime)));
		}
	}
}
