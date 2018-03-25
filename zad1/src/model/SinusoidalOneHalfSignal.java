package model;

//sygna³ sinusoidalny wyprostowany jednopo³ówkowo
public class SinusoidalOneHalfSignal extends SinusoidalSignal {
	
	public SinusoidalOneHalfSignal(String name, double amplitude, int initTime, int duration,
			int samplingFrequency, int period) {
		super(name, amplitude, initTime, duration, samplingFrequency, period);
	}

	@Override
	protected void calculateValues() {
		values = new double[duration*samplingFrequency];
		for(int i = 0; i < values.length; i++) {
			double d = (2*Math.PI)/period * ((i/(double)samplingFrequency)-initTime);
			values[i] = 0.5*amplitude * (Math.sin(d) + Math.abs(Math.sin(d)));
		}
	}
}
